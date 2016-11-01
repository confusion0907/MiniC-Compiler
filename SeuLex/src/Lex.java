public class Lex {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "Cminus.l";

		lexGrammarReader lexFileReader = new lexGrammarReader(path);
		CodeGen cg  = new CodeGen();
		cg.write(lexFileReader.getCDefPart()+ "\n");
		NFA nfas[] = new NFA[lexFileReader.getRegularExpressionNumber()];
		System.out.println("构建NFA...");
		
		for ( int i=0; i<lexFileReader.getRegularExpressionNumber(); i++ ) {
			String regExpr = lexFileReader.getRegularExpression()[i];
			nfas[i] = new NFA(regExpr, lexFileReader);
			System.out.println(regExpr+"--------"+nfas[i].numState+"个状态的NFA");
		}
		System.out.println("归并NFA...");
		MergedNFA mn = new MergedNFA(nfas);
		System.out.println("归并完成，归并后有"+mn.numState+"个状态");
		System.out.println("创建DFA...");
		DFA dfa = new DFA (mn);
		System.out.println("创建完成，创建的DFA有"+dfa.getNumStates()+"个状态");
		System.out.println("最小化DFA...");
		dfa.minimize();
		System.out.println("最小化结束，最小化的DFA有"+dfa.getNumStates()+"个状态");
		System.out.println("正在努力写代码...请等待...");
		if (AppData.TRUE)
			dfa.print();
		cg.genTable(dfa.getTable(), "TABLE");
		cg.genVector(dfa.getStatePattern(), "STATE_PATTERN");
		cg.genDriver(lexFileReader.getCCode());
		cg.write(lexFileReader.getCSubRoutine()+"\n");
		cg.close();
		System.out.println("成功产生C++的源代码，代码参见temp.data");
		System.out.println("重构代码中...");
		Formatter f = new Formatter("temp.data", "SeuLex_Generated_Code.cpp");
		f.format();
		System.out.println("重构成功！源码在SeuLex_Generated_Code.cpp中");
	}
}
