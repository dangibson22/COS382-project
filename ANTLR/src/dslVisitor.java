import org.antlr.v4.runtime.misc.ParseCancellationException;
import java.io.*;
import java.util.*;

public class dslVisitor<T> extends CSVScriptBaseVisitor<T> {
    private ArrayList<String[][]> inFiles = new ArrayList<>(); //List of all our inFiles
    private int inFilesSize = 0;
    private Hashtable<String, Integer> fileVarListIdx = new Hashtable<>(); //Maps each file's variable name to one of our array tables

    private Hashtable<String, Integer> rowHeaders = new Hashtable<>();
    private Hashtable<String, Integer> colHeaders = new Hashtable<>();

    private Hashtable<String, Subset> subsetVars = new Hashtable<>();
    private Hashtable<String, Cell> cellVars = new Hashtable<>();

    private String currentInFile;

    private class Subset {
        private LinkedList<int[]> cellLocations;
        public String fileVar;

        Subset(String file) {
            fileVar = file;
        }

        public void addCell(int row, int col) {
            int[] locations = {row, col};
            cellLocations.add(locations);
        }
    }
    private class Cell {
        public String fileVar;
        public int[] rowCol = new int[2];

        Cell(String file, int row, int col) {
            fileVar = file;
            rowCol[0] = row;
            rowCol[1] = col;
        }
    }

    private int[] getRowColSize(File spreadsheet) throws FileNotFoundException {
        int[] rowColSize = {1, 1}; //[rowSize, colSize]
        Scanner reader = new Scanner(spreadsheet);
        String line = reader.nextLine();
        //Get the maximum number of columns
        for (int i=0; i<line.length(); i++) {
            if (line.charAt(i) == ',') {
                rowColSize[1] ++;
            }
        }
        //Get amount of rows
        while (reader.hasNextLine()) {
            line = reader.nextLine();
            if(line.length() > 0) rowColSize[0]++;
        }
        return rowColSize;
    }

    private String scrubHeader(String oldHeader) {
        StringBuilder fixingString = new StringBuilder();
        if (! Character.isDigit(oldHeader.charAt(0)) ) { //Make sure first char is alphabetical or _
            fixingString.append(oldHeader.charAt(0));
        }
        for (int i=1; i<oldHeader.length(); i++) { //Make sure rest of characters are valid [0-9A-Za-z_]
            char c = oldHeader.charAt(i);
            if (Character.isDigit(c) | Character.isAlphabetic(c) | c == '_') {
                fixingString.append(c);
            }
        }
        return fixingString.toString();
    }
    private String indexHeader(String oldHeader, Hashtable<String, Integer> headerTable) {
        String header=oldHeader;
        if ( headerTable.get(header) != null ) { //Indexes headers of the same name.
            int suffix = 2;
            while ( colHeaders.get(header + "_" + suffix) != null ) {
                suffix++;
            }
            header = header + "_" + suffix;
        }
        return header;
    }

    private Boolean alreadyInList(LinkedList<int[]> list, int[] rowCol) {
        for (int[] i : list) {
            if (i[0] == rowCol[0] & i[1] == rowCol[1]) {
                return true;
            }
        }
        return false;
    }

    @Override
    public T visitInputStatement(CSVScriptParser.InputStatementContext ctx) {
        if (ctx.getChildCount() == 0) return null; //Parser matched epsilon rule
        String filename = (String) visit(ctx.filename());
        boolean[] inputflags = (boolean[]) visit(ctx.inputFlags()); //first bool is noRowHeader, second is noColHeader

        try {
            File inFile = new File(filename);
            Scanner myReader = new Scanner(inFile);
            int[] rowColSize = getRowColSize(inFile);
            String[][] spreadsheet = new String[rowColSize[0]][rowColSize[1]];
            //Initialize the array
            if (inputflags == null || !inputflags[1]) { //Col headers exist
                String line = myReader.nextLine();
                String[] data = line.split(",");
                for (int i=0; i<data.length; i++) {
                    String header;
                    if (data[i] != null && !data[i].equals("") ) {
                        header = scrubHeader(data[i]);
                    } else {
                        header = "col_" + i;
                    }
                    header = indexHeader(header, colHeaders);
                    colHeaders.put(header, i);
                }
                spreadsheet[0] = data;
            }
            int row = 1;
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                String[] data = line.split(",");
                // Skip over null rows such as: ",,,,,"
                // if (data.length > 0) {
                if (inputflags == null || !inputflags[0]) { //row headers exist
                    String header;
                    if (data.length > 0 && data[0] != null && !data[0].equals("")) {
                        header = scrubHeader((data[0]));
                    } else {
                        header = "row_" + row;
                    }
                    header = indexHeader(header, rowHeaders);
                    rowHeaders.put(header, row);
                }
                spreadsheet[row] = data;
                row++;
                // }
            }
            //Add array to stored data
            String fileVarName = ctx.ID().toString();
            inFiles.add(spreadsheet);
            fileVarListIdx.put(fileVarName, inFilesSize);
            inFilesSize ++;

            myReader.close();
        } catch (FileNotFoundException e) {
            throw new ParseCancellationException(e);
            //Figure out how to tell people which line error is on
        }

        return visit(ctx.inputStatement());
    }

    @Override
    public T visitFilename(CSVScriptParser.FilenameContext ctx) {
        return (T) ctx.getText();
    }

    @Override
    public T visitInputFlags(CSVScriptParser.InputFlagsContext ctx) {
        boolean[] inputflags = {false, false};
        if (ctx.getChildCount() == 0) return (T) inputflags;
        return visit(ctx.inputFlag());
    }

    @Override
    public T visitInputFlag(CSVScriptParser.InputFlagContext ctx) {
        boolean[] inputflags = {false, false};
        if (! ctx.getTokens(8).isEmpty()) inputflags[0] = true; //Get noRowHeader flag
        if (! ctx.getTokens(9).isEmpty()) inputflags[1] = true; //Get noColHeader flag
        return (T) inputflags;
    }

    @Override
    public T visitSubsetAssignment(CSVScriptParser.SubsetAssignmentContext ctx) {
        String file = ctx.ID(0).toString();
        currentInFile = file;
        Subset temp = new Subset(file);
        visitSet(ctx.set());

        return null;
    }

    @Override
    public T visitSet(CSVScriptParser.SetContext ctx) {
        LinkedList<int[]> cellLocs = (LinkedList<int[]>) visitReferences(ctx.references()); //Set a value to this
        System.out.println(cellLocs.toString());
        return null;
    }

    @Override
    public T visitReferences(CSVScriptParser.ReferencesContext ctx) {
        LinkedList<int[]> cellLocs = new LinkedList<>();
        LinkedList<int[]> currentCells = (LinkedList<int[]>) visitReference(ctx.reference()); //Set this to a variable
        for (int[] loc : currentCells) {
            if (!alreadyInList(cellLocs, loc)) {
                cellLocs.add(loc);
            }
        }
        return (T) cellLocs;
    }

    @Override
    public T visitReference(CSVScriptParser.ReferenceContext ctx) {
        LinkedList<int[]> cellLocs = new LinkedList<>();
        int fileIdx = fileVarListIdx.get(currentInFile);
        boolean isRange = ctx.getToken(19, 0) != null;
        if (!isRange) {
            if (ctx.getToken(17, 0) != null) { //row reference
                int row = rowHeaders.get(ctx.ID().getText());
                int maxCols = inFiles.get(fileIdx)[row].length;
                int maxRow = row;
                for (int col = 0; col < maxCols; col++) {
                    int[] rowCol = {row, col};
                    cellLocs.add(rowCol);
                }
            } else if (ctx.getToken(18, 0) != null) { //col reference
                int col = colHeaders.get(ctx.ID().getText());
                int maxRows = inFiles.get(fileIdx).length;
                for (int row = 0; row < maxRows; row++) {
                    int[] rowCol = {row, col};
                    cellLocs.add(rowCol);
                }
            } else { //cell reference
                int[] rowCol = (int[]) visit(ctx.cellReference());
                cellLocs.add(rowCol);
            }
        }
        else {
            LinkedList<int[]> list1 = (LinkedList<int[]>) visitReference(ctx.reference(0));
            LinkedList<int[]> list2 = (LinkedList<int[]>) visitReference(ctx.reference(1));
            for (int i=0; i<list2.size(); i++) {
                int[] firstLoc = list1.get(i);
                int[] secondLoc = list2.get(i);
                    for (int row = firstLoc[0]; row < secondLoc[0]; row++) { //case first list row < second list row
                        //Do stuff
                    }
            }

        }

        return (T) cellLocs;
    }

    @Override
    public T visitCellReference(CSVScriptParser.CellReferenceContext ctx) {
        if(ctx.ID(1) != null) { // 2 IDs, therefore col.row
            int row = rowHeaders.get(ctx.ID(1).getText());
            int col = colHeaders.get(ctx.ID(0).getText());
            int[] rowCol = {row, col};
            return (T) rowCol;
        }
        else {
            Cell loc = cellVars.get(ctx.ID(0).getText());
            int[] rowCol = loc.rowCol;
            return (T) rowCol;
        }
    }

    @Override
    public T visitValue(CSVScriptParser.ValueContext ctx) {
        if (ctx.INT() != null) {
            return (T) ctx.INT().getText();
        }
        if (ctx.realNumber() != null) {
            return (T) ctx.realNumber().getText();
        }
        if (ctx.ID() != null) {
            if (ctx.ID().getText().equals("value")) {
                //Lookup key for current cell which will be set elsewhere
                //get value in current cell
            }
            else {
                //Lookup id in symbol table
            }
            return null;
        }
        return null;
    }

    @Override
    public T visitIfStatement(CSVScriptParser.IfStatementContext ctx) {
        if ( (boolean) visit(ctx.conditional()) ) {
            visit(ctx.actionBlock());
        }
        return null;
    }

    public T visitConditional(CSVScriptParser.ConditionalContext ctx) {
        if(!ctx.value().isEmpty()) {
            if (ctx.OPERATOR().getText().equals(">=") ) {
                Boolean condition = Float.parseFloat((String) visit(ctx.value(0))) >= Float.parseFloat((String) visit(ctx.value(1)));
                return (T) condition;
            }
            if (ctx.OPERATOR().getText().equals("<=") ) {
                Boolean condition = Float.parseFloat((String) visit(ctx.value(0))) <= Float.parseFloat((String) visit(ctx.value(1)));
                return (T) condition;
            }
            if (ctx.OPERATOR().getText().equals(">") ) {
                Boolean condition = Float.parseFloat((String) visit(ctx.value(0))) > Float.parseFloat((String) visit(ctx.value(1)));
                return (T) condition;
            }
            if (ctx.OPERATOR().getText().equals("<") ) {
                Boolean condition = Float.parseFloat((String) visit(ctx.value(0))) < Float.parseFloat((String) visit(ctx.value(1)));
                return (T) condition;
            }
            if (ctx.OPERATOR().getText().equals("==") ) {
                Boolean condition = Float.parseFloat((String) visit(ctx.value(0))) == Float.parseFloat((String) visit(ctx.value(1)));
                return (T) condition;
            }
            if (ctx.OPERATOR().getText().equals("!=") ) {
                Boolean condition = Float.parseFloat((String) visit(ctx.value(0))) == Float.parseFloat((String) visit(ctx.value(1)));
                return (T) condition;
            }
        }
        else {
            if (ctx.AND() != null) {
                Boolean condition = (boolean) visit(ctx.conditional(0)) & (boolean) visit(ctx.conditional(1));
                return (T) condition;
            }
            if (ctx.OR() != null) {
                Boolean condition = (boolean) visit(ctx.conditional(0)) | (boolean) visit(ctx.conditional(1));
                return (T) condition;
            }
        }
        return null;
    }

}