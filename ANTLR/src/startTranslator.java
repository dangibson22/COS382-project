import java.nio.file.NoSuchFileException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class startTranslator {
    public static void main(String[] args) throws Exception {
        CharStream input;
        try {
            input = CharStreams.fromFileName(args[0]);
        } catch (NoSuchFileException e) {
            System.out.println(e);
            return;
        }

        CSVScriptLexer lexer = new CSVScriptLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CSVScriptParser parser = new CSVScriptParser(tokens);
        ParseTree tree = parser.start();
        dslListener extractor = new dslListener();
        ParseTreeWalker.DEFAULT.walk(extractor, tree);
    }
}
