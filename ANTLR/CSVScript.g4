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
				| numAssignment
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

numAssignment   : 'num' ID '=' INT
                | 'num' ID '=' realNumber
                | 'num' ID '=' opFunc
                | 'num' ID '=' expr
                ;

opFunc          : 'max' ID
                | 'min' ID
                | 'avg' ID 'max' value
                | /* epsilon */
                ;

cellReference   : THIS '.' 'col' '.' ID
                | THIS '.' ID '.' 'row'
                | ID '.' ID '.' ID // colName.rowName
                | ID // Cell variable name
                ;

schemeAssignment: 'scheme' ID '=' '{' r '}';

r				: ID ':' expr rules;

rules			: ',' r //ID corresponds to a subset or cell
				|
				;

/*Math portion*/
expr			: term
                | expr '+' term
                | expr '-' term
                ;

term			: factor
                | term '*' factor
                | term '/' factor
                ;

factor			: '(' expr ')'
                | VAL
				| value
				| variable
				;
/*End Math portion*/

variable        : ID
                | cellReference
                ;

functionAssignment: 'function' ID '=' expr ; //A rule is a mathematical operation to apply to the value of a cell or subset

ifStatement		: 'if' '(' conditional ')' actionBlock 'end if';

forStatement	: 'for' ID 'in' ID forAction 'end for' ;

forAction       : forIf ;

forIf           : ifStatement
                | /* epsilon */
                ;

conditional		: value OPERATOR value
				| '('conditional')' AND '('conditional')'
				| '('conditional')' OR '('conditional')'
				;

value			: INT
				| realNumber
				;
/*End actions phase*/

output          : 'output:' outputStatement 'end output' ';' ;
outputStatement : outputRule outputAdd outputWrite ;

outputRule      : 'use' ID 'on' ID ';' outputRule //first ID corresponds to rule. Second ID corresponds to set.
                | /* epsilon */
                ;

outputAdd       : 'add' ID 'to' ID ';' outputAdd // first ID corresponds to num var, second ID corresponds to inFile
                | /* epsilon */
                ;

outputWrite     : 'write' ID filename ';' ; //ID corresponds to a inFile

filename		: ID '.csv'
				| ALPHANUM '.csv';

realNumber		: INT '.' INT ;

INT     : [0-9]+ ;
AND     : 'and' | 'AND' ;
OR      : 'or' | 'OR' ;
VAL     : 'value';
THIS    : 'this';
OPERATOR: ('>=' | '<=' | '>' | '<' | '==' | '!=');
ID      : [a-zA-Z_] [0-9a-zA-Z_]+ ;
ALPHANUM: [0-9a-zA-Z_/]+ ;
NEWLINE : '\r'? '\n' -> skip;
WS      : [ \t]+ -> skip ;  // tells ANTLR to ignore these