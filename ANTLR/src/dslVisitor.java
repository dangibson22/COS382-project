public class dslVisitor<T> extends CSVScriptBaseVisitor<T> {

    @Override
    public T visitInputStatement(CSVScriptParser.InputStatementContext ctx) {
        if (ctx.getChildCount() == 0) return null;
        //System.out.println("Hello World");
        String filename = "";
        filename = (String) visit(ctx.filename());
        System.out.println(filename);
        return visit(ctx.inputStatement());
    }

    @Override
    public T visitFilename(CSVScriptParser.FilenameContext ctx) {
        return (T) ctx.getText();
    }

}