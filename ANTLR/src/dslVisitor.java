import org.antlr.v4.runtime.misc.ParseCancellationException;
import java.io.*;
import java.util.*;

public class dslVisitor<T> extends CSVScriptBaseVisitor<T> {
    private ArrayList<String[][]> inFiles = new ArrayList<>(); //List of all our inFiles
    private int inFilesSize = 0;
    private Hashtable<String, Integer> fileVarListIdx = new Hashtable<>(); //Maps each file's variable name to one of our array tables

    private Hashtable<String, Integer> rowHeaders = new Hashtable<>();
    private Hashtable<String, Integer> colHeaders = new Hashtable<>();

    private class Subset {
        LinkedList<int[]> cellLocations;
        String fileVar;
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

    @Override
    public T visitInputStatement(CSVScriptParser.InputStatementContext ctx) {
        /*
        Probably will have to do more coding to handle headers vs noheaders.
        Make another 2 maps that map colHeader to column index and rowHeader to row index
         */
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
        if (ctx.getChildCount() == 0) return null;
        return visit(ctx.inputFlag());
    }

    @Override
    public T visitInputFlag(CSVScriptParser.InputFlagContext ctx) {
        boolean[] inputflags = {false, false};
        if (! ctx.getTokens(8).isEmpty()) inputflags[0] = true; //Get noRowHeader flag
        if (! ctx.getTokens(9).isEmpty()) inputflags[1] = true; //Get noColHeader flag
        T a = (T) inputflags;
        return (T) inputflags;
    }

    @Override
    public T visitSubsetAssignment(CSVScriptParser.SubsetAssignmentContext ctx) {
        String file = ctx.ID(0).toString();
        String subsetToken = ctx.ID(1).toString();

        return null;
    }

}