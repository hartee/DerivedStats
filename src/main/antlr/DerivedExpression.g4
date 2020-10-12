grammar DerivedExpression;

/*
* PARSER Rules
*/

assignment
	: statId ASSIGN expression EOF
	;

expression
    : ternary_expression
    | logical_or_expression
    ;

logical_or_expression
    : logical_and_expression                                                            # Logical
    | logical_or_expression LOGICAL_OR logical_and_expression                           # LogicalOR
    ;

ternary_expression
    : logical_or_expression ternary_clause
    ;

ternary_clause
    : QUESTION expression COLON expression                                              # Ternary
    ;

logical_and_expression
    : equality_expression                                                               # Equality
    | logical_and_expression LOGICAL_AND equality_expression                            # LogicalAND
    ;

equality_expression
    : relational_expression                                                             # Relational
    | equality_expression EQ relational_expression                                      # EqualityEQ
    | equality_expression NEQ relational_expression                                     # EqualityNEQ
    ;

relational_expression
    : additive_expression                                                               # Additive
    | relational_expression LT additive_expression                                      # RelationalLT
    | relational_expression GT additive_expression                                      # RelationalGT
    | relational_expression LE additive_expression                                      # RelationalLE
    | relational_expression GE additive_expression                                      # RelationalGE
    ;

additive_expression
    : multiplicative_expression                                                         # Multiplicative
    | additive_expression PLUS multiplicative_expression                                # AdditivePLUS
    | additive_expression MINUS multiplicative_expression                               # AdditiveMINUS
    ;

multiplicative_expression
    : unary_expression                                                                  # Unary
    | multiplicative_expression MULT unary_expression                                   # MultiplicativeMULTI
    | multiplicative_expression DIV unary_expression                                    # MultiplicativeDIV
    ;

unary_expression
    : primary_expression                                                                # Primary
    | PLUS primary_expression                                                           # PLUSPrimary
    | MINUS primary_expression                                                          # MINUSPrimary
    ;

primary_expression
    :   numeric_entity                                                                  # Numeric
    |   LPAREN expression RPAREN                                                        # Parens
    |   aggregate_expression                                                            # Aggregate
    |   statId                                                                          # StatIdPrimary
    ;

aggregate_expression
    : SUM aggregateList                                                                 # SUMAggregate
    | AVG aggregateList                                                                 # AVGAggregate
    | MAX aggregateList                                                                 # MAXAggregate
    | MIN aggregateList                                                                 # MINAggregate
    ;

aggregateList
    : LPAREN aggregateClause RPAREN
    ;

aggregateClause
    : (expression) (COMMA expression)*
    ;

statId
	: baseStatId
	| qualifiedStatId
	;

baseStatId
	: IDENTIFIER (dimensionList)?
	;

qualifiedStatId
	: categoryId DOT baseStatId
	;

categoryId
	: IDENTIFIER
	;

numeric_entity
	: DECIMAL                                                                           # DecimalLiteral
	| POSITIVE_INFINITY                                                                 # PosINF
	| NEGATIVE_INFINITY                                                                 # NegInf
	| NAN                                                                               # NaN
	;

dimensionList
	: DIM_LIST_START dimensionListItem (COMMA dimensionListItem)* DIM_LIST_END
	;

dimensionListItem
	: IDENTIFIER COLON dim_value_clause
	;

dim_value_clause
	: DIM_VALUE
	;

/*
 * Lexer Rules
 */

 QUESTION
    : '?'
    ;

 COLON
    : ':'
    ;

 LE
    : '<='
    ;

 GE
    : '>='
    ;

 LT
    : '<'
    ;

 GT
    : '>'
    ;

 EQ
    : '=='
    ;

 NEQ
    : '!='
    ;

 LOGICAL_OR
    : '||'
    ;

 LOGICAL_AND
    : '&&'
    ;

 DIM_VALUE_LIST_START
 	: '['
 	;

 DIM_VALUE_LIST_END
 	: ']'
 	;

 DIM_LIST_START
 	: '{'
 	;

 DIM_LIST_END
 	: '}'
 	;

 DIM_VALUE
 	: STRING
 	;

 STRING
 	: '"' (ESC | ~["\\])* '"'
 	;

 ESC
 	: '\\' ( ["\\/bfnr] )
 	;

 ASSIGN
 	: '='
 	;

 DOT
 	: '.'
 	;

 SUM
 	: 'SUM'
 	;

 AVG
    : 'AVG'
    ;

 MIN
    : 'MIN'
    ;

 MAX
    : 'MAX'
    ;

 COMMA
 	: ','
 	;

 MULT
 	: '*'
 	;

 DIV
 	: '/'
 	;

 PLUS
 	: '+'
 	;

 MINUS
 	: '-'
 	;

 LPAREN
 	: '('
 	;

 RPAREN
 	: ')'
 	;

 DECIMAL
 	: '-'?[0-9]+('.'[0-9]+)?
 	;

 POSITIVE_INFINITY
    : 'Infinity'
    ;

 NEGATIVE_INFINITY
    : '-Infinity'
    ;

 NAN
    : 'NaN'
    ;

 IDENTIFIER
 	: [a-zA-Z_][a-zA-Z_0-9]*
 	;

 WS
 	: [ \r\t\u000C\n]+ -> skip
 	;


