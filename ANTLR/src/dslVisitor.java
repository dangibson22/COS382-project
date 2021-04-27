import org.antlr.v4.runtime.misc.ParseCancellationException;
import java.io.*;
import java.util.*;

public class dslVisitor<T> extends CSVScriptBaseVisitor<T> {
    private final HashMap<String, String[][]> inFiles = new HashMap<>(); //List of all our inFiles

    private final HashMap<String, Integer> rowHeaders = new HashMap<>();
    private final HashMap<String, Integer> colHeaders = new HashMap<>();

    private final HashMap<String, Subset> subsetVars = new HashMap<>();
    private final HashMap<String, Cell> cellVars = new HashMap<>();
    private final HashMap<String, Float> numVars = new HashMap<>();
    private final HashMap<String, CSVScriptParser.ExprContext> functionContexts = new HashMap();
    private final HashMap<String, CSVScriptParser.RContext> schemeContexts = new HashMap();


    //private final HashMap<String, Scheme> schemeVars = new HashMap<>();

    private String currentInFile;
    private String loopControlVar;
    private int[] loopLoc;
    private Cell currentCell;
    private Float funcExprVal;

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
        public void setList(LinkedList<int[]> list) {
            cellLocations = list;
        }
        public LinkedList<int[]> getCellLocations() {
            return this.cellLocations;
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

        public float getVal() {
            return Float.parseFloat(inFiles.get(fileVar)[rowCol[0]][rowCol[1]]);
        }
    }

    /*
    private class Scheme {
        private HashMap<String, Float> schemeValues;
        private HashMap<String, String> schemeOps;

        public Scheme() {
            schemeValues = new HashMap<>();
            schemeOps = new HashMap<>();
        }

        public void addSubset(String subset, Float value, String op) {
            schemeValues.put(subset, value);
            schemeOps.put(subset, op);
        }

        public Float getValue(String subset) {
            return schemeValues.get(subset);
        }

        public String getOp(String subset) {
            return schemeOps.get(subset);
        }

        public LinkedList<String> getSubsets() {
            LinkedList<String> subsetNames = new LinkedList<>();
            for (String name : schemeValues.keySet()) {
                subsetNames.push(name);
            }
            return subsetNames;
        }

    }

     */

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
    private String indexHeader(String oldHeader, HashMap<String, Integer> headerTable) {
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
                if (data.length == 0) { //Fills empty row with empty strings
                    data = new String[rowColSize[1]];
                    for (int i=0; i<rowColSize[1]; i++) {
                        data[i] = "";
                    }
                }
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
            inFiles.put(fileVarName, spreadsheet);
            myReader.close();
        } catch (FileNotFoundException e) {
            throw new ParseCancellationException(e);
            //Figure out how to tell people which line error is on
        }
        return null;
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
        temp.setList( (LinkedList<int[]>) visitSet(ctx.set()) );
        subsetVars.put(ctx.ID(1).getText(), temp);
        return null;
    }

    @Override
    public T visitSet(CSVScriptParser.SetContext ctx) {
        LinkedList<int[]> cellLocs = (LinkedList<int[]>) visitReferences(ctx.references()); //Set a value to this
        //printCellLocContents(cellLocs, currentInFile);

        return (T) cellLocs;
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
        if(ctx.references() != null) {
            currentCells = (LinkedList<int[]>) visitReferences(ctx.references());
            for (int[] loc : currentCells) {
                if (!alreadyInList(cellLocs, loc)) {
                    cellLocs.add(loc);
                }
            }
        }
        return (T) cellLocs;
    }

    @Override
    public T visitReference(CSVScriptParser.ReferenceContext ctx) {
        LinkedList<int[]> cellLocs = new LinkedList<>();
        //int fileIdx = fileVarListIdx.get(currentInFile);
        boolean isRange = ctx.getToken(19, 0) != null;
        if (!isRange) {
            if (ctx.getToken(17, 0) != null) { //row reference
                int row = rowHeaders.get(ctx.ID().getText());
                int maxCols = inFiles.get(currentInFile)[row].length;
                int maxRow = row;
                for (int col = 0; col < maxCols; col++) {
                    int[] rowCol = {row, col};
                    cellLocs.add(rowCol);
                }
            } else if (ctx.getToken(18, 0) != null) { //col reference
                int col = colHeaders.get(ctx.ID().getText());
                int maxRows = inFiles.get(currentInFile).length;
                for (int row = 0; row < maxRows; row++) {
                    int[] rowCol = {row, col};
                    cellLocs.add(rowCol);
                }
            } else { //cell reference
                visit(ctx.cellReference());
                int[] rowCol = (int[]) visit(ctx.cellReference());
                cellLocs.add(rowCol);
            }
        }
        else {
            LinkedList<int[]> list1 = (LinkedList<int[]>) visitReference(ctx.reference(0));
            LinkedList<int[]> list2 = (LinkedList<int[]>) visitReference(ctx.reference(1));

            if (list1.size() != list2.size()) throw new ParseCancellationException("Ranges must be same type");
            //Add exception for range of row to col

            for (int i=0; i<list2.size(); i++) {
                int[] firstLoc = list1.get(i);
                int[] secondLoc = list2.get(i);
                int colStart = firstLoc[1];
                int colEnd = secondLoc[1]+1;
                int rowStart = firstLoc[0];
                int rowEnd = secondLoc[0]+1;
                if(firstLoc[0] == secondLoc[0]) {//row are the same
                    if(firstLoc[1] > secondLoc[1]) {
                        colStart = secondLoc[1];
                        colEnd = firstLoc[1]+1;
                    }
                    for (int col=colStart; col<colEnd; col++) {
                        int[] rowCol = {firstLoc[0], col};
                        cellLocs.add(rowCol);
                    }
                }
                else if(firstLoc[1] == secondLoc[1]) { //cols are the same
                    if(firstLoc[0] > secondLoc[0]) {
                        rowStart = secondLoc[1];
                        rowEnd = firstLoc[1]+1;
                    }
                    for (int row = rowStart; row<rowEnd; row++) {
                        int[] rowCol = {row, firstLoc[1]};
                        cellLocs.add(rowCol);
                    }
                }
                else {
                    if(firstLoc[1] > secondLoc[1]) {
                        colStart = secondLoc[1];
                        colEnd = firstLoc[1]+1;
                    }
                    if(firstLoc[0] > secondLoc[0]) {
                        rowStart = secondLoc[1];
                        rowEnd = firstLoc[1]+1;
                    }
                    for (int row=rowStart; row<rowEnd; row++) {
                        for (int col=colStart; col<colEnd; col++) {
                            int[] rowCol = {row, col};
                            cellLocs.add(rowCol);
                        }
                    }
                }
            }

        }

        return (T) cellLocs;
    }

    @Override
    public T visitCellReference(CSVScriptParser.CellReferenceContext ctx) { //Sets the current cell and returns location
        if(ctx.ID(1) == null) {
            Cell loc = cellVars.get(ctx.ID(0).getText());
            currentCell = loc;
            int[] rowCol = loc.rowCol;
            return (T) rowCol;
        }
        else { // 3 IDs, therefore file.col.row
            int row = rowHeaders.get(ctx.ID(2).getText());
            int col = colHeaders.get(ctx.ID(1).getText());
            String file = ctx.ID(0).getText();
            currentCell = new Cell(file, row, col);
            int[] rowCol = {row, col};
            return (T) rowCol;
        }
    }

    @Override
    public T visitCellAssignment(CSVScriptParser.CellAssignmentContext ctx) {
        String cellName = ctx.ID().getText();
        if (numVars.containsKey(cellName)) throw new ParseCancellationException();
        visit(ctx.cellReference());
        cellVars.put(cellName, currentCell);
        return null;
    }

    @Override
    public T visitNumAssignment(CSVScriptParser.NumAssignmentContext ctx) {
        String numName = ctx.ID().getText();

        if (cellVars.containsKey(numName)) throw new ParseCancellationException();

        if (ctx.opFunc() != null) {
            float val = (float) visitOpFunc(ctx.opFunc());
            numVars.put(numName, val);
        }
        else if (ctx.INT() != null) {
            float val = Float.parseFloat(ctx.INT().getText());
            numVars.put(numName, val);
        } else {
            float val = Float.parseFloat(ctx.realNumber().getText());
            numVars.put(numName, val);
        }
        for (String s : numVars.keySet()) {
            System.out.println("key: " + s + " val: " + numVars.get(s));
        }
        return null;
    }

    @Override
    public T visitOpFunc(CSVScriptParser.OpFuncContext ctx) {
        Float returnVal = 0.0f;
        if (ctx.getToken(24, 0) != null) {
            // avg
            String subsetName = ctx.ID().toString();
            if (!subsetVars.containsKey(subsetName)) throw new ParseCancellationException();

            Subset s = subsetVars.get(subsetName);
            Float maxVal = (Float) visitValue(ctx.value());
            Float sum = 0.0f;
            int count = 0;
            String[][] file = inFiles.get(currentInFile);
            for (int[] loc : s.getCellLocations()) {
                try {
                    String val = file[loc[0]][loc[1]];
                    Float curr = Float.parseFloat(val);
                    sum += curr / maxVal;
                    count++;
                } catch (NumberFormatException e) {
                    // in case string isn't a float val
                }
            }
            Float finalVal = sum / (float) count;
            returnVal = finalVal;
        }
        else if (ctx.getToken(22, 0) != null) {
            // max
            String subsetName = ctx.ID().toString();
            if (!subsetVars.containsKey(subsetName)) throw new ParseCancellationException();

            Float max = 0.0f;
            Subset s = subsetVars.get(subsetName);
            String[][] file = inFiles.get(currentInFile);
            for (int[] loc : s.getCellLocations()) {
                // 0 = row, 1 = col
                try {
                    String val = file[loc[0]][loc[1]];
                    Float curr = Float.parseFloat(val);
                    if (curr > max) {
                        max = curr;
                    }
                } catch (NumberFormatException e) {
                    // in case string isn't a float val
                }
            }
            returnVal = max;
        }
        else if (ctx.getToken(23, 0) != null) {
            // min
            String subsetName = ctx.ID().toString();
            if (!subsetVars.containsKey(subsetName)) throw new ParseCancellationException();

            Float min = Float.MAX_VALUE;
            Subset s = subsetVars.get(subsetName);
            String[][] file = inFiles.get(currentInFile);
            for (int[] loc : s.getCellLocations()) {
                // 0 = row, 1 = col
                try {
                    String val = file[loc[0]][loc[1]];
                    Float curr = Float.parseFloat(val);
                    if (curr < min) {
                        min = curr;
                    }
                } catch (NumberFormatException e) {
                    // in case string isn't a float val
                }
            }
            returnVal = min;
        } else throw new ParseCancellationException();

        return (T) returnVal;
    }



    @Override
    public T visitValue(CSVScriptParser.ValueContext ctx) {
        if (ctx.INT() != null) {
            return (T) (Float) Float.parseFloat(ctx.INT().getText());
        }
        if (ctx.realNumber() != null) {
            return (T) (Float) Float.parseFloat(ctx.realNumber().getText());
        }
        /*
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
        */
        return null;
    }

    @Override
    public T visitIfStatement(CSVScriptParser.IfStatementContext ctx) {
        if ( (boolean) visit(ctx.conditional()) ) {
            visit(ctx.actionBlock());
        }
        return null;
    }

    @Override
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

    @Override
    public T visitFunctionAssignment(CSVScriptParser.FunctionAssignmentContext ctx) {
        String functionId = ctx.ID().getText();
        functionContexts.put(functionId, ctx.expr());
        visit(ctx.expr());
        return null;
    }

    @Override
    public T visitExpr(CSVScriptParser.ExprContext ctx) {
        Float term = (Float) visit(ctx.term());
        CSVScriptParser.FunctionAssignmentContext c = null;
        if (ctx.getParent() instanceof CSVScriptParser.FunctionAssignmentContext) {
            term = funcExprVal;
        }
        if (ctx.terms() == null) {
            return (T) term;
        }
        Float retVal;
        String addOp = ctx.getChild(1).getText();
        if (addOp.equals("+")) {
            retVal = term + (Float) visit(ctx.terms());
        }
        else if (addOp.equals("-")) {
            retVal = term - (Float) visit(ctx.terms());
        }
        else {
            throw new ParseCancellationException();
        }
        return (T) retVal;
    }

    @Override
    public T visitTerms(CSVScriptParser.TermsContext ctx) {
        Float term = (Float) visit(ctx.term());
        if (ctx.terms() == null) {
            return (T) term;
        }
        Float retVal;
        String addOp = ctx.getChild(1).getText();
        if (addOp.equals("+")) {
            retVal = term + ( (Float) visit(ctx.terms()) );
        }
        else if (addOp.equals("-")) {
            retVal = term - (Float) visit(ctx.terms());
        }
        else {
            throw new ParseCancellationException();
        }
        return (T) retVal;
    }

    @Override
    public T visitTerm(CSVScriptParser.TermContext ctx) {
        Float retVal = (Float) visit(ctx.factor());
        if (ctx.factors() == null) {
            return (T) retVal;
        }
        String multOp = ctx.getChild(1).getText();
        if (multOp.equals("*")) {
            retVal = retVal * (Float) visit(ctx.factors());
        }
        else if (multOp.equals("/")) {
            retVal = retVal / (Float) visit(ctx.factors());
        }
        return (T) retVal;
    }

    @Override
    public T visitFactors(CSVScriptParser.FactorsContext ctx) {
        Float retVal = (Float) visit(ctx.factor());
        if (ctx.factors() == null) {
            return (T) retVal;
        }
        String multOp = ctx.getChild(1).getText();
        if (multOp.equals("*")) {
            retVal = retVal * (Float) visit(ctx.factors());
        }
        else if (multOp.equals("/")) {
            retVal = retVal / (Float) visit(ctx.factors());
        }
        return (T) retVal;
    }

    @Override
    public T visitFactor(CSVScriptParser.FactorContext ctx) {
        if (ctx.expr() != null) {
            return visit(ctx.expr());
        }
        else if (ctx.value() != null) {
            return visit(ctx.value());
        }
        else {
            visit(ctx.cellReference());
            int row = currentCell.rowCol[0];
            int col = currentCell.rowCol[1];
            Float retVal = Float.parseFloat(inFiles.get(currentCell.fileVar)[row][col]);
            //System.out.println(retVal);
            return (T) retVal;
        }
    }

    @Override
    public T visitOutputRule(CSVScriptParser.OutputRuleContext ctx) {
        String rule = ctx.ID(0).toString();
        String subsetName = ctx.ID(1).toString();
        if (!functionContexts.containsKey(rule)) throw new ParseCancellationException();
        if (!subsetVars.containsKey(subsetName)) throw new ParseCancellationException();
        CSVScriptParser.ExprContext rule_ctx = functionContexts.get(rule);
        Subset set = subsetVars.get(subsetName);
        for (int[] loc : set.getCellLocations()) {
            try {
                String[][] file = inFiles.get(currentInFile);
                String valStr = file[loc[0]][loc[1]];
                funcExprVal = Float.parseFloat(valStr);
                Float newVal = (Float) visitExpr(rule_ctx);
                file[loc[0]][loc[1]] = newVal.toString();
            } catch (NumberFormatException e) {
                // pass
            }

        }

        return null;
    }

    private void printCellLocContents(LinkedList<int[]> list, String file) {
        System.out.println(Arrays.deepToString(inFiles.get(file)));
        for (int[] i : list) {
            String s = inFiles.get(file)[i[0]][i[1]];
            System.out.printf("%d, %d, %s\n", i[0], i[1], s);
        }
    }
}