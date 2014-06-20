grammar OMP;

@header {
package org.omp4j.preprocessor.grammar;
}

// PARSER RULES
ompUnit : OMP (
               ompParallel    |
               ompParallelFor |
               ompFor         |
               ompSections    |
               ompSection
          ) ;

ompParallel    : PARALLEL     ompSchedule? ompModifier* ;
ompParallelFor : PARALLEL FOR ompSchedule? ompModifier* ;
ompFor         : FOR          ompSchedule? ompModifier* ;
ompSections    : SECTIONS                               ;
ompSection     : SECTION                                ;
ompModifier    : ( PUBLIC | PRIVATE ) '(' ompVars ')'   ;
ompVars        : ( ompVar | ( ompVar ',' )+ ompVar )    ;
ompVar         : VAR                                    ;
ompSchedule    : SCHEDULE ( STATIC | DYNAMIC )          ;

// LEXER RULES
OMP       : 'omp'      ;
PARALLEL  : 'parallel' ;
FOR       : 'for'      ;
SECTIONS  : 'sections' ;
SECTION   : 'section'  ;
BARRIER   : 'barrier'  ;
CRITICAL  : 'critical' ;
PUBLIC    : 'public'   ;
PRIVATE   : 'private'  ;
SCHEDULE  : 'schedule' ;
STATIC    : 'static'   ;
DYNAMIC   : 'dynamic'  ;

VAR : JavaLetter JavaLetterOrDigit* ;
WS  : [ \t\r\n\u000C]+ -> skip      ;
EOL : ( '\r'? '\n' )+               ;

// following code was taken from:
// https://github.com/antlr/grammars-v4/blob/master/java8/Java8.g4
fragment
JavaLetter
    :   [a-zA-Z$_] // these are the "java letters" below 0xFF
    |   // covers all characters above 0xFF which are not a surrogate
        ~[\u0000-\u00FF\uD800-\uDBFF]
        {Character.isJavaIdentifierStart(_input.LA(-1))}?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

fragment
JavaLetterOrDigit
    :   [a-zA-Z0-9$_] // these are the "java letters or digits" below 0xFF
    |   // covers all characters above 0xFF which are not a surrogate
        ~[\u0000-\u00FF\uD800-\uDBFF]
        {Character.isJavaIdentifierPart(_input.LA(-1))}?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;
