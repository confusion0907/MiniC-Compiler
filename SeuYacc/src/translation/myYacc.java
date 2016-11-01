package translation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Stack;
import java.util.Vector;

public class myYacc {
	final int number = 1586;
	public String terminals[] = {"#","IDENT","VOID","INT","WHILE","IF","ELSE","RETURN","EQ","NE","LE","GE","AND","OR","DECNUM","CONTINUE","BREAK","HEXNUM","LSHIFT","RSHIFT",";","[","]","(",")",",","{","}","=","$","<",">","+","-","*","/","%","!","&","^","~","|"};
	public String nonterminals[] = {"program","decl_list","decl","var_decl","type_spec","fun_decl","FUNCTION_IDENT","params","param_list","param","compound_stmt","compound","local_decls","local_decl","stmt_list","stmt","expr_stmt","while_stmt","WHILE_IDENT","block_stmt","if_stmt","return_stmt","expr","int_literal","arg_list","args","continue_stmt","break_stmt"};
	public String [][]actions;
	public int [][]gotos;
	public Vector<producer> producers;
	
	public myYacc()
	{
		producers = new Vector<producer>();
		actions = new String[number][];
		gotos = new int[number][];
		for(int i = 0 ; i < number ; i++)
			actions[i] = new String[terminals.length];
		for(int i = 0 ; i < number ; i++)
			gotos[i] = new int[nonterminals.length];
		
		File f = new File("parsingTable.data");
		
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			long ptr = 0;
			String str = "";
			
			while (ptr < f.length()) 
			{
				str = raf.readLine();
				ptr = raf.getFilePointer();
				
				if(str.equals("%actions:"))
					break;
			}
			
			str = "";
			int index = 0;
			while (ptr < f.length() && index < number) 
			{
				str = raf.readLine();
				ptr = raf.getFilePointer();
				
				if(!str.equals(""))
				{
					String[] ss = str.split(",");
					for(int i = 0 ; i < ss.length ; i++)
						actions[index][i] = ss[i];
					
					++index;
				}
			}
			
			while (ptr < f.length()) 
			{
				str = raf.readLine();
				ptr = raf.getFilePointer();
				
				if(str.equals("%gotos:"))
					break;
			}
			
			str = "";
			index = 0;
			while (ptr < f.length() && index < number) 
			{
				str = raf.readLine();
				ptr = raf.getFilePointer();
				
				if(!str.equals(""))
				{
					String[] ss = str.split(",");
					for(int i = 0 ; i < ss.length ; i++)
						gotos[index][i] = Integer.parseInt(ss[i]);
					
					++index;
				}
			}
			
			while (ptr < f.length()) 
			{
				str = raf.readLine();
				ptr = raf.getFilePointer();
				
				if(str.equals("%%"))
					break;
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
					
					for(int i = 2 ; i < ss.length ; i++)
					{
						if(ss[i].charAt(0) == '{' && ss[i].length() != 1) 
						{
							for(int j = i ; j < ss.length ; j++)
								semantic = semantic + ss[j];
							break;
						} 
						else
							right.add(ss[i]);
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
							right.add(ss[i]);
					}
				}
				
				semantic = semantic.substring(1, semantic.length()-1);
				
				Vector<String> sem = new Vector<String>();
				
				ss = semantic.split(";");
				for(int i = 0 ; i < ss.length ; i++)
					sem.add(ss[i]);
				
				this.producers.add(new producer(left,right,sem));
			}
			raf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int findNonterminal(int temp)
	{
		for(int i = 0 ; i < nonterminals.length ; i++)
		{
			if(nonterminals[i].equals(producers.get(temp).left))
				return i;
		}
		return -1;
	}
	
	private int findTerminal(String temp)
	{
		for(int i = 0 ; i < terminals.length ; i++)
		{
			if(terminals[i].equals(temp))
				return i;
		}
		return -1;
	}
	
	public boolean YaccAnalysis(String []ss)
	{
		for(int i = 0 ; i < ss.length-1 ; i++)
		{
			if((ss[i] == "{" && ss[i+1] == "}") || (ss[i] == "(" && ss[i+1] == ")"))
			{
				String []s = new String[ss.length+1];
				for(int j = 0 ; j <= i ; j++)
					s[j] = ss[j];
				s[i+1] = "#";
				for(int j = i+1 ; j < ss.length ; j++)
					s[j+1] = ss[j];
				ss = s;
			}
		}
		
		Stack<Integer> states = new Stack<Integer>();
		Stack<String> signals = new Stack<String>();
		
		states.push(0);
		signals.push("#");
		
		int index = 0;
		String temp;
		while(index < ss.length)
		{
			if(findTerminal(ss[index]) != -1)
			{
				temp = actions[states.lastElement()][findTerminal(ss[index])];
				if(temp.charAt(0) == 's')
				{
					states.push(Integer.parseInt(temp.substring(1)));
					signals.push(ss[index++]);
				}
				else if(temp.charAt(0) == 'r' && (findNonterminal(Integer.parseInt(temp.substring(1)))!=-1 || temp.equals("r0")))
				{
					//�������
					System.out.println(temp);
					if(temp.equals("r0"))
						return true;
					
					for(int i = 0 ; i < producers.get(Integer.parseInt(temp.substring(1))).right.size() ; i++)
						states.pop();
					states.push(gotos[states.lastElement()][findNonterminal(Integer.parseInt(temp.substring(1)))]);
					for(int i = 0 ; i < producers.get(Integer.parseInt(temp.substring(1))).right.size() ; i++)
						signals.pop();
					signals.push(nonterminals[findNonterminal(Integer.parseInt(temp.substring(1)))]);
				}
				else
					return false;
			}
			else
				return false;
		}
		return false;
	}
}

class producer
{
	public String left;
	public Vector<String> right;
	public Vector<String> semantic;
	
	public producer(String left,Vector<String> right,Vector<String> semantic)
	{
		this.right = new Vector<String>();
		this.semantic = new Vector<String>();
		
		this.left = left;
		for(int i = 0 ; i < right.size() ; i++)
			this.right.add(new String(right.get(i)));
		for(int i = 0 ; i < semantic.size() ; i++)
			this.semantic.add(new String(semantic.get(i)));
	}
}