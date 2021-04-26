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
				| forStatement
				| subsetAssignment
				| cellAssignment
				| schemeAssignment
				| functionAssignment
				;

subsetAssignment: ID 'subset' ID '=' set; // First ID is fileVar, second is subsetVar

set				: '[' references ']';
references		: reference
				| reference ',' references
				;

reference		: 'row' ID //subset reference
                | 'col' ID
				| cellReference // colName.rowName
				| reference 'to' reference
				;

cellAssignment	: 'cell' ID '=' cellReference;

cellReference   : ID '.' ID '.' ID // colName.rowName
                | ID // Cell variable name
                ;

schemeAssignment: 'scheme' ID '=' '{' r '}';

r				: ID ':' expr rules;

rules			: ',' r rules //ID corresponds to a subset or cell
				|
				;

/*Math portion*/
expr			: term
                | terms '+' term
                | terms '-' term
                ;

terms			: term
				| terms '+' term
				| terms '-' term;

term			: factor
                | factors '*' factor
                | factors '/' factor
                ;

factors			: factor
                | factors '*' factor
                | factors '/' factor
                /*Maybe a mod function*/
                ;

factor			: '(' expr ')'
				| value
				| cellReference
				;
/*End Math portion*/

functionAssignment: 'function' ID '=' expr ; //A rule is a mathematical operation to apply to the value of a cell or subset

ifStatement		: 'if' '(' conditional ')' actionBlock 'end if';

forStatement	: 'for' ID 'in' ID actionBlock 'end for'
				| 'for' ID 'in' set actionBlock 'end for'
				;

conditional		: value OPERATOR value
				| '('conditional')' AND '('conditional')'
				| '('conditional')' OR '('conditional')'
				;

value			: INT
				/*| ID*/
				| realNumber
				;
/*End actions phase*/

output          : 'output:' outputStatement 'end output' ';' ;
outputStatement : outputRule outputWrite ;

outputRule      : 'use' ID 'on' ID ';' outputRule //first ID corresponds to rule. Second ID corresponds to set.
                | /* epsilon */
                ;

outputWrite     : 'write' ID filename ';' ; //ID corresponds to a subsection

filename		: ID '.csv'
				| ALPHANUM '.csv';

realNumber		: INT '.' INT ;

INT     : [0-9]+ ;
AND     : 'and';
OR      : 'or';
OPERATOR: ('>=' | '<=' | '>' | '<' | '==' | '!=');
ID      : [a-zA-Z_] [0-9a-zA-Z_]+ ;
ALPHANUM: [0-9a-zA-Z_/]+ ;
NEWLINE : '\r'? '\n' -> skip;
WS      : [ \t]+ -> skip ;  // tells ANTLR to ignore these