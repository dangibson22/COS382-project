import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.*;
import java.util.*;

public class dslVisitor<T> extends CSVScriptBaseVisitor<T> {
    private LinkedList<String[][]> inFiles = new LinkedList<>(); //List of all our inFiles
    private int inFilesSize = 0;
    private Map<String, Integer> fileVarListIdx = new Hashtable<>(); //Maps each file's variable name to one of our array tables

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

    @Override
    public T visitInputStatement(CSVScriptParser.InputStatementContext ctx) {
        /*
        Probably will have to do more coding to handle headers vs noheaders.
        Make another 2 maps that map colHeader to column index and rowHeader to row index
         */
        if (ctx.getChildCount() == 0) return null; //Parser matched epsilon rule
        String filename = (String) visit(ctx.filename());

        try {
            File inFile = new File(filename);
            Scanner myReader = new Scanner(inFile);
            int[] rowColSize = getRowColSize(inFile);
            String[][] spreadsheet = new String[rowColSize[0]][rowColSize[1]];
            int row = 0;
            //Initialize the array
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                String[] data = line.split(",");
                spreadsheet[row] = data;
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


}