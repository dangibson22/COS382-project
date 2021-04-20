import java.util.*;

public class dslListener extends CSVScriptBaseListener {

    @Override
    public void enterInputStatement(CSVScriptParser.InputStatementContext ctx) {
        System.out.println("Hello World");
    }

}