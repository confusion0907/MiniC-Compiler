package translation;

public class test {
	public static void main(String[] args) {
		String temp = "int number ; void main ( void ) { int a ; int b ; a = 1 ; b = 2 ; c = a + b ; }";
		String []ss = temp.split(" ");
		new translation(ss);
	}
}
