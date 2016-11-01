%{
#include <fstream>
using namespace std;

int lineno = 1;
%}

digit                   [0-9] 
letter                  [a-zA-Z]
alphanum		[A-Za-z0-9]

%%

"void"                  return 'VOID'; 
"continue"              return 'CONTINUE'; 
"if"                    return 'IF';  
"while"                 return 'WHILE';
"else"                  return 'ELSE';
"break"                 return 'BREAK';
"int"                   return 'INT';
"return"                return 'RETURN';
"||"                    return 'OR'; 
"&&"                    return 'AND';
{letter}{alphanum}*     yyval.IDENT_v.ID_NAME=yytext;return IDENT;
0(x|X)({letter}|[0-9])* yyval.int_literal_v.int_val=ConvertHexToint(yytext);return HEXNUM; 
{digit}+(\.{digit}+)?   yyval.int_literal_v.int_val=atoi(yytext);return DECNUM; 
"<="                    return 'LE';
">="                    return 'GE';
"=="                    return 'EQ';
"!="                    return 'NE';
">"                     return '>';
"<"                     return '<'; 
","                     return ','; 
";"                     return ';';  
"{"                     return '{';  
"}"                     return '}';  
"%"                     return '%'; 
"*"                     return '*';
"+"                     return '+';  
"-"                     return '-';  
"/"                     return '/'; 
"="                     return '=';  
"("                     return '('; 
")"                     return ')';  
"~"                     return '~';
"&"                     return '&';
"^"                     return '^'; 
"["                     return '['; 
"]"                     return ']'; 
"<<"                    return 'LSHIFT'; 
">>"                    return 'RSHIFT';
"|"                     return '|';
\t                      return ' ';
\n                      Lineno++;yylineno++;
\r\n                    Lineno++;yylineno++; 
"$"                     return '$';

%%

int main()
{
	ifstream in("test.c");
	seuLex(in, std::cout);
	cout<<"Lexing Ended\n";
	system("pause");
	return 0;
}
