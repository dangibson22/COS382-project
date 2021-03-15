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

subsetAssignment: 'subset' ID '=' set;
set				: '[' references ']';
references		: reference
				| reference ',' references
				| /* epsilon */
				;
reference		: ID // row or column name
				| ID '.' ID // colName.rowName
				| reference 'to' reference
				;

cellAssignment	: 'cell' ID '=' reference;

schemeAssignment: 'scheme' ID '=' '{' r '}';

r				: ID ':' expr rules;

rules			: ',' r rules //ID corresponds to a subset or cell
				|
				;
expr			: term terms ;
/*The below code was ripped from A02 to register mathematical expressions*/
terms			: '+' term terms
				| '-' term terms
				| ;

term			: factor factors;

factors			: '*' factor factors
				| '/' factor factors
				| 'mod' factor factors
				| ;

factor			: '(' expr ')'
				| value
				| ID '.' ID //This ID should be a reference to a column or row
				;
/*End Math portion*/

functionAssignment: 'function' ID '=' expr ; //A rule is a mathematical operation to apply to the value of a cell or subset

ifStatement		: 'if' '(' conditional ')' actionBlock 'end if';

conditional		: value OPERATOR value
				| '('conditional')' 'and' '('conditional')'
				| '('conditional')' 'or' '('conditional')'
				;

value			: INT
				| ID
				| realNumber
				;
/*End actions phase*/

output          : 'output:' outputStatement 'end output' ';' ;
outputStatement : outputRule outputWrite ;

outputRule      : 'use' ID 'on' reference ';' outputRule //first ID corresponds to rule. Second ID corresponds to set.
                | /* epsilon */
                ;

outputWrite     : 'write' ID filename ';' ; //ID corresponds to a subsection

filename		: ID '.csv'
				| ALPHANUM '.csv';

realNumber		: INT '.' INT ;

INT     : [0-9]+ ;
ID      : [a-zA-Z_] [0-9a-zA-Z_]+ ;
NEWLINE : '\r'? '\n' -> skip;
WS      : [ \t]+ -> skip ;  // tells ANTLR to ignore these
OPERATOR: ('>=' | '<=' | '>' | '<' | '==' | '!=');
ALPHANUM: [0-9a-zA-Z_]+ ;