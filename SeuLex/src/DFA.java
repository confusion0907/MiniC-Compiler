import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;



public class DFA {
	private MergedNFA nfa;
	private int trsTbl[][];
	private int numStates;
	private int finalStatePatten[];
	private static final int NUM_OF_SYMBOLS = Table.NUM_OF_COLUMNS;
	private HashMap<Integer, HashSet<Integer>> espClsTbl;
	
	@SuppressWarnings("unchecked")
	public DFA( MergedNFA pnfa) {
		espClsTbl = new HashMap<Integer, HashSet<Integer>>();
		if (AppData.TRUE)
			pnfa.print();
		nfa = pnfa;
		HashMap<Set<Integer>, Set<Integer>[]> Dtran = new HashMap<Set<Integer>, Set<Integer>[]>();
		HashMap<Set<Integer>, Integer> Dstates = new HashMap<Set<Integer>, Integer>();
		LinkedList<Set<Integer>> UnmarkedDstates = new LinkedList<Set<Integer>>();
		Set<Integer> eps0 = epsilonClosure( 0 );
		Dstates.put( eps0, 0 );
		UnmarkedDstates.add(epsilonClosure(0));
		Dtran.put(eps0, new HashSet[NUM_OF_SYMBOLS]);
		while ( !UnmarkedDstates.isEmpty() ) {
			Set<Integer> T = UnmarkedDstates.poll();
			for ( char a=1; a<NUM_OF_SYMBOLS; a++ ) {
				Set<Integer> U = epsilonClosure( move(T, a) );
				if ( U==null || U.isEmpty() )
					continue;
				if ( !Dstates.containsKey(U) ) {
					Dstates.put(U, Dstates.size());
					UnmarkedDstates.push(U);
					Dtran.put(U, new HashSet[NUM_OF_SYMBOLS]);
				}
				Set<Integer>[] r = Dtran.get(T);
				r[a] = U;
			}
		}
		
		numStates = Dtran.size();
		trsTbl = new int[numStates][NUM_OF_SYMBOLS];
		finalStatePatten = new int[numStates];
		for ( int i=0; i<numStates; i++ ) {
			finalStatePatten[i] = -1;
			for ( int j=0; j<NUM_OF_SYMBOLS; j++ ) {
				trsTbl[i][j] = -1;
			}
		}
		Set<Set<Integer>> ks = Dtran.keySet();
		Iterator<Set<Integer>> i = ks.iterator();
		while ( i.hasNext() ) {
			Set<Integer> k = i.next();
			int kn = Dstates.get(k);
			finalStatePatten[kn] = statePatten(k);
			if ( AppData.TRUE)
				System.out.println(kn+"\t"+k.toString());
			Set<Integer>[] r = Dtran.get(k);
			for ( int a=0; a<NUM_OF_SYMBOLS; a++ ) {
				if ( r[a]!=null && !r[a].isEmpty())
					trsTbl[kn][a] = Dstates.get(r[a]);
			}
		}
	}
	

	public int[][] getTable() {
		return trsTbl;
	}
	
	public int[] getStatePattern() {
		return finalStatePatten;
	}
	private int statePatten( Set<Integer> k ) { 
		int min = Integer.MAX_VALUE;
		int pid = -1;
		Iterator<Integer> i = k.iterator();
		while ( i.hasNext() ) {
			int currentState = i.next();
			int cpid = nfa.statePatten(currentState);
			if ( cpid>=0 && currentState<min ) {
				pid = cpid;
				min = currentState;
			}
		}
		return pid;
	}
	
	private Set<Integer> move(Set<Integer> T, char a) {
		HashSet<Integer> dess = new HashSet<Integer>();
		Iterator<Integer> i = T.iterator();
		while ( i.hasNext() ) {
			dess.addAll(nfa.move(i.next(), a));
		}
		return dess;
	}
	
	private Set<Integer> epsilonClosure ( int i ) {
		if (espClsTbl.containsKey(i)) {
			return espClsTbl.get(i);
		}
		HashSet<Integer> epsCls = new HashSet<Integer>();
		epsCls.add(i);
		Stack<Integer> s = new Stack<Integer>();
		s.push(i);
		while ( !s.empty() ) {
			int t = s.pop();
			Set<Integer> epsTrs = nfa.move(t, NFA.EPSILON);
			Iterator<Integer> itEspTrs = epsTrs.iterator();
			while ( itEspTrs.hasNext() ) {
				int u = itEspTrs.next();
				if ( !epsCls.contains(u) )
					s.push(u);
				epsCls.add(u);
			}
		}
		espClsTbl.put(i, epsCls);
		return epsCls;
	}
	
	private Set<Integer> epsilonClosure( Set<Integer> T ) {
		HashSet<Integer> epsCls = new HashSet<Integer>(T);
		Iterator<Integer> i =  T.iterator();
		while ( i.hasNext() ) {
			epsCls.addAll(epsilonClosure(i.next()));
		}
		return epsCls;
	}
	
	public void print() {
		for ( int i=0; i<numStates; i++ ) {
			for ( int j=0; j<NUM_OF_SYMBOLS; j++ ) {
				if ( trsTbl[i][j] >=0 ) {
					System.out.println(i + "\t" + trsTbl[i][j] + "\t" + j);
				}
			}
		}
		System.out.print("Final States are: ");
		for ( int i=0; i<numStates; i++ ) {
			if ( finalStatePatten[i] >=0 )
				System.out.print(i+" ");
		}
		System.out.println();
	}
	
	@SuppressWarnings("unchecked")
	public void minimize() {
		HashSet<HashSet<Integer>> pi = new HashSet<HashSet<Integer>>();
		int max = -1;
		for ( int x=0; x<numStates; x++ ) {
			if ( finalStatePatten[x] > max ) {
				max =  finalStatePatten[x];
			}
		}
		HashSet<Integer> nonfinalStates = new HashSet<Integer>();
		HashSet<Integer> finalStates[] = new HashSet[max+1];
		for ( int x=0; x<=max; x++ ) {
			finalStates[x] = new HashSet<Integer>();
		}
		for ( int s = 0; s<numStates; s++ ) {
			if ( finalStatePatten[s]<0 ) {
				nonfinalStates.add(s);
			} else {
				finalStates[finalStatePatten[s]].add(s);
			}
		}
		if ( !nonfinalStates.isEmpty()) {
			pi.add(nonfinalStates);
		}
		for ( int x=0; x<=max; x++ ) {
			if ( !finalStates[x].isEmpty() ) {
				pi.add(finalStates[x]);
			}
		}
		while ( partition(pi) );
		HashMap<Integer, Integer> repMap = new HashMap<Integer, Integer>();
		repMap.put(-1, -1);
		Iterator<HashSet<Integer>> ipi = pi.iterator();
		int count = 0;
		while ( ipi.hasNext() ) {
			Iterator<Integer> i = ipi.next().iterator();
			int rep  = i.next();
			repMap.put(rep, count);
			while ( i.hasNext() ) {
				repMap.put(i.next(), count);
			}
			count++;
		}
		int newTrsTbl[][] = new int[pi.size()][Table.NUM_OF_COLUMNS];
		int newStatePattern[] = new int[pi.size()];
		ipi = pi.iterator();
		while ( ipi.hasNext() ) {
			Iterator<Integer> i = ipi.next().iterator();
			int oldNum  = i.next();
			int newNum = repMap.get(oldNum);
			newStatePattern[newNum] = finalStatePatten[oldNum];
			for ( int c=0; c<Table.NUM_OF_COLUMNS; c++ ) {
				newTrsTbl[newNum][c] = repMap.get(trsTbl[oldNum][c]);
			}
		}
		numStates = pi.size();
		trsTbl = newTrsTbl;
		finalStatePatten = newStatePattern;
	}
	
	private boolean partition( HashSet<HashSet<Integer>> pi ) {
		boolean partitioned = false;
		Stack<HashSet<Integer>> stack =new Stack<HashSet<Integer>>();
		Iterator<HashSet<Integer>> ipi = pi.iterator();
		while ( ipi.hasNext() ) {
			HashSet<Integer> G = ipi.next();
			if ( G.size()<=1 )
				continue;
			Iterator<Integer> i = G.iterator();
			while ( i.hasNext() ) {
				if (G.size()<=1)
					break;
				int s = i.next();
				for ( int c=1; c<Table.NUM_OF_COLUMNS; c++ ) {
					if ( trsTbl[s][c]>=0 && !G.contains(trsTbl[s][c]) ) {
						i.remove();
						HashSet<Integer> newg = new HashSet<Integer>();
						newg.add(s);
						stack.push(newg);
						partitioned = true;
						break;
					}
				}
			}
		}
		while ( !stack.isEmpty()) {
			pi.add(stack.pop());
		}
		return partitioned;
	}
	
	public int getNumStates() {
		return numStates;
	}

}
