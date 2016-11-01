package yacc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

public class ParsingTable {
	private Vector<regularGrammar> grammar;
	private Vector<String> terminals;
	private Vector<String> nonterminals;
	private Vector<operators> OP;
	private LFA m_LFA;
	private Vector<ParsingTableItem> PPT;
	
	public ParsingTable(Vector<String> ter,Vector<String> nonter,Vector<regularGrammar> regularG,Vector<operators> optemp,LFA lfa)
	{
		grammar = new Vector<regularGrammar>();
		terminals = new Vector<String>();
		nonterminals = new Vector<String>();
		OP = new Vector<operators>();
		PPT = new Vector<ParsingTableItem>();
		
		regularGrammar start = new regularGrammar();
		start.dot=0;
		start.left="start";
		start.right.add(regularG.get(0).left);
		grammar.add(start);

		grammar.addAll(regularG);

		terminals.addAll(ter);
		nonterminals.addAll(nonter);
		OP.addAll(optemp);
		m_LFA=lfa;
	}

	public int regulatingStateCalculation(regularGrammar temp)
	{
		for(int i=0;i<grammar.size();i++)
		{
			boolean judge = true;
			if(grammar.get(i).left.equals(temp.left) && grammar.get(i).right.size()==temp.right.size())
			{
				int k = 0;
				for(int j = 0 ; j < grammar.get(i).right.size() ; j++)
				{
					if(!grammar.get(i).right.get(j).equals(temp.right.get(k)))
						judge = false;

					++k;
				}
			}
			else
				judge = false;

			if(judge == true)
				return i;
		}
		return -1;
	}

	private boolean isTerminals(String temp)
	{
		for(int i=0;i<terminals.size();i++)
		{
			if(terminals.get(i).equals(temp))
				return true;
		}
		return false;
	}

	private int getOpLevel(String temp)
	{
		for(int i = 0 ; i < OP.size() ; i++)
		{
			if(OP.get(i).op.equals(temp))
				return OP.get(i).level;
		}
		return -1;
	}

	private boolean isLeftAssociative(String temp)
	{
		for(int i = 0 ; i < OP.size() ; i++)
		{
			if(OP.get(i).op.equals(temp))
			{
				if(OP.get(i).rule.equals("left"))
					return true;
				else
					return false;
			}
		}
		return false;
	}

	private void modifyInformation(String temp,String information,ParsingTableItem item)
	{
		Iterator<Entry<String, String>> it = item.actions.entrySet().iterator();
		
		while(it.hasNext())
		{
			Entry<String, String> entry = it.next();
			if(entry.getKey().equals(temp))
				entry.setValue(information);
		}
	}

	public void CreatePPT()
	{
		Vector<LFANode> m_LFANode=m_LFA.getLFANodeTable();
		for(int i=0;i<m_LFANode.size();i++)
		{
			ParsingTableItem newItem=new ParsingTableItem();
			newItem.stateID=m_LFANode.get(i).getStateID();
			
			Iterator<Entry<String, Integer>> it = m_LFANode.get(i).getAction().entrySet().iterator();
			
			while(it.hasNext())
			{
				Entry<String, Integer> entry = it.next();
				
				String temp = "s" + entry.getValue();
				newItem.actions.put(entry.getKey(),temp);
			}
			
			Iterator<Entry<String, Integer>> it2 = m_LFANode.get(i).getGoto().entrySet().iterator();
			
			while(it2.hasNext())
			{
				Entry<String, Integer> entry2 = it2.next();
				newItem.gotos.put(entry2.getKey(),entry2.getValue());
			}
			
			int size=m_LFANode.get(i).getRegularGrammar().size();
			for(int j=0;j<size;j++)
			{
				regularGrammar rg=new regularGrammar(m_LFANode.get(i).getRegularGrammar().get(j));
				if(rg.dot==rg.right.size())
				{
					Map<String,String> actionsTemp = new HashMap<String,String>();
					Iterator<Entry<String, String>> it_ = newItem.actions.entrySet().iterator();
					while(it_.hasNext())
					{
						Entry<String, String> entry = it_.next();
						
						actionsTemp.put(entry.getKey(),entry.getValue());
					}
					
					for(int m=0;m<rg.prediction.size();m++)
					{
						Iterator<Entry<String, String>> it3 = actionsTemp.entrySet().iterator();
						boolean judge=false;
						
						while(it3.hasNext())
						{
							Entry<String, String> entry3 = it3.next();
							
							if(entry3.getKey().equals(rg.prediction.get(m)))
							{
								judge = true;
								
								int index=rg.dot-1;
								while(index>=0 && !isTerminals(rg.right.get(index)))
									--index;
							
								if(getOpLevel(rg.right.get(index)) > getOpLevel(entry3.getKey()))
								{
									String temp = "r" + regulatingStateCalculation(rg);
									modifyInformation(rg.prediction.get(m),temp,newItem);
								}
								else if(getOpLevel(rg.right.get(index)) == getOpLevel(entry3.getKey()))
								{
									if(isLeftAssociative(entry3.getKey()))
									{
										String temp = "r" + regulatingStateCalculation(rg);
										modifyInformation(rg.prediction.get(m),temp,newItem);
									}
								}
							}
						}
						if(judge == false)
						{
							String temp = "r" + regulatingStateCalculation(rg);
							newItem.actions.put(rg.prediction.get(m), temp);
						}
					}
				}
			}
			PPT.add(newItem);
		}
	}

	public void outputResult()
	{
		System.out.print("\n\n");
		for(int i=0;i<PPT.size();i++)
		{
			System.out.print("I"+PPT.get(i).stateID+"  ACTION: ");
			Iterator<Entry<String, String>> it = PPT.get(i).actions.entrySet().iterator();
			while(it.hasNext())
			{
				Entry<String, String> entry = it.next();
				System.out.print(entry.getKey()+" : "+entry.getValue()+"   ");
			}
			
			System.out.print("GOTO: ");
			Iterator<Entry<String, Integer>> it2 = PPT.get(i).gotos.entrySet().iterator();
			while(it2.hasNext())
			{
				Entry<String, Integer> entry = it2.next();
				System.out.print(entry.getKey()+" : "+entry.getValue()+"   ");
			}
			System.out.print("\n\n");
		}
	}
	
	public void resultArray()
	{
		try {
			FileWriter fileWriter = new FileWriter("parsingTable.data");
			
			fileWriter.write("%terminals ");
			for(int i = 0 ; i < terminals.size() ; i++)
			{
				fileWriter.write("\""+terminals.get(i)+"\"");
				if(i != terminals.size()-1)
					fileWriter.write(",");
			}
			
			fileWriter.write("\r\n%nonterminals ");
			for(int i = 0 ; i < nonterminals.size() ; i++)
			{
				fileWriter.write("\""+nonterminals.get(i)+"\"");
				if(i != nonterminals.size()-1)
					fileWriter.write(",");
			}
			
			fileWriter.write("\r\n\r\n");
			
			fileWriter.write("%actions:\r\n");
			for(int i = 0 ; i < PPT.size() ; i++)
			{
				for(int j = 0 ; j < terminals.size() ; j++)
				{
					boolean judge = false;
					String temp = "";
					Iterator<Entry<String, String>> it = PPT.get(i).actions.entrySet().iterator();
					while(it.hasNext())
					{
						Entry<String, String> entry = it.next();
						if(entry.getKey().equals(terminals.get(j)))
						{
							judge = true;
							temp = entry.getValue();
							break;
						}
					}
					if(judge == true)
						fileWriter.write(temp);
					else
						fileWriter.write("null");
					
					fileWriter.write(",");
				}
				
				if(i != PPT.size()-1)
					fileWriter.write("\r\n");
			}
			
			fileWriter.write("\r\n");
			fileWriter.write("\r\n%gotos:\r\n");
			for(int i = 0 ; i < PPT.size() ; i++)
			{
				for(int j = 0 ; j < nonterminals.size() ; j++)
				{
					boolean judge = false;
					Integer temp = -1;
					Iterator<Entry<String, Integer>> it = PPT.get(i).gotos.entrySet().iterator();
					while(it.hasNext())
					{
						Entry<String, Integer> entry = it.next();
						if(entry.getKey().equals(nonterminals.get(j)))
						{
							judge = true;
							temp = entry.getValue();
							break;
						}
					}
					if(judge == true)
						fileWriter.write(temp.toString());
					else
						fileWriter.write("-1");
					
					if(j != nonterminals.size()-1)
						fileWriter.write(",");
				}
				fileWriter.write("\r\n");
			}
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Vector<regularGrammar> getGrammar()
	{
		return grammar;
	}

	public Vector<ParsingTableItem> getPPT()
	{
		return PPT;
	}
}

class ParsingTableItem
{
	int stateID;
	Map<String,String> actions;
	Map<String,Integer> gotos;
	
	public ParsingTableItem()
	{
		actions = new HashMap<String,String>();
		gotos = new HashMap<String,Integer>();
	}
}
