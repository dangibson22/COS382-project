grammar CSVScript;

start   : input actions output EOF ;

input   : 'input:' inputStatement 'end input' ';' ;
inputStatement  : 'read' filename 'as' id inputFlags ';' inputStatement
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



output          : 'output:' outputStatement 'end output' ';' ;
outputStatement : outputRule outputWrite ;

outputRule      : 'use' ruleId 'on' setId ';' outputRule
                | /* epsilon */
                ;

outputWrite     : 'write' id filename ';' ;