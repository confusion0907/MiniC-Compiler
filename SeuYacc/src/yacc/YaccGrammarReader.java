package yacc;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Vector;

public class YaccGrammarReader {
	private String fname;
	private Vector<String> terminals;
	private Vector<String> nonterminals;
	private Vector<operators> ops;
	private Vector<producer> producers;
	
	public Vector<String> getTerminals() {
		return terminals;
	}

	public void setTerminals(Vector<String> terminals) {
		this.terminals = terminals;
	}

	public Vector<String> getNonterminals() {
		return nonterminals;
	}

	public void setNonterminals(Vector<String> nonterminals) {
		this.nonterminals = nonterminals;
	}

	public Vector<operators> getOps() {
		return ops;
	}

	public void setOps(Vector<operators> ops) {
		this.ops = ops;
	}

	public Vector<producer> getProducers() {
		return producers;
	}

	public void setProducers(Vector<producer> producers) {
		this.producers = producers;
	}
	
	public YaccGrammarReader(String fname)
	{
		this.fname = fname;
		this.nonterminals = new Vector<String>();
		this.terminals = new Vector<String>();
		this.ops = new Vector<operators>();
		this.producers = new Vector<producer>();
		
		terminals.add("#");
	}
	
	public void readYacc()
	{
		File f = new File(this.fname);
		
		try{
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			long ptr = 0;
			String str = "";
			
			while (ptr < f.length()) 
			{
				str = raf.readLine();
				ptr = raf.getFilePointer();
				
				if(str.equals("%%"))
					break;
			}
			
			str = "";
			int level = 0;
			while (ptr < f.length()) 
			{
				str = raf.readLine();
				ptr = raf.getFilePointer();
				
				if(str.equals(""))
					continue;
				else if(str.equals("%%"))
					break;
				
				str = str.substring(1);
				
				String[] ss = str.split(" ");
				String rule = ss[0];
				
				if(rule.equals("token"))
				{
					for(int i = 1 ; i < ss.length ; i++)
						terminals.add(ss[i]);
					
					continue;
				}
				
				for(int i = 1 ; i < ss.length ; i++)
				{
					if(ss[i].charAt(0) == '\'')
						ops.add(new operators(ss[i].substring(1, 2),level,rule));
					else
						ops.add(new operators(ss[i],level,rule));
				}
				
				level++;
			}
			
			str = "";
			String left = "";
			while (ptr < f.length() && !str.equals("%%")) {
				str = raf.readLine();
				ptr = raf.getFilePointer();
				
				Vector<String> right = new Vector<String>();
				String semantic = "";
				String[] ss;
				
				if(str.equals(""))
				{
					str = raf.readLine();
					ptr = raf.getFilePointer();
					
					if(str.equals("%%"))
						break;
					
					str = str.replaceAll("( )+"," ");
					ss = str.split(" ");
					
					left = ss[0];
					this.nonterminals.add(left);
					
					for(int i = 2 ; i < ss.length ; i++)
					{
						if(ss[i].charAt(0) == '{' && ss[i].length() != 1) 
						{
							for(int j = i ; j < ss.length ; j++)
							{
								semantic = semantic + ss[j];
							}
							break;
						} 
						else
						{
							right.add(ss[i]);
							
							if(!this.nonterminals.contains(ss[i]) && !this.terminals.contains(ss[i]))
								this.terminals.add(ss[i]);
						}
					}
				}
				else
				{
					str = str.replaceAll("( )+"," ");
					ss = str.split(" ");
					
					for(int i = 2 ; i < ss.length ; i++)
					{
						if(ss[i].charAt(0) == '{' && ss[i].length() != 1) 
						{
							for(int j = i ; j < ss.length ; j++)
							{
								semantic = semantic + ss[j];
							}
							break;
						} 
						else
						{
							right.add(ss[i]);
							
							if(!this.nonterminals.contains(ss[i]) && !this.terminals.contains(ss[i]))
								this.terminals.add(ss[i]);
						}
					}
				}
				
				semantic = semantic.substring(1, semantic.length()-1);
				
				Vector<String> sem = new Vector<String>();
				
				ss = semantic.split(";");
				for(int i = 0 ; i < ss.length ; i++)
					sem.add(ss[i]);
				
				this.producers.add(new producer(left,right,sem));
			}
			
			for(int i = 0 ; i < this.nonterminals.size() ; i++)
				this.terminals.remove(this.nonterminals.get(i));
			
			raf.close();
		}catch (Exception e) {
			System.out.println("Have an error");
			e.printStackTrace();
		}
			
	}
	
	public Vector<regularGrammar> grammarGenerating()
	{
		Vector<regularGrammar> result = new Vector<regularGrammar>();
		for(int i=0;i<producers.size();i++)
		{
			regularGrammar newGrammar=new regularGrammar();

			newGrammar.dot=0;
			newGrammar.left=producers.get(i).left;
			newGrammar.right.addAll(producers.get(i).right);

			result.add(newGrammar);
		}
		return result;
	}
}

class producer
{
	public String left;
	public Vector<String> right;
	public Vector<String> semantic;
	
	public producer(String left,Vector<String> right,Vector<String> semantic)
	{
		this.left = left;
		this.right = right;
		this.semantic = semantic;
		
		right = new Vector<String>();
		semantic = new Vector<String>();
	}
}

class operators
{
	public String op;
	public int level;
	public String rule;
	
	public operators(String op,int level,String rule)
	{
		this.op = op;
		this.level = level;
		this.rule = rule;
	}
}
