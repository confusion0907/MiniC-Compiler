package yacc;

public class test {
	public static void main(String[] args) 
	{
		String fname = "Mips.y";
		
		YaccGrammarReader yacc = new YaccGrammarReader(fname);
		yacc.readYacc();
		
		LFA lfa = new LFA(yacc.grammarGenerating(),yacc.getTerminals(),yacc.getNonterminals());
		lfa.CreateLFA();

		ParsingTable ppt = new ParsingTable(yacc.getTerminals(),yacc.getNonterminals(),yacc.grammarGenerating(),yacc.getOps(),lfa);
		ppt.CreatePPT();
		ppt.outputResult();
		ppt.resultArray();
	}
}
