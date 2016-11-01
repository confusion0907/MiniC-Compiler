package yacc;

import java.util.Vector;

public class LFA {
	private int stateID;
	private Vector<regularGrammar> grammar;
	private Vector<String> terminals;
	private Vector<String> nonterminals;
	private Vector<LFANode> LFANodeTable;
	
	public LFA()
	{
		stateID=0;
		grammar = new Vector<regularGrammar>();
		terminals = new Vector<String>();
		nonterminals = new Vector<String>();
		LFANodeTable = new Vector<LFANode>();
		grammar.clear();
		terminals.clear();
		nonterminals.clear();
	}

	public LFA(Vector<regularGrammar> regularG,Vector<String> ter,Vector<String> nonter)
	{
		grammar = new Vector<regularGrammar>();
		terminals = new Vector<String>();
		nonterminals = new Vector<String>();
		LFANodeTable = new Vector<LFANode>();
		
		stateID=0;
		grammar.addAll(regularG);
		terminals.addAll(ter);
		nonterminals.add("start");
		nonterminals.addAll(nonter);
	}

	public boolean AddRegularGrammar(regularGrammar regularG)
	{
		boolean judge=true;
		for(int i=0;i<grammar.size();i++)
		{
			judge=true;
			if(grammar.get(i).left.equals(regularG.left) && grammar.get(i).dot==0)
			{
				int k=0;
				for(int j=0;j<grammar.get(i).right.size();j++)
				{
					if(!grammar.get(i).right.get(j).equals(regularG.right.get(k)))
						judge=false;

					k++;
				}
			}
			else
				judge=false;

			if(judge==true)
			{
				grammar.add((regularGrammar)regularG.clone());
				break;
			}
		}

		return judge;
	}

	public boolean AddRegularGrammar(String left,Vector<String> right)
	{
		boolean judge=true;
		for(int i=0;i<grammar.size();i++)
		{
			judge=true;
			if(grammar.get(i).left.equals(left) && grammar.get(i).dot==0)
			{
				int k=0;
				for(int j=0;j<grammar.get(i).right.size();j++)
				{
					if(!grammar.get(i).right.get(j).equals(right.get(k)))
						judge=false;

					k++;
				}
			}
			else
				judge=false;

			if(judge==true)
			{
				regularGrammar regularG = new regularGrammar();
				regularG.left=left;
				regularG.right=right;
				regularG.dot=0;
				grammar.add(regularG);
				break;
			}
		}

		return judge;
	}

	private void ActionCalculation(LFANode node)
	{
		Vector<regularGrammar> rg=new Vector<regularGrammar>();
		for(int i = 0 ; i < node.getRegularGrammar().size() ; i++)
			rg.add((regularGrammar)node.getRegularGrammar().get(i).clone());

		for(int i=0;i<terminals.size();i++)
		{
			LFANode newNode = NextNode(terminals.get(i),rg);
			if(newNode.getStateID() != stateID)
				node.AddAction(terminals.get(i),newNode.getStateID());
		}
	}

	private void GotoCalculation(LFANode node)
	{
		Vector<regularGrammar> rg = new Vector<regularGrammar>();
		for(int i = 0 ; i < node.getRegularGrammar().size() ; i++)
			rg.add((regularGrammar)node.getRegularGrammar().get(i).clone());

		for(int i=0;i<nonterminals.size();i++)
		{
			LFANode newNode=NextNode(nonterminals.get(i),rg);
			if(newNode.getStateID() != stateID)
				node.AddGoto(nonterminals.get(i),newNode.getStateID());
		}
	}

	private LFANode NextNode(String temp,Vector<regularGrammar> rg)
	{
		LFANode newNode=new LFANode(stateID);
		for(int j=0;j<rg.size();j++)
		{
			if(rg.get(j).dot!=rg.get(j).right.size())
			{
				if(rg.get(j).right.get(rg.get(j).dot).equals(temp))
				{
					regularGrammar newGrammar=new regularGrammar((regularGrammar)rg.get(j).clone());
					newGrammar.dot=newGrammar.dot+1;
					newNode.AddRegularGrammar(newGrammar);
				}
			}
		}

		if(!newNode.getRegularGrammar().isEmpty())
		{
			newNode.clusureCalculation(terminals,nonterminals,grammar);
			newNode.precitionCalculation(terminals,nonterminals);
			if(isInLFANodeTable(newNode)==-1)
			{
				++stateID;
				LFANodeTable.add(newNode);
			}
			else
				return LFANodeTable.get(isInLFANodeTable(newNode));
		}
		return newNode;
	}

	private int isInLFANodeTable(LFANode temp)
	{
		for(int i=0;i<LFANodeTable.size();i++)
		{
			boolean judge=true;
			Vector<regularGrammar> rg1 = new Vector<regularGrammar>(temp.getRegularGrammar());
			Vector<regularGrammar> rg2 = new Vector<regularGrammar>(LFANodeTable.get(i).getRegularGrammar());

			if(rg1.size()!=rg2.size())
				continue;

			int k=0;
			for(int j=0;j<rg2.size();j++)
			{
				if(rg1.get(k).dot==rg2.get(j).dot && rg1.get(k).left.equals(rg2.get(j).left) && rg1.get(k).right.size()==rg2.get(j).right.size() && rg1.get(k).prediction.size() == rg2.get(j).prediction.size())
				{
					int m=0;
					for(int n=0;n<rg2.get(j).right.size();n++)
					{
						if(!rg1.get(k).right.get(m).equals(rg2.get(j).right.get(n)))
						{
							judge=false;
							break;
						}

						++m;
					}
					
					int x=0;
					for(int y=0;y<rg2.get(j).prediction.size();y++)
					{
						if(!rg1.get(k).prediction.get(x).equals(rg2.get(j).prediction.get(y)))
						{
							judge=false;
							break;
						}

						++x;
					}
				}
				else
					judge=false;

				++k;
			}

			if(judge==true)
				return LFANodeTable.get(i).getStateID();
		}
		return -1;
	}

	public void CreateLFA()
	{
		regularGrammar start=new regularGrammar();
		start.left="start";
		start.right.add(grammar.get(0).left);
		start.dot=0;
		start.prediction.add("#");

		LFANode newNode=new LFANode(stateID++);
		newNode.AddRegularGrammar(start);
		newNode.clusureCalculation(terminals,nonterminals,grammar);
		newNode.precitionCalculation(terminals,nonterminals);
		LFANodeTable.add(newNode);

		int index=0;

		while(index!=stateID)
		{
			ActionCalculation(LFANodeTable.get(index));
			GotoCalculation(LFANodeTable.get(index));
			index=index+1;
		}
		
		return;
	}

	public Vector<LFANode> getLFANodeTable()
	{
		return LFANodeTable;
	}

	public int getStateID()
	{
		return stateID;
	}
}
