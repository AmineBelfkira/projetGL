lexer grammar DecaLexer;

options {
   language=Java;
   // Tell ANTLR to make the generated lexer class extend the
   // the named class, which is where any supporting code and
   // variables will be placed.
   superClass = AbstractDecaLexer;
}


@members {
}

// Deca lexer rules.

// Mots réservés
CLASS: 'class';   
NEW: 'new';
NULL: 'null';  
EXTENDS: 'extends';
PROTECTED: 'protected'; 
THIS: 'this';
INSTANCEOF: 'instanceof'; 
TRUE: 'true'; 
FALSE: 'false';      
IF: 'if';
ELSE: 'else';
WHILE: 'while';
PRINT: 'print';
PRINTLN: 'println';
PRINTX: 'printx'; 
PRINTLNX: 'printlnx';  
RETURN: 'return';  
READINT : 'readInt';
READFLOAT : 'readFloat';   
ASM: 'asm';

//Symboles spéciaux
EQUALS: '=';
EQEQ: '==';
PLUS: '+';
MINUS: '-';
TIMES: '*';
DOT: '.';
COMMA: ',';
OPARENT: '(';
CPARENT: ')';
SLASH: '/';
PERCENT: '%';
OBRACE: '{';
CBRACE: '}';
EXCLAM: '!';
SEMI: ';';
NEQ: '!=';
GEQ: '>=';
LEQ: '<=';
LT: '<' ;
GT: '>' ;
AND: '&&';
OR: '||';


//Littéraux entiers
fragment POSITIVE_DIGIT : '1' .. '9';
INT : '0' | POSITIVE_DIGIT DIGIT*;

//Littéraux flottants
fragment NUM : DIGIT+;
fragment SIGN : PLUS | MINUS | ;
fragment EXP : ('E' | 'e') SIGN NUM;
fragment DEC : NUM '.' NUM;
fragment FLOATDEC : (DEC | DEC EXP) ('F' | 'f'| );
fragment DIGITHEX : '0' .. '9' | 'A' .. 'F' | 'a' .. 'f';
fragment NUMHEX : DIGITHEX+;
fragment FLOATHEX : ('0x' | 'OX') NUMHEX '.' NUMHEX ('P' | 'p') SIGN NUM ('F' | 'f' | );
FLOAT : FLOATDEC | FLOATHEX;

//Chaînes de caractères
fragment EOL : '\n';
fragment STRING_CAR :  ~('\n'|'"'|'\\');
STRING : '"' (STRING_CAR | '\\"' | '\\\\')* '"';
MULTI_LINE_STRING : '"' (STRING_CAR | EOL | '\\"' | '\\\\')* '"';

//Commentaires
fragment COMMENT : (('/*' .*? '*/') | ('//'  .*?  (EOL|EOF)));

//Séparateurs
fragment SPACE : ' ';
SEPARETOR : (SPACE | EOL | COMMENT | '\t' | '\r') ->skip;

//Inclusion de fichier
fragment FILENAME : (LETTER | DIGIT | '.' | '-' | '_')+;
INCLUDE : '#include' (' ')* '"' FILENAME '"' {doInclude(getText()); };

//Identificateurs
fragment DIGIT : '0' .. '9';
fragment LETTER : ('a' .. 'z')|('A' .. 'Z');
IDENT : (LETTER  | '$' | '_') (LETTER | DIGIT | '$' | '_')* ;