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
				| numAssignment
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

numAssignment   : 'num' ID '=' numVal ;

numVal          : INT
                | realNumber
                | opFunc
                | expr
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

conditional		: value OPERATOR value
				| '('conditional')' AND '('conditional')'
				| '('conditional')' OR '('conditional')'
				;

value			: INT
				| realNumber
				;
/*End actions phase*/

output          : 'output:' outputStatement outputWriteSt 'end output' ';' ;
//outputStatement : outputRule outputAdd outputWrite ;
outputStatement : outputRule outputStatement
                | outputAdd outputStatement
                |
                ;

outputRule      : 'use' ID 'on' ID ';' ;//first ID corresponds to rule. Second ID corresponds to set.

outputAdd       : 'add' ID 'to' ID ';' ; // first ID corresponds to num var, second ID corresponds to inFile

outputWriteSt   : outputWrite outputWriteCont;
outputWriteCont : outputWrite outputWriteCont
                | /* epsilon */
                ;

outputWrite     : 'write' ID filename ';' ; //ID corresponds to a inFile

filename	    : './' filename
                | '../' filename
                | '/' filename
                | ID '.csv'
				| ALPHANUM '.csv';

realNumber		: INT '.' INT ;

INT     : [0-9]+ ;
AND     : 'and' | 'AND' ;
OR      : 'or' | 'OR' ;
VAL     : 'value';
THIS    : 'this';
OPERATOR: ('>=' | '<=' | '>' | '<' | '==' | '!=');
ID      : [a-zA-Z_] [0-9a-zA-Z_]+ ;
ALPHANUM: [0-9a-zA-Z_/\-]+ ;
NEWLINE : '\r'? '\n' -> skip;
WS      : [ \t]+ -> skip ;  // tells ANTLR to ignore these