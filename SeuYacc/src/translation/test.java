package translation;

public class test {
	public static void main(String[] args) {
		String temp = "int number ; int c [ 3 ] ; int debug ( int a ) { int b ; b = 2 ; return a + b ; } void main ( void ) { int a ; int b ; a = 1 ; b = 2 ; c [ 0 ] = a + b ; if ( a == b ) a = debug ( b ) ; else { a = 4 ; b = 5 ; } }";
		String []ss = temp.split(" ");
		new translation(ss);
	}
}
