import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;



public class Table {
	private HashSet<Integer> t[][];
	private int numRows;
	public static final int NUM_OF_COLUMNS = 128;
	
	@SuppressWarnings("unchecked")
	public Table(int pNumRows) {
		numRows = pNumRows;
		t = new HashSet[numRows][NUM_OF_COLUMNS];
	}
	
	/**
	 * Set an entire row of a the table
	 * @param row
	 * @param column
	 * @param items
	 */
	public void set(int row, char column, HashSet<Integer> items) {
		t[row][column] = items;
	}
	
	/**
	 * Add an element to the table
	 * @param row
	 * @param column
	 * @param item
	 */
	public void add(int row, int column, int item) {
		if ( t[row][column] == null) {
			t[row][column] = new HashSet<Integer>();
		}
		t[row][column].add(item);
	}
	
	
	/**
	 * Get all the elements of an item in the table
	 * @param row
	 * @param column
	 * @return
	 */
	public Set<Integer> get(int row, char column) {
		return t[row][column];
	}
	
	/**
	 * Copy a line from another table by reference
	 * @param from
	 * @param fromLine
	 * @param toLine
	 */
	public void copyLineByRef(Table from, int fromLine, int toLine) {
		t[toLine] = from.t[fromLine];
	}
	
	/**
	 * Print the table to standard out for test
	 */
	public void print()	{
		for ( int i=0; i<numRows; i++ ) {
			for ( int j=0; j<NUM_OF_COLUMNS; j++ ) {
				if ( t[i][j] != null ) {
					Iterator<Integer> k = t[i][j].iterator();
					while ( k.hasNext() ){
						System.out.println(i+"\t"+k.next()+"\t"+j);
					}
				}
			}
		}
	}
	
	public int getNumRows() {
		return numRows;
	}
	
}
