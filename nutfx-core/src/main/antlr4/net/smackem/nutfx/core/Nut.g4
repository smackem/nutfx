grammar Nut;

command
    : Ident positionalParameter* namedParameter* EOF
    ;

positionalParameter
    : value
    ;

namedParameter
    : '-' Ident ('=' value)?
    ;

value
    : String
    | Integer
    | Float
    | Ident
    ;

Ident
    : ('a' .. 'z' | 'A' .. 'Z' | '_') ('a' .. 'z' | 'A' .. 'Z' | '_' | '-' | '0' .. '9') *
    ;

Integer
    : [0-9]+
    ;

Float
    : [0-9]+ '.' [0-9]+?
    ;

String
    : '"' .*? '"'
    | '\'' .*? '\''
    ;

Comment
    : '//' ~ [\r\n]* -> skip
    ;

Ws
    : [ \t\r\n] -> skip
    ;
