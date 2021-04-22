// Generated from /home/CS/users/dgibson/.linux/Documents/COS382/project/COS382-project/ANTLR/CSVScript.g4 by ANTLR 4.9.1
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CSVScriptParser}.
 */
public interface CSVScriptListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#start}.
	 * @param ctx the parse tree
	 */
	void enterStart(CSVScriptParser.StartContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#start}.
	 * @param ctx the parse tree
	 */
	void exitStart(CSVScriptParser.StartContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#input}.
	 * @param ctx the parse tree
	 */
	void enterInput(CSVScriptParser.InputContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#input}.
	 * @param ctx the parse tree
	 */
	void exitInput(CSVScriptParser.InputContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#inputStatement}.
	 * @param ctx the parse tree
	 */
	void enterInputStatement(CSVScriptParser.InputStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#inputStatement}.
	 * @param ctx the parse tree
	 */
	void exitInputStatement(CSVScriptParser.InputStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#inputFlags}.
	 * @param ctx the parse tree
	 */
	void enterInputFlags(CSVScriptParser.InputFlagsContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#inputFlags}.
	 * @param ctx the parse tree
	 */
	void exitInputFlags(CSVScriptParser.InputFlagsContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#inputFlag}.
	 * @param ctx the parse tree
	 */
	void enterInputFlag(CSVScriptParser.InputFlagContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#inputFlag}.
	 * @param ctx the parse tree
	 */
	void exitInputFlag(CSVScriptParser.InputFlagContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#actions}.
	 * @param ctx the parse tree
	 */
	void enterActions(CSVScriptParser.ActionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#actions}.
	 * @param ctx the parse tree
	 */
	void exitActions(CSVScriptParser.ActionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#actionBlock}.
	 * @param ctx the parse tree
	 */
	void enterActionBlock(CSVScriptParser.ActionBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#actionBlock}.
	 * @param ctx the parse tree
	 */
	void exitActionBlock(CSVScriptParser.ActionBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#action}.
	 * @param ctx the parse tree
	 */
	void enterAction(CSVScriptParser.ActionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#action}.
	 * @param ctx the parse tree
	 */
	void exitAction(CSVScriptParser.ActionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#subsetAssignment}.
	 * @param ctx the parse tree
	 */
	void enterSubsetAssignment(CSVScriptParser.SubsetAssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#subsetAssignment}.
	 * @param ctx the parse tree
	 */
	void exitSubsetAssignment(CSVScriptParser.SubsetAssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#set}.
	 * @param ctx the parse tree
	 */
	void enterSet(CSVScriptParser.SetContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#set}.
	 * @param ctx the parse tree
	 */
	void exitSet(CSVScriptParser.SetContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#references}.
	 * @param ctx the parse tree
	 */
	void enterReferences(CSVScriptParser.ReferencesContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#references}.
	 * @param ctx the parse tree
	 */
	void exitReferences(CSVScriptParser.ReferencesContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#reference}.
	 * @param ctx the parse tree
	 */
	void enterReference(CSVScriptParser.ReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#reference}.
	 * @param ctx the parse tree
	 */
	void exitReference(CSVScriptParser.ReferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#cellAssignment}.
	 * @param ctx the parse tree
	 */
	void enterCellAssignment(CSVScriptParser.CellAssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#cellAssignment}.
	 * @param ctx the parse tree
	 */
	void exitCellAssignment(CSVScriptParser.CellAssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#schemeAssignment}.
	 * @param ctx the parse tree
	 */
	void enterSchemeAssignment(CSVScriptParser.SchemeAssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#schemeAssignment}.
	 * @param ctx the parse tree
	 */
	void exitSchemeAssignment(CSVScriptParser.SchemeAssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#r}.
	 * @param ctx the parse tree
	 */
	void enterR(CSVScriptParser.RContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#r}.
	 * @param ctx the parse tree
	 */
	void exitR(CSVScriptParser.RContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#rules}.
	 * @param ctx the parse tree
	 */
	void enterRules(CSVScriptParser.RulesContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#rules}.
	 * @param ctx the parse tree
	 */
	void exitRules(CSVScriptParser.RulesContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(CSVScriptParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(CSVScriptParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#terms}.
	 * @param ctx the parse tree
	 */
	void enterTerms(CSVScriptParser.TermsContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#terms}.
	 * @param ctx the parse tree
	 */
	void exitTerms(CSVScriptParser.TermsContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(CSVScriptParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(CSVScriptParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#factors}.
	 * @param ctx the parse tree
	 */
	void enterFactors(CSVScriptParser.FactorsContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#factors}.
	 * @param ctx the parse tree
	 */
	void exitFactors(CSVScriptParser.FactorsContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterFactor(CSVScriptParser.FactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitFactor(CSVScriptParser.FactorContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#functionAssignment}.
	 * @param ctx the parse tree
	 */
	void enterFunctionAssignment(CSVScriptParser.FunctionAssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#functionAssignment}.
	 * @param ctx the parse tree
	 */
	void exitFunctionAssignment(CSVScriptParser.FunctionAssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(CSVScriptParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(CSVScriptParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#forStatement}.
	 * @param ctx the parse tree
	 */
	void enterForStatement(CSVScriptParser.ForStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#forStatement}.
	 * @param ctx the parse tree
	 */
	void exitForStatement(CSVScriptParser.ForStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#conditional}.
	 * @param ctx the parse tree
	 */
	void enterConditional(CSVScriptParser.ConditionalContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#conditional}.
	 * @param ctx the parse tree
	 */
	void exitConditional(CSVScriptParser.ConditionalContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(CSVScriptParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(CSVScriptParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#output}.
	 * @param ctx the parse tree
	 */
	void enterOutput(CSVScriptParser.OutputContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#output}.
	 * @param ctx the parse tree
	 */
	void exitOutput(CSVScriptParser.OutputContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#outputStatement}.
	 * @param ctx the parse tree
	 */
	void enterOutputStatement(CSVScriptParser.OutputStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#outputStatement}.
	 * @param ctx the parse tree
	 */
	void exitOutputStatement(CSVScriptParser.OutputStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#outputRule}.
	 * @param ctx the parse tree
	 */
	void enterOutputRule(CSVScriptParser.OutputRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#outputRule}.
	 * @param ctx the parse tree
	 */
	void exitOutputRule(CSVScriptParser.OutputRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#outputWrite}.
	 * @param ctx the parse tree
	 */
	void enterOutputWrite(CSVScriptParser.OutputWriteContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#outputWrite}.
	 * @param ctx the parse tree
	 */
	void exitOutputWrite(CSVScriptParser.OutputWriteContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#filename}.
	 * @param ctx the parse tree
	 */
	void enterFilename(CSVScriptParser.FilenameContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#filename}.
	 * @param ctx the parse tree
	 */
	void exitFilename(CSVScriptParser.FilenameContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVScriptParser#realNumber}.
	 * @param ctx the parse tree
	 */
	void enterRealNumber(CSVScriptParser.RealNumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVScriptParser#realNumber}.
	 * @param ctx the parse tree
	 */
	void exitRealNumber(CSVScriptParser.RealNumberContext ctx);
}