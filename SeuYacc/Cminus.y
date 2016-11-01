%%

%token IDENT VOID INT WHILE IF ELSE RETURN EQ NE LE GE AND OR DECNUM CONTINUE BREAK HEXNUM LSHIFT RSHIFT 
%left OR 
%left AND 
%left EQ NE LE GE '<' '>' 
%left '+' '-' 
%left '|' 
%left '&' '^' 
%left '*' '/' '%'
%right LSHIFT RSHIFT 
%right '!' 
%right '~'

%%

program        : decl_list                                              {}

decl_list      : decl_list decl                                         {}
               | decl                                                   {}

decl           : var_decl                                               {}
               | fun_decl                                               {}

var_decl       : type_spec IDENT ;                                      {enter(IDENT.name,type_spec.type,offset);offset=offset+type_spec.width;}
               | type_spec IDENT [ int_literal ] ;                      {enter(IDENT.name,array(int_literal.lexval,type_spec.type),offset);offset=offset+int_literal.lexval*type_spec.width;}

type_spec      : INT                                                    {type_spec.type=integer;type_spec.width=4;}
               | VOID                                                   {}

fun_decl       : type_spec FUNCTION_IDENT ( params ) compound_stmt      {build a table;offset=0;put param.itemlist into the table;}
               | type_spec FUNCTION_IDENT ( params ) ;                  {}

FUNCTION_IDENT : IDENT                                                  {}

params         : param_list                                             {params.itemlist=param_list.itemlist;}
               | VOID                                                   {}

param_list     : param_list , param                                     {param_list1.itemlist=param_list2.itemlist+param.itemlist;}
               | param                                                  {param_list.itemlist=param.itemlist;}

param          : type_spec IDENT                                        {param.list=makelist(IDENT.name,type_spec.type,offset);offset=offset+type_spec.width;}
               | type_spec IDENT [ int_literal ]                        {param.list=makelist(IDENT.name,array(int_literal.lexval,type_spec.type),offset);offset=offset+int_literal.lexval*type_spec.width;}

compound_stmt  : { compound }                                           {}

compound       : local_decls stmt_list                                  {}
               | stmt_list                                              {}
               | #                                                      {}

local_decls    : local_decls local_decl                                 {enter(IDENT.name,type_spec.type,offset);offset=offset+type_spec.width;}
               | local_decl                                             {enter(IDENT.name,type_spec.type,offset);offset=offset+type_spec.width;}

local_decl     : type_spec IDENT ;                                      {}
               | type_spec IDENT [ int_literal ] ;                      {enter(IDENT.name,array(int_literal.lexval,type_spec.type),offset);offset=offset+int_literal.lexval*type_spec.width;}

stmt_list      : stmt_list stmt                                         {}
               | stmt                                                   {}
               | #                                                      {}

stmt           : expr_stmt                                              {}
               | block_stmt                                             {}
               | if_stmt                                                {}
               | while_stmt                                             {}
               | return_stmt                                            {}
               | continue_stmt                                          {}
               | break_stmt                                             {}

expr_stmt      : IDENT = expr ;                                         {p=lookup(IDENT.name);if(p!=NULL) expr_stmt.code=expr.code || gen(p '=' expr.place);else error("Undefined identifier");}
               | IDENT [ expr ] = expr ;                                {}
               | $ expr = expr ;                                        {}
               | IDENT ( args ) ;                                       {}
               | IDENT ( ) ;                                            {}

while_stmt     : WHILE_IDENT ( expr ) stmt                              {while_stmt.begin=newlabel();expr.true=newlabel();expr.false=while.stmt.next;stmt.next=while_stmt.begin;while_stmt.code=gen(while_stmt.begin ':') || expr.code || gen(expr.true ':') || stmt.code || gen('j' while_stmt.begin);}

WHILE_IDENT    : WHILE                                                  {} 

block_stmt     : { stmt_list }                                          {}

if_stmt        : IF ( expr ) stmt                                       {expr.true=newlabel();expr.false=if_stmt.next;stmt.next=if_stmt.next;if_stmt.code=expr.code || gen(expr.true ':') || stmt.code;}
               | IF ( expr ) stmt ELSE stmt                             {expr.true=newlabel();expr.false=newlabel();stmt1.next=if_stmt.next;stmt2.next=if_stmt.next;if_stmt.code=expr.code || gen(expr.true ':') || stmt1.code || gen('j' if_stmt.next) || gen(expr.false ':') || stmt2.code;}

return_stmt    : RETURN ;                                               {}
               | RETURN expr ;                                          {}

expr           : expr OR expr                                           {}
               | expr EQ expr                                           {}
               | expr NE expr                                           {}
               | expr LE expr                                           {}
               | expr < expr                                            {}
               | expr GE expr                                           {}
               | expr > expr                                            {}
               | expr AND expr                                          {}
               | expr + expr                                            {expr1.place=newtemp();expr1.code=expr2.code || expr3.code || gen(expr1.place '=' expr2.place '+' expr3.place);}
               | expr - expr                                            {expr1.place=newtemp();expr1.code=expr2.code || expr3.code || gen(expr1.place '=' expr2.place '-' expr3.place);}
               | expr * expr                                            {expr1.place=newtemp();expr1.code=expr2.code || expr3.code || gen(expr1.place '=' expr2.place '*' expr3.place);}
               | expr / expr                                            {expr1.place=newtemp();expr1.code=expr2.code || expr3.code || gen(expr1.place '=' expr2.place '/' expr3.place);}
               | expr % expr                                            {}
               | ! expr                                                 {}
               | - expr                                                 {}
               | + expr                                                 {}
               | $ expr                                                 {}
               | ( expr )                                               {expr1.palce=expr2.place;expr1.code=expr2.code;}
               | IDENT                                                  {p=lookup(IDENT.name);if(p!=NULL) expr.place=p;else error("Undefined identifier");}
               | IDENT [ expr ]                                         {expr.code='';for(int i=0;i<args.size();i++) expr.code=expr.code || gen('call' n IDENT.place);}
               | IDENT ( args )                                         {}
               | int_literal                                            {}
               | expr & expr                                            {}
               | expr ^ expr                                            {}
               | ~ expr                                                 {}
               | expr LSHIFT expr                                       {}
               | expr RSHIFT expr                                       {}
               | expr | expr                                            {}

int_literal    : DECNUM                                                 {}
               | HEXNUM                                                 {}

arg_list       : arg_list , expr                                        {arg_list.push(expr);}
               | expr                                                   {arg_list.push(expr);}

args           : arg_list                                               {args=new Queue();}
               | #                                                      {}

continue_stmt  : CONTINUE ;                                             {}

break_stmt     : BREAK ;                                                {}

%%