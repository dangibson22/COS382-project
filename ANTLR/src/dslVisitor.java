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
    private final HashMap<String, CSVScriptParser.ExprContext> functionContexts = new HashMap<>();
    //private final HashMap<String, CSVScriptParser.RContext> schemeContexts = new HashMap();

    //private final HashMap<String, Scheme> schemeVars = new HashMap<>();

    private String currentInFile;
    private String loopControlVar;
    private int[] loopLoc;
    private Cell currentCell;
    private Cell valCell;
    private Float funcExprVal;
    private boolean noRowHeader;
    private boolean noColHeader;

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
        public String rowHead;
        public String colHead;

        Cell(String file, int row, int col) {
            fileVar = file;
            rowCol[0] = row;
            rowCol[1] = col;
            if (!noRowHeader) rowHead = inFiles.get(file)[row][0];
            if (!noColHeader) colHead = inFiles.get(file)[0][col];
        }

        public float getVal() throws NumberFormatException{
            return Float.parseFloat(inFiles.get(fileVar)[rowCol[0]][rowCol[1]]);
        }

        public void setHeaders(String rowHead, String colHead) {
            this.rowHead = rowHead;
            this.colHead = colHead;
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
        int[] rowColSize = {1, 1}; //[rowCount, colCount]
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
            int[] rowColCount = getRowColSize(inFile);
            String[][] spreadsheet = new String[rowColCount[0]][rowColCount[1]];
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
                if (data.length < rowColCount[0]) { //Fills empty row with empty strings
                    int oldLen = data.length;
                    data = Arrays.copyOf(data, rowColCount[1]);
                    for (int i=oldLen; i<rowColCount[1]; i++) {
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
            System.err.printf("File '%s' not found on line %d\n", filename, ctx.start.getLine());
            System.exit(1);
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
        if (! ctx.getTokens(8).isEmpty()) {
            inputflags[0] = true; //Get noRowHeader flag
            noRowHeader = true;
        }
        if (! ctx.getTokens(9).isEmpty()) {
            inputflags[1] = true; //Get noColHeader flag
            noColHeader = true;
        }
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
                if (rowHeaders.get(ctx.ID().getText()) == null) {
                    System.err.printf("Row '%s' does not exist (line %d)\n", ctx.ID().getText(), ctx.start.getLine());
                    System.exit(1);
                }
                int row = rowHeaders.get(ctx.ID().getText());
                int maxCols = inFiles.get(currentInFile)[row].length;
                int maxRow = row;
                int colStart = 1;
                if (noRowHeader) colStart = 0;
                for (int col = colStart; col < maxCols; col++) {
                    int[] rowCol = {row, col};
                    cellLocs.add(rowCol);
                }
            } else if (ctx.getToken(18, 0) != null) { //col reference
                if (colHeaders.get(ctx.ID().getText()) == null) {
                    System.err.printf("Col '%s' does not exist (line %d)\n", ctx.ID().getText(), ctx.start.getLine());
                    System.exit(1);
                }
                int col = colHeaders.get(ctx.ID().getText());
                int maxRows = inFiles.get(currentInFile).length;
                int rowStart = 1;
                if (noColHeader) rowStart = 0;
                for (int row = rowStart; row < maxRows; row++) {
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

            if (list1.size() != list2.size()) {
                System.err.printf("Ranges must be same type (line %d)", ctx.start.getLine());
                System.exit(1);
            }
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
        if (ctx.THIS() != null) {
            if (ctx.getToken(17, 0) != null) { //Row token found, use valCell's row.
                int row = rowHeaders.get(valCell.rowHead);
                int col = colHeaders.get(ctx.ID(0).getText());
                currentCell = new Cell(valCell.fileVar, row, col);
                int[] rowCol = {row, col};
                return (T) rowCol;
            }
            else { //use valCell's col
                int row = rowHeaders.get(ctx.ID(0).getText());
                int col = colHeaders.get(valCell.colHead);
                currentCell = new Cell(valCell.fileVar, row, col);
                int[] rowCol = {row, col};
                return (T) rowCol;
            }
        }
        else if(ctx.ID(1) == null) {
            Cell loc = cellVars.get(ctx.ID(0).getText());
            if (loc == null) System.err.printf("cell '%s' does not exist (line %d)", ctx.ID(0).getText(), ctx.start.getLine());
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
        if (numVars.containsKey(cellName)) {
            System.err.printf("Variable '%s' already exists as a num (Line %d)", cellName, ctx.start.getLine());
            System.exit(1);
        }
        visit(ctx.cellReference());
        cellVars.put(cellName, currentCell);
        return null;
    }

    @Override
    public T visitNumAssignment(CSVScriptParser.NumAssignmentContext ctx) {
        String numName = ctx.ID().getText();

        if (cellVars.containsKey(numName)) {
            System.err.printf("Variable '%s' already exists as a cell (Line %d)", numName, ctx.start.getLine());
            System.exit(1);
        }

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
            //System.out.println("key: " + s + " val: " + numVars.get(s));
        }
        return null;
    }

    @Override
    public T visitOpFunc(CSVScriptParser.OpFuncContext ctx) {
        Float returnVal = 0.0f;
        if (ctx.getToken(24, 0) != null) {
            // avg
            String subsetName = ctx.ID().toString();
            if (!subsetVars.containsKey(subsetName)) {
                System.err.printf("subset '%s' does not exist (line %d)", subsetName, ctx.start.getLine());
                System.exit(1);
            };

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
            if (!subsetVars.containsKey(subsetName)) {
                System.err.printf("subset '%s' does not exist (line %d)", subsetName, ctx.start.getLine());
                System.exit(1);
            }

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
            if (!subsetVars.containsKey(subsetName)) {
                System.err.printf("subset '%s' does not exist (line %d)", subsetName, ctx.start.getLine());
                System.exit(1);
            }

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
        return null;
    }

    @Override
    public T visitExpr(CSVScriptParser.ExprContext ctx) {
        Float retVal = (Float) visit(ctx.term());
        CSVScriptParser.FunctionAssignmentContext c = null;
        if (ctx.expr() == null) {
            return (T) retVal;
        }
        String addOp = ctx.getChild(1).getText();
        if (addOp.equals("+")) {
            retVal = (Float) visit(ctx.expr()) + retVal;
        }
        else if (addOp.equals("-")) {
            retVal = (Float) visit(ctx.expr()) - retVal;
        }
        else {
            throw new ParseCancellationException();
        }
        return (T) retVal;
    }

    @Override
    public T visitTerm(CSVScriptParser.TermContext ctx) {
        Float retVal = (Float) visit(ctx.factor());
        if (ctx.term() == null) {
            return (T) retVal;
        }
        String multOp = ctx.getChild(1).getText();
        if (multOp.equals("*")) {
            retVal = (Float) visit(ctx.term()) * retVal;
        }
        else if (multOp.equals("/")) {
            retVal = (Float) visit(ctx.term()) / retVal;
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
        else if (ctx.VAL() != null) {
            String cellValStr = inFiles.get(valCell.fileVar)[valCell.rowCol[0]][valCell.rowCol[1]];
            return (T) (Float) Float.parseFloat(cellValStr);
        }
        else {
            return visit(ctx.variable());
        }
    }

    @Override
    public T visitVariable(CSVScriptParser.VariableContext ctx) {
        if (ctx.ID() != null) {
            String varName = ctx.ID().getText();
            if (subsetVars.get(varName) != null) {
                System.err.printf("variable '%s' is invalid type: subset (line %d)", varName, ctx.start.getLine());
                System.exit(1);
            }
            else if (numVars.get(varName) != null) {
                return (T) numVars.get(varName);
            }
            else {
                System.err.printf("Variable '%s' does not exist (line %d)", varName, ctx.start.getLine());
                System.exit(0);
            }
        }
        else {
            visit(ctx.cellReference());
            Float retVal = currentCell.getVal();
            return (T) retVal;
        }
        throw new ParseCancellationException();
    }

    @Override
    public T visitOutputRule(CSVScriptParser.OutputRuleContext ctx) {
        if (ctx.getChildCount() == 0) return null;
        String rule = ctx.ID(0).toString();
        String subsetName = ctx.ID(1).toString();
        if (!functionContexts.containsKey(rule)) {
            System.err.printf("function '%s' does not exist (line %d)", rule, ctx.start.getLine());
            System.exit(1);
        }
        if (!subsetVars.containsKey(subsetName)) {
            System.err.printf("subset '%s' does not exist (line %d)", subsetName, ctx.start.getLine());
            System.exit(1);
        }
        CSVScriptParser.ExprContext rule_ctx = functionContexts.get(rule);
        Subset set = subsetVars.get(subsetName);
        String[][] file = inFiles.get(set.fileVar);
        for (int[] loc : set.getCellLocations()) {
            try {
                valCell = new Cell(set.fileVar, loc[0], loc[1]); //Sets current cell to be used in rule with 'value' keyword
                CSVScriptParser.ExprContext exprCtx = functionContexts.get(rule);
                Float newVal = (Float) visit(exprCtx);
                file[loc[0]][loc[1]] = newVal.toString();
            } catch (NumberFormatException e) {
                // pass
            }

        }
        visit(ctx.outputRule());
        return null;
    }

    @Override
    public T visitOutputWrite(CSVScriptParser.OutputWriteContext ctx) {
        String out_file = (String) visit(ctx.filename());
        String in_file = ctx.ID().toString();
        FileWriter csvOut;
        try {
            csvOut = new FileWriter(out_file);
        } catch(IOException e) {
            System.err.print("output attempt failed");
            e.printStackTrace();
            return null;
        }

        String[][] arr = inFiles.get(in_file);
        if (arr == null) {
            System.err.printf("file variable '%s' does not exist on line %d\n", in_file, ctx.start.getLine());
            System.exit(1);
        }
        for (int i = 0; i < arr.length; i++) {
            String[] rowData = new String[arr[i].length];
            //System.out.println("New row: " + arr.length + " items long");
            for (int j = 0; j < arr[i].length; j++) {
                //System.out.println("testing: i = " + i + ", j = " + j + ", val = " + arr[i][j]);
                rowData[j] = arr[i][j];
            }
            try {
                csvOut.append(String.join(",", rowData));
                csvOut.append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            csvOut.flush();
            csvOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO - implement min/max/avg vals

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