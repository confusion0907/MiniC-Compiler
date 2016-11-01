 import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;


public class NFA {
	protected Table trsTbl;
	protected int numState;
	private static final int NUM_OF_SYMBOLS = Table.NUM_OF_COLUMNS;
	protected static final char EPSILON = AppData.EPSILON;
	
	/**
	 * Ä¬ÈÏNFA
	 */
	public NFA() {
		
	}
	
	/**
	 * n¸ö×´Ì¬µÄNFA
	 * @param _numState number of states
	 */
	private NFA(int _numState) {
		numState = _numState;
		trsTbl = new Table(numState);
	}
	
	/**
	 * Construct an NFA from a regular expression
	 * @param regExpr
	 */
	public NFA( String regExpr , lexGrammarReader lfr) {
		Infix2postfixTransformer ipt = new Infix2postfixTransformer(lfr);
		String postReg = ipt.postfix(regExpr);
		Stack<Object> s = new Stack<Object>();
		NFA n = null;
		try {
			for ( int i=0; i<postReg.length(); i++ ) {
				char symble = postReg.charAt(i);
				if ( symble == AppData.AND ) {
					n = (NFA)s.pop();
					NFA m = (NFA)s.pop();
					s.push( and(m, n) );
				} else if ( symble == AppData.OR ) {
					n = (NFA)s.pop();
					NFA m = (NFA)s.pop();
					s.push(or(m,n));
				} else if ( symble == AppData.STAR ) {
					n = (NFA)s.pop();
					s.push(star(n));
				} else if ( symble == AppData.ONE_OR_MORE ){
					n = (NFA)s.pop();
					s.push(oneMore(n));
				} else if ( symble == AppData.ONE_OR_NONE ){
					n = (NFA)s.pop();
					s.push(oneNone(n));
				} else {
					n = new NFA(2);
					n.trsTbl.add(0, symble, 1);
					s.push(n);
				}
			}
			if ( s.empty() ) {
				System.err.println("Regular Expression Presentation Error!");
				System.exit(1);
			}
			n = (NFA)s.pop();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			System.err.println("Regular Expression Presentation Error!");
			System.exit(1);
		}
		trsTbl = n.trsTbl;
		numState = trsTbl.getNumRows();
	}
	
	/**
	 * Construct a NFA with or operation
	 * @param m
	 * @param n
	 * @return
	 */
	protected NFA or(NFA m, NFA n) {
		NFA p = new NFA(m.numState+n.numState+2);
		if (AppData.TRUE){
			System.out.println("m");
			m.print();
			System.out.println("n");
			n.print();
		}
			
		m.shift(1);
		n.shift(m.numState+1);
		for ( int i=0; i<m.numState; i++ ){
			p.trsTbl.copyLineByRef(m.trsTbl, i, i+1);
		}
		for ( int i=0; i<n.numState; i++) {
			p.trsTbl.copyLineByRef(n.trsTbl, i, m.numState+1+i);
		}
		p.trsTbl.add(0, EPSILON, 1);
		p.trsTbl.add(0, EPSILON, m.numState+1);
		p.trsTbl.add(m.numState, EPSILON, p.numState-1);
		p.trsTbl.add(p.numState-2, EPSILON, p.numState-1);
		return p;
	}
	
	/**
	 * Construct a NFA with and operation
	 * @param m
	 * @param n
	 * @return
	 */
	private NFA and(NFA m, NFA n) {
		NFA p = new NFA(m.numState+n.numState-1);
		n.shift(m.numState-1);
		for ( char c=0; c<Table.NUM_OF_COLUMNS; c++ ) {
			Set<Integer> s = n.trsTbl.get(0, c);
			if ( s!=null ) {
				Iterator<Integer> i = s.iterator();
				while ( i.hasNext() ) {
					m.trsTbl.add(m.numState-1, c, i.next());
				}
			}
		}
		for ( int i=0; i<m.numState; i++ ) {
			p.trsTbl.copyLineByRef(m.trsTbl, i, i);
		}
		for ( int i=1; i<n.numState; i++) {
			p.trsTbl.copyLineByRef(n.trsTbl, i, m.numState+i-1);
		}
	//	p.trsTbl.add(m.numState-1, EPSILON, m.numState);
		return p;
	}
	
	/**
	 * Construct a NFA with star operation
	 * @param m
	 * @return
	 */
	private NFA star(NFA m) {
		NFA p = new NFA(m.numState+2);
		m.shift(1);
		for ( int i=0; i<m.numState; i++ ){
			p.trsTbl.copyLineByRef(m.trsTbl, i, i+1);
		}
		p.trsTbl.add(0, EPSILON, 1);
		p.trsTbl.add(0, EPSILON, p.numState-1);
		p.trsTbl.add(p.numState-2, EPSILON, 1);
		p.trsTbl.add(p.numState-2, EPSILON, p.numState-1);
		return p;
	}
	
	
	/**
	 * Construct a NFA with + operation
	 * @param m
	 * @return
	 */
	private NFA oneMore(NFA m) {
		NFA p = new NFA(m.numState+2);
		m.shift(1);
		for ( int i=0; i<m.numState; i++ ){
			p.trsTbl.copyLineByRef(m.trsTbl, i, i+1);
		}
		p.trsTbl.add(0, EPSILON, 1);
		p.trsTbl.add(p.numState-2, EPSILON, 1);
		p.trsTbl.add(p.numState-2, EPSILON, p.numState-1);
		return p;
	}
	
	/**
	 * Construct a NFA with ? operation
	 * @param m
	 * @return
	 */
	private NFA oneNone(NFA m) {
		m.trsTbl.add(0, EPSILON, m.numState-1);
		return m;
	}
	
	
	/**
	 * Increase the values of transition table by s
	 * @param s increment
	 */
	protected void shift(int s) {
		for ( int i=0; i<numState; i++ ) {
			for ( char symb=0; symb<NUM_OF_SYMBOLS; symb++) {
				Set<Integer> items = trsTbl.get(i,symb);
				if ( items == null ) {
					continue;
				}
				HashSet<Integer> newItems = new HashSet<Integer>();
				Iterator<Integer> j = items.iterator();
				while ( j.hasNext() ) {
					newItems.add(j.next()+s);
				}
				trsTbl.set(i, symb, newItems);
			}
		}
	}
	
	
	/**
	 * Get the transition table of the NFA
	 * @return
	 */
	protected Table getTrsTbl() {
		return trsTbl;
	}
	
	/**
	 * Print the NFA to standard output for test
	 */
	public void print() {
		trsTbl.print();
	}
	
	/**
	 * Get a set of NFA states while the NFA receive a character
	 * @param state
	 * @param symble
	 * @return
	 */
	public Set<Integer> move( int state, char symble ) {
		Set<Integer> dess =  trsTbl.get(state, symble);
		if ( dess == null ) {
			dess = new HashSet<Integer>();
		}
		return dess;
	}
	

}
