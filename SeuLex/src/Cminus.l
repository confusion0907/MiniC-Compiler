%{
#include <fstream>
using namespace std;

int lineno = 1;
%}

alpha			[A-Za-z]
digit			[0-9]
alphanum		[A-Za-z0-9]

%%

"int"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tPrimitive Type\n";
"char"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tPrimitive Type\n";
"bool"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tPrimitive Type\n";
"float"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tPrimitive Type\n";
"double"		cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tPrimitive Type\n";
"void"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tPrimitive Type\n";

"null"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tInternal Constant\n";
"true"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tInternal Constant\n";
"false"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tInternal Constant\n";

"if"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tFlow Controller\n";
"else"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tFlow Controller\n";
"while"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tFlow Controller\n";
"return"		cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tFlow Controller\n";

{alpha}{alphanum}*	cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tIdentifier\n";
{digit}+(\.{digit}+)?	cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tNumber\n";

"="			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tAssign Operator\n";
"+="			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tAssign Operator\n";
"-="			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tAssign Operator\n";
"*="			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tAssign Operator\n";
"/="			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tAssign Operator\n";
"%="			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tAssign Operator\n";

"-"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tArithmetic Operator\n";
"+"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tArithmetic Operator\n";
"*"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tArithmetic Operator\n";
"/"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tArithmetic Operator\n";
"%"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tArithmetic Operator\n";

"!"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tLogical Operator\n";
"&&"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tLogical Operator\n";
"||"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tLogical Operator\n";

"=="			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tRelation Operator\n";
"<"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tRelation Operator\n";
">"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tRelation Operator\n";
"<="			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tRelation Operator\n";
">="			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tRelation Operator\n";
"!="			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tRelation Operator\n";

","			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tSeparating Character\n";
";"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tSeparating Character\n";
"{"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tSeparating Character\n";
"}"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tSeparating Character\n";
"("			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tSeparating Character\n";
")"			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tSeparating Character\n";

\n			lineno++;
[ \t\r]			;//ignore white space
.			{
			cout<<seuLexLastLex<<"\t at line "<<lineno<<"\t----\tBad Character\n";
			return -1;
			}

%%

int main()
{
	ifstream in("test.c");
	seuLex(in, std::cout);
	cout<<"Lexing Ended\n";
	system("pause");
	return 0;
}
