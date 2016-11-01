import java.util.Stack;


public class Infix2postfixTransformer {

	lexGrammarReader lexFileReader;
	public Infix2postfixTransformer(lexGrammarReader lf) {
		lexFileReader = lf ;
	}
	
	/**
	 * Convert a regular expression in infix to its equivalent postfix format.
	 * @param regExpr
	 * @return
	 */
	public String postfix(String regExpr) {
		regExpr = preProcess(regExpr);
		String postFixExpr = "";
		Stack<Character> s = new Stack<Character>();
		try {
			for ( int i=0; i<regExpr.length(); i++) {
				char c = regExpr.charAt(i);
				if ( c == '\\' ) {
					i++;
					postFixExpr += escape(regExpr.charAt(i));
				} else if ( c=='(' ) {
					s.push('(');
				} else if ( c=='*' ) {
					postFixExpr += AppData.STAR;
				} else if ( c=='+' ) {
					postFixExpr += AppData.ONE_OR_MORE;
				} else if ( c=='?' ) {
					postFixExpr += AppData.ONE_OR_NONE;
				} else if ( c==')' ) {
					while ( s.peek()!= '(' ) {
						postFixExpr += innerCode(s.pop());
					}
					s.pop();
				} else if ( c=='|' ) {
					while ( !s.isEmpty() && s.peek()!='(' ) {
						postFixExpr += innerCode(s.pop());
					}
					s.push('|');
				} else {
					postFixExpr += c;
				}
				
				if ( i+2<=regExpr.length() ) {
					char d = regExpr.charAt(i+1);
					if ( !isUniOp(d) && !isBinOp(c) && !isBinOp(d) && c!='(' && d!=')' ) {
						while ( !s.isEmpty() && s.peek()=='.' ) {
							postFixExpr += innerCode(s.pop());
						}
						s.push('.');
					}
				}
				
			}
		} catch (RuntimeException e) {
			System.err.println("Regular Expression Presentation Error!");
			System.exit(1);
		}
		while ( !s.isEmpty() ) {
			postFixExpr += innerCode(s.pop());
		}
		if (AppData.TRUE)
			System.out.println(postFixExpr);
		return postFixExpr;
	}

	private String preProcess(String s) {
		String re = "";
		for ( int i=0; i<s.length(); i++ ) {
			char c = s.charAt(i);
			if ( c == '\\' ) {
				char d = s.charAt(i+1);
				i++;
				if ( d!='[' && d!='}' && d!='"')
					re += '\\';
				re += d;
			} else if ( c == '.' ) {
				re += proBrakets((char)1+"-"+(char)127);
			} else if ( c == '[' ) {
				String bs = "";
				while ( s.charAt(++i)!=']' ) {
					bs += s.charAt(i);
				}
				re += proBrakets(bs);
			} else if ( c == '{' ) {
				String regDef = "";
				while ( s.charAt(++i)!='}' ) {
					regDef += s.charAt(i);
				}
				if ( AppData.TRUE)
					System.out.println(lexFileReader.getRegDef(regDef));
				regDef = lexFileReader.getRegDef(regDef);
				if ( regDef.isEmpty() ) {
					System.err.println("Regular Expression Presentation Error!");
					System.exit(1);
				}
				re += regDef;
			} else if ( c == '"') {
				while ( s.charAt(++i)!='"' ) {
					if( isRegOp(s.charAt(i))  || s.charAt(i)=='(' ||s.charAt(i)==')' || s.charAt(i)=='\\') {
						re+= '\\';
					}
					re += s.charAt(i);
				}
			} else {
				re += c;
			}
		}
		if (AppData.TRUE)
			System.out.println(re);
		return re;
	}

	private String proBrakets( String s ) {
		String pb = "(";
		char b = 0;
		char c;
		for ( int i=0; i<s.length(); i++ ) {
			c = s.charAt(i);
			if ( c == '-' ) {
				c = s.charAt(i+1);
				i++;
				for ( char x = ++b; x<=c; x++ ) {
					if ( isRegOp(x) || x=='(' || x==')'){
						pb += '\\';
					}
					pb += x;
					if ( x<c ) {
						pb += '|';
					}
				}
			} else {
				pb += c;
				if ( c == '\\' ) {
					pb += s.charAt(i+1);
					i++;
				}
				b = c;
			}
			
			if ( i+1<s.length() ) {
				pb += '|';
			}
		}
		pb+=')';
		return pb;
	}
	
	private char escape( char d ) {
		if ( d=='n' ) {
			return '\n';
		} else if ( d=='r' ) {
			return '\r';
		} else if ( d=='t' ) {
			return '\t';
		} else {
			return d;
		}
	}

	/**
	 * Test if the character is an operator
	 * @param c
	 * @return
	 */
	private boolean isRegOp(char c) {
		if ( c=='*' )
			return true;
		else if ( c=='+' )
			return true;
		else if ( c=='?' )
			return true;
		else if ( c=='|' )
			return true;
		else
			return false;
	}
	
	private boolean isUniOp(char c) {
		if ( c=='*' )
			return true;
		else if ( c=='+' )
			return true;
		else if ( c=='?' )
			return true;
		else
			return false;
	}

	private boolean isBinOp(char c) {
		if ( c=='|' )
			return true;
		else 
			return false;
	}
	public char innerCode( char c ) {
		if ( c=='*' )
			return AppData.STAR;
		if ( c=='+' )
			return AppData.ONE_OR_MORE;
		if ( c=='?')
			return AppData.ONE_OR_NONE;
		if ( c=='.')
			return AppData.AND;
		if ( c=='|')
			return AppData.OR;
		
		return c;
	}
	
	
}
