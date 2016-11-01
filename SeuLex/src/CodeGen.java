import java.io.FileWriter;
import java.io.IOException;


public class CodeGen {
	public CodeGen() {
		try {
			fileWriter = new FileWriter("temp.data");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		genInclude("iostream");
		genInclude("stack");
		genInclude("deque");
		genInclude("cassert");
		genInclude("string");
	}
	
	public void genDriver(String rules[]) {
		writeln("std::string seuLexLastLex;");
		writeln("int seuLex(String temp)");
		writeln("{");
		writeln("int currentState = 0;");
		writeln("int matchedState = 0;");
		writeln("int currentLength = 0;");
		writeln("int matchedLength = 0;");
		writeln("seuLexLastLex = temp;");
		writeln("int index = 0;");
		writeln("char c;");
		writeln("std::stack<int> s;");
		writeln("std::deque<char> q;");
		writeln("while ( currentState!=-1 && index<seuLexLastLex.length() )");
		writeln("{");
		writeln("c = temp.charAt(index++);");
		writeln("q.push_back(c);");
		writeln("currentLength++;");
		writeln("currentState = TABLE[currentState][c];");
		writeln("if ( STATE_PATTERN[currentState] != -1 )");
		writeln("{");
		writeln("matchedState = currentState;");
		writeln("matchedLength = currentLength;");
		writeln("}");
		writeln("}");
		writeln("if ( matchedLength>0 )");
		writeln("{");
		writeln("while ( currentLength>matchedLength )");
		writeln("{");
		writeln("cin.putback(q.back());");
		writeln("q.pop_back();");
		writeln("currentLength--;");
		writeln("}");
		writeln("while ( !q.empty() )");
		writeln("{");
		writeln("seuLexLastLex += q.front();");
		writeln("q.pop_front();");
		writeln("}");
		writeln("switch ( STATE_PATTERN[matchedState] )");
		writeln("{");
		for ( int i=0; i<rules.length; i++ ) {
			write("case "+ i +":\n");
			write(rules[i]+"\n");
			write("break;\n");
		}
		writeln("default:");
		writeln("assert(false);");
		writeln("}");
		writeln("}");
		writeln("else ");
		writeln("{");
		writeln("return -1;");
		writeln("}");
		writeln("return 0;");
		writeln("}");
	}
	
	public void genInclude(String head) {
		write("#include <"+head+">\n");
	}
	
	public void genConstant(int c, String name ) {
		write("int "+name+" = "+c+";\n");
	}
	
	public void genTable( int t[][], String name) {
		write("int "+"[][]"+name+" = {\n");
		for ( int i=0; i<t.length; i++ ) {
			write("\t{");
			for ( int j=0; j<t[i].length; j++ ) {
				if(j != t[i].length-1)
					write(t[i][j] + ",");
			}
			if(i != t.length-1)
				write("},\n");
			else
				write("}\n");
		}
		write("};\n");
	}
	
	public void genVector( int v[], String name ) {
		write("int "+"[]"+name+" = {");
		for ( int i=0; i<v.length; i++ ) {
			write(v[i] + ",");
		}
		write("};\n");
	}
	
	public void close() {
		try {
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void writeln( String str ) {
		write(str+"\n");
	}
	public void write( String str ) {
		try {
			fileWriter.write(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private FileWriter fileWriter;
}
