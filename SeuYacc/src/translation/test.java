package translation;

public class test {
	public static void main(String[] args) {
		myYacc my = new myYacc();
		myLex ml = new myLex();
		
		String temp = "int c [ 2 ] ; int d ; void main ( void ) { int a ; int b ; int c ; a = 1 ; b = 2 ; c = a + b ; if ( c == 3 ) return c ; else { c = 0x11 ; return c ; } } void debug ( void ) { }";
		
		String []ss = temp.split(" ");
		String []s = ml.LexAnalysis(ss);
		
		String []sss = new String[s.length+1];
		for(int i = 0 ; i < s.length ; i++)
			sss[i] = s[i];
		
		sss[s.length] = "#";
		
		my.YaccAnalysis(sss);
	}
}
