public class MergedNFA extends NFA {
	private int finalStates[];
	/**
	 * Construct a MergedNFA from an array of small NFAs
	 * @param nfas
	 */
	public MergedNFA (NFA nfas[]) {
		finalStates = new int[nfas.length];
		numState = 1;
		for ( int i=0; i<nfas.length; i++ ) {
			nfas[i].shift(numState);
			numState += nfas[i].numState;
			finalStates [i] = numState-1;
		}
		trsTbl = new Table(numState);
		int k = 1;
		for ( int i=0; i<nfas.length; i++ ) {
			trsTbl.add(0, EPSILON, k);
			for ( int j=0; j<nfas[i].numState; j++ ) {
				trsTbl.copyLineByRef(nfas[i].trsTbl, j, k);
				k++;
			}
		}
	}
	
	/**
	 * Get corresponding pattern ID of a final state.
	 * -1 indicate the state is not a final state
	 * @param s
	 * @return
	 */
	public int statePatten(int s) {
		for ( int i=0; i<finalStates.length; i++ ) {
			if ( finalStates[i] == s)
				return i;
		}
		return -1;
	}
	
	/**
	 * Print the MergedNFA to standard output for test
	 */
	public void print() {
		super.print();
		System.out.print("Final States are: ");
		for ( int i=0; i<finalStates.length; i++ ) {
			System.out.print(finalStates[i]+ " ");
		}
		System.out.println();
	}
	
	
}