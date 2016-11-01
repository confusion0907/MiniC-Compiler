public class Lex {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "Cminus.l";

		lexGrammarReader lexFileReader = new lexGrammarReader(path);
		CodeGen cg  = new CodeGen();
		cg.write(lexFileReader.getCDefPart()+ "\n");
		NFA nfas[] = new NFA[lexFileReader.getRegularExpressionNumber()];
		System.out.println("����NFA...");
		
		for ( int i=0; i<lexFileReader.getRegularExpressionNumber(); i++ ) {
			String regExpr = lexFileReader.getRegularExpression()[i];
			nfas[i] = new NFA(regExpr, lexFileReader);
			System.out.println(regExpr+"--------"+nfas[i].numState+"��״̬��NFA");
		}
		System.out.println("�鲢NFA...");
		MergedNFA mn = new MergedNFA(nfas);
		System.out.println("�鲢��ɣ��鲢����"+mn.numState+"��״̬");
		System.out.println("����DFA...");
		DFA dfa = new DFA (mn);
		System.out.println("������ɣ�������DFA��"+dfa.getNumStates()+"��״̬");
		System.out.println("��С��DFA...");
		dfa.minimize();
		System.out.println("��С����������С����DFA��"+dfa.getNumStates()+"��״̬");
		System.out.println("����Ŭ��д����...��ȴ�...");
		if (AppData.TRUE)
			dfa.print();
		cg.genTable(dfa.getTable(), "TABLE");
		cg.genVector(dfa.getStatePattern(), "STATE_PATTERN");
		cg.genDriver(lexFileReader.getCCode());
		cg.write(lexFileReader.getCSubRoutine()+"\n");
		cg.close();
		System.out.println("�ɹ�����C++��Դ���룬����μ�temp.data");
		System.out.println("�ع�������...");
		Formatter f = new Formatter("temp.data", "SeuLex_Generated_Code.cpp");
		f.format();
		System.out.println("�ع��ɹ���Դ����SeuLex_Generated_Code.cpp��");
	}
}
