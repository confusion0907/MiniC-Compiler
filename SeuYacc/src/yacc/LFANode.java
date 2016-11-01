package yacc;

import java.util.Vector;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class LFANode {
	private int stateID;
	private Vector<regularGrammar> RG;
	private Map<String, Integer> Action;
	private Map<String, Integer> Goto;
	
	public LFANode(int ID)
	{
		stateID=ID;
		RG = new Vector<regularGrammar>();
		Action = new HashMap<String, Integer>();
		Goto = new HashMap<String, Integer>();
	}
	
	public LFANode(LFANode m_LFANode)
	{
		RG = new Vector<regularGrammar>();
		Action = new HashMap<String, Integer>();
		Goto = new HashMap<String, Integer>();
		
		this.stateID = m_LFANode.stateID;
		for(int i = 0 ; i < m_LFANode.RG.size() ; i++)
			this.RG.add((regularGrammar)m_LFANode.RG.get(i).clone());
		
		Iterator<Entry<String, Integer>> it = m_LFANode.getAction().entrySet().iterator();
		while(it.hasNext())
		{
			Entry<String, Integer> entry = it.next();
			this.Action.put(entry.getKey(), entry.getValue());
		}
		
		Iterator<Entry<String, Integer>> it2 = m_LFANode.getGoto().entrySet().iterator();
		while(it2.hasNext())
		{
			Entry<String, Integer> entry = it2.next();
			this.Goto.put(entry.getKey(), entry.getValue());
		}
	}
	
	private int nonterminalsCalculation(String temp,Vector<regularGrammar> grammar)
	{
		int index = 0;
		for(int i = 0 ; i < grammar.size() ; i++)
		{
			if(grammar.get(i).left.equals(temp))
				index++;
		}
		return index;
	}

	private Vector<String> first(String temp,Vector<String> terminals,Vector<String> nonterminals,Vector<regularGrammar> grammar)
	{
		Vector<String> result = new Vector<String>();
		Vector<String> done = new Vector<String>();
		Vector<Integer> number = new Vector<Integer>();
		if(terminals.contains(temp))
			result.add(new String(temp));
		else if(nonterminals.contains(temp))
		{
			for(int i = 0 ; i < grammar.size() ; i++)
			{
				if(grammar.get(i).left.equals(temp))
				{
					done.add(new String(temp));
					number.add(1);
					Vector<String> resulttemp = first(grammar.get(i).right.get(0),terminals,nonterminals,grammar,done,number);
					for(int j = 0 ; j < resulttemp.size() ; j++)
						result.add(new String(resulttemp.get(j)));
				}
			}
		}
		return result;
	}
	
	private Vector<String> first(String temp,Vector<String> terminals,Vector<String> nonterminals,Vector<regularGrammar> grammar,Vector<String> done,Vector<Integer> number)
	{
		Vector<String> result = new Vector<String>();
		if(terminals.contains(temp))
			result.add(new String(temp));
		else if(nonterminals.contains(temp))
		{
			for(int i = 0 ; i < grammar.size() ; i++)
			{
				if(grammar.get(i).left.equals(temp) && !done.contains(temp))
				{
					done.add(new String(temp));
					number.add(1);
					Vector<String> resulttemp = first(grammar.get(i).right.get(0),terminals,nonterminals,grammar,done,number);
					for(int j = 0 ; j < resulttemp.size() ; j++)
						result.add(new String(resulttemp.get(j)));
				}
				else if(grammar.get(i).left.equals(temp) && done.contains(temp))
				{
					int indextemp = nonterminalsCalculation(temp,grammar);
					boolean judge = true;
					for(int j = 0 ; j < done.size() ; j++)
					{
						if(done.get(j).equals(temp))
						{
							if(number.get(j) >= indextemp)
								judge = false;
							else
								number.set(j, number.get(j)+1);
						}
					}
					
					if(judge == false)
						continue;
					else
					{
						Vector<String> resulttemp = first(grammar.get(i).right.get(0),terminals,nonterminals,grammar,done,number);
						for(int j = 0 ; j < resulttemp.size() ; j++)
							result.add(new String(resulttemp.get(j)));
					}
				}
			}
		}
		return result;
	}

	private Vector<String> follow(String temp,Vector<String> terminals,Vector<String> nonterminals,Vector<regularGrammar> grammar)
	{
		Vector<String> result = new Vector<String>();
		for(int i = 0 ; i < grammar.size() ; i++)
		{
			if(grammar.get(i).dot != grammar.get(i).right.size() && grammar.get(i).right.get(grammar.get(i).dot).equals(temp) && grammar.get(i).dot != grammar.get(i).right.size()-1)
			{
				Vector<String> resulttemp = new Vector<String>(first(grammar.get(i).right.get(grammar.get(i).dot+1),terminals,nonterminals,grammar));
				for(int j = 0 ; j < resulttemp.size() ; j++)
					result.add(new String(resulttemp.get(j)));
			}
			else if(grammar.get(i).dot == grammar.get(i).right.size()-1)
			{
				for(int j = 0 ; j < grammar.get(i).prediction.size() ; j++)
					result.add(new String(grammar.get(i).prediction.get(j)));
			}
		}
		return result;
	}

	public void AddRegularGrammar(regularGrammar regularG)
	{
		RG.add(regularG);
	}

	public int getStateID()
	{
		return stateID;
	}

	public void AddAction(String temp,int nodeID)
	{
		Action.put(temp, nodeID);
	}

	public void AddGoto(String temp,int nodeID)
	{
		Goto.put(temp,nodeID);
	}

	public void AddRegularGrammar(String left,Vector<String> right)
	{
		regularGrammar regularG = new regularGrammar();
		regularG.left = left;
		for(int i = 0 ; i < right.size() ; i++)
			regularG.right.add(new String(right.get(i)));
		regularG.dot = 0;

		RG.add(regularG);
	}

	public Vector<regularGrammar> getRegularGrammar()
	{
		return RG;
	}

	private boolean isInNode(regularGrammar regularG)
	{
		boolean judge = true;
		for(int i = 0 ; i < RG.size() ; i++)
		{
			judge = true;
			if(RG.get(i).left.equals(regularG.left) && RG.get(i).dot == 0 && RG.get(i).right.size() == regularG.right.size())
			{
				int k = 0;
				for(int j = 0 ; j < RG.get(i).right.size() ; j++)
				{
					if(!RG.get(i).right.get(j).equals(regularG.right.get(k)))
						judge = false;

					k++;
				}
			}
			else
				judge = false;

			if(judge == true)
				break;
		}
		return judge;
	}

	public void clusureCalculation(Vector<String> terminals,Vector<String> nonterminals,Vector<regularGrammar> grammar)
	{
		Vector<regularGrammar> clusure = new Vector<regularGrammar>();

		for(int i = 0 ; i < RG.size() ; i++)
			clusure.add((regularGrammar)RG.get(i).clone());

		while(!clusure.isEmpty())
		{
			regularGrammar temp = new regularGrammar(clusure.get(0));
			clusure.remove(0);
			
			for(int j = 0 ; j<grammar.size() ; j++)
			{
				if(temp.dot != temp.right.size())
				{
					if(grammar.get(j).left.equals(temp.right.get(temp.dot)) && !isInNode(grammar.get(j)))
					{
						AddRegularGrammar((regularGrammar)grammar.get(j).clone());
						clusure.add((regularGrammar)RG.get(RG.size()-1).clone());
					}
				}
			} 
		}
	}

	void precitionCalculation(Vector<String> terminals,Vector<String> nonterminals)
	{
		for(int i = 0 ; i < RG.size() ; i++)
		{
			if(RG.get(i).dot == 0)
				RG.get(i).prediction=new Vector<String>(follow(RG.get(i).left,terminals,nonterminals,RG));
		}
		
		for(int i = 0 ; i<RG.size() ; i++)
		{
			HashSet<String> hs = new HashSet<String>(RG.get(i).prediction);
			
			RG.get(i).prediction.clear();
			RG.get(i).prediction.addAll(hs);
		}
	}

	public Map<String,Integer> getAction()
	{
		return Action;
	}

	public Map<String,Integer> getGoto()
	{
		return Goto;
	}
}

class regularGrammar implements Cloneable 
{
	String left;
	Vector<String> right;
	int dot;
	Vector<String> prediction;
	
	public regularGrammar()
	{
		right = new Vector<String>();
		prediction = new Vector<String>();
	}
	
	public regularGrammar(regularGrammar regularG)
	{
		right = new Vector<String>();
		prediction = new Vector<String>();
		
		this.left = regularG.left;
		
		for(int i = 0 ; i < regularG.right.size() ; i++)
			this.right.add(new String(regularG.right.get(i)));
		
		this.dot = regularG.dot;
		
		for(int i = 0 ; i < regularG.prediction.size() ; i++)
			this.prediction.add(new String(regularG.prediction.get(i)));
	}
	
	public Object clone() 
	{   
		regularGrammar o = null;   
		try {   
			o = (regularGrammar) super.clone();  
		}
		catch (CloneNotSupportedException e) {   
			e.printStackTrace();   
		} 
		return o;   
	}
}
