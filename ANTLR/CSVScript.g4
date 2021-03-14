grammar CSVScript;

start   : input actions output EOF ;

input   : 'input:' inputStatement 'end input' ';' ;
inputStatement  : 'read' filename 'as' ID inputFlags ';' inputStatement
                | /* epsilon */
                ;
inputFlags      : '(' inputFlag ')'
                | /* epsilon */
                ;
inputFlag       : 'noRowHeader'
                | 'noColHeader'
                | 'noRowHeader' ',' 'noColHeader'
                | 'noColHeader' ',' 'noRowHeader'
                ;
/*End input phase*/

actions			: 'actions:' actionBlock 'end actions' ';' ;
actionBlock		: action ';' actionBlock
				| /* epsilon */
				;
action			: ifStatement
				| subsetAssignment
				| cellAssignment
				| schemeAssignment
				| functionAssignment
				;

subsetAssignment: SUBSET ID '=' set;
set				: '[' references ']';
references		: reference ',' references
				| /* epsilon */
				;
reference		: ID // row or column name
				| ID '.' ID // colName.rowName
				| reference '-' reference
				;

cellAssignment	: CELL ID '=' reference;

schemeAssignment: SCHEME ID '=' '{' rules '}';

rules			: ID ':' expr ',' rules ; //ID corresponds to a subset or cell
expr			: term terms ;
/*The below code was ripped from A02 to register mathematical expressions*/
terms			: '+' term terms
				| '-' term terms
				| ;

term			: factor factors;

factors			: '*' factor factors
				| '/' factor factors
				| MOD factor factors
				| ;

factor			: '(' expr ')'
				| VALUE
				| ID '.' VALUE //This ID should be a reference to a column or row
				| INT
				;
/*End Math portion*/

functionAssignment: FUNCTION ID '=' expr ; //A rule is a mathematical operation to apply to the value of a cell or subset

ifStatement		: 'if' '(' conditional ')' actionBlock 'end if';

conditional		: value OPERATOR value
				| '('conditional')' 'and' '('conditional')'
				| '('conditional')' 'or' '('conditional')'
				;

value			: INT
				| ID
				;
/*End actions phase*/

output          : 'output:' outputStatement 'end output' ';' ;
outputStatement : outputRule outputWrite ;

outputRule      : 'use' ID 'on' ID ';' outputRule //first ID corresponds to rule. Second ID corresponds to set.
                | /* epsilon */
                ;

outputWrite     : 'write' ID filename ';' ; //ID corresponds to a subsection

filename		: ALPHANUM '.csv';

INT     : [0-9]+ ;
ID      : [a-zA-Z_] [0-9a-zA-Z_]+ ;
NEWLINE : '\r'? '\n' -> skip;
WS      : [ \t]+ -> skip ;  // tells ANTLR to ignore these
OPERATOR: ('>=' | '<=' | '>' | '<' | '==' | '!=');
ALPHANUM: [0-9a-zA-Z_]+ ;
SUBSET	: 'subset';
CELL	: 'cell';
SCHEME	: 'scheme';
FUNCTION: 'function';
VALUE	: 'value';
MOD		: 'mod';