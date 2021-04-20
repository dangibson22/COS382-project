// Generated from /home/trevor/cos382/P00-DSL/ANTLR/CSVScript.g4 by ANTLR 4.9.1
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CSVScriptParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CSVScriptVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStart(CSVScriptParser.StartContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#input}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput(CSVScriptParser.InputContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#inputStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInputStatement(CSVScriptParser.InputStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#inputFlags}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInputFlags(CSVScriptParser.InputFlagsContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#inputFlag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInputFlag(CSVScriptParser.InputFlagContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#actions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActions(CSVScriptParser.ActionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#actionBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActionBlock(CSVScriptParser.ActionBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction(CSVScriptParser.ActionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#subsetAssignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubsetAssignment(CSVScriptParser.SubsetAssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#set}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSet(CSVScriptParser.SetContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#references}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReferences(CSVScriptParser.ReferencesContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#reference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReference(CSVScriptParser.ReferenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#cellAssignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCellAssignment(CSVScriptParser.CellAssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#schemeAssignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSchemeAssignment(CSVScriptParser.SchemeAssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#r}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitR(CSVScriptParser.RContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#rules}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRules(CSVScriptParser.RulesContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(CSVScriptParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#terms}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerms(CSVScriptParser.TermsContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm(CSVScriptParser.TermContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#factors}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFactors(CSVScriptParser.FactorsContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFactor(CSVScriptParser.FactorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#functionAssignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionAssignment(CSVScriptParser.FunctionAssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(CSVScriptParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#forStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStatement(CSVScriptParser.ForStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#conditional}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditional(CSVScriptParser.ConditionalContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(CSVScriptParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#output}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutput(CSVScriptParser.OutputContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#outputStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutputStatement(CSVScriptParser.OutputStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#outputRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutputRule(CSVScriptParser.OutputRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#outputWrite}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutputWrite(CSVScriptParser.OutputWriteContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#filename}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFilename(CSVScriptParser.FilenameContext ctx);
	/**
	 * Visit a parse tree produced by {@link CSVScriptParser#realNumber}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRealNumber(CSVScriptParser.RealNumberContext ctx);
}