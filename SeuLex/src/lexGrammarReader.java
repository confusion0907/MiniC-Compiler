import java.io.*;

public class lexGrammarReader {
	private String fname;
	private static final char OR = '|';

	public lexGrammarReader(String _fname) {
		fname = _fname;
	}

	public int getRegularExpressionNumber() {
		File f = new File(fname);
		int count = -1;
		try {
			@SuppressWarnings("resource")
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			long ptr = 0;
			while (ptr < f.length()) {
				String str = raf.readLine();
				ptr = raf.getFilePointer();
				if (str.equals("%%"))
					break;
			}
			String str = "";

			while (ptr < f.length() && !str.equals("%%")) {
				str = raf.readLine();
				ptr = raf.getFilePointer();
				// System.out.println(str+count);

				if (!str.equals("")) {
					for (int i = 0; i < str.length(); i++) {

						if ((i < str.length() - 1) && (str.charAt(i) == ' ' || str.charAt(i) == '\t')
								&& (str.charAt(i + 1) == '{')) {
							int flag = 0;
							for (int j = i + 1; j < str.length(); j++)
								if (str.charAt(j) == '}')
									flag = 1;

							while (flag == 0) {
								str = raf.readLine();
								ptr = raf.getFilePointer();
								// System.out.println(str+count);
								for (int j = 0; j < str.length(); j++) {
									if (str.charAt(j) == '}')
										flag = 1;
								}
							}
							break;
						}
					}
					count++;
				}

			}

		} catch (Exception e) {
			System.out.println("Have an error");
			e.printStackTrace();
		}
		return count;
	}

	public String getRegDef(String s) {
		File f = new File(fname);
		int tabCount = -1;
		try {
			@SuppressWarnings("resource")
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			long ptr = 0;
			while (ptr < f.length()) {
				String str = raf.readLine();
				ptr = raf.getFilePointer();
				if (str.equals("%}"))
					break;
			}
			String str = "";

			while (ptr < f.length() && !str.equals("%%")) {

				str = raf.readLine();
				ptr = raf.getFilePointer();

				if (!str.equals("")) {
					tabCount++;
				}

			}

			int tabNumber = tabCount;
			String tab[] = new String[tabCount];
			ptr = 0;
			raf.seek(0);
			while (ptr < f.length()) {
				str = raf.readLine();
				ptr = raf.getFilePointer();
				if (str.equals("%}"))
					break;
			}
			str = "";
			tabCount = 0;
			while (tabCount < tabNumber) {

				str = raf.readLine();
				ptr = raf.getFilePointer();

				if (!str.equals("")) {
					tab[tabCount] = str;
					tabCount++;
				}

			}

			String tab1[] = new String[tabNumber];
			String tab2[] = new String[tabNumber];
			for (int i = 0; i < tabNumber; i++) {
				tab1[i] = tab2[i] = "";
			}

			for (int i = 0; i < tabNumber; i++) {
				for (int j = 0; j < tab[i].length(); j++) {
					if (tab[i].charAt(j) != ' ' && tab[i].charAt(j) != '\t') {
						tab1[i] += tab[i].charAt(j);
					} else
						break;
				}
			}

			for (int i = 0; i < tabNumber; i++) {
				for (int j = 0; j < tab[i].length(); j++) {

					if ((tab[i].charAt(j) == ' ' || tab[i].charAt(j) == '\t') && (tab[i].charAt(j + 1) == '[')) {
						tab2[i] += '(';
						for (int k = j + 2; tab[i].charAt(k) != ']'; k++) {

							if (tab[i].charAt(k + 1) == '-') {
								char char1 = tab[i].charAt(k);
								char char2 = tab[i].charAt(k + 2);
								while (char1 <= char2) {
									tab2[i] += char1;
									if (char1 + 1 <= char2)
										tab2[i] += OR;
									char1++;
								}
								if (tab[i].charAt(k + 3) != ']')
									tab2[i] += OR;

							}

						}
						tab2[i] += ')';
						break;
					}

				}
			}

			for (int i = 0; i < tabNumber; i++) {
				if (s.equals(tab1[i]))
					s = tab2[i];
			}

		} catch (Exception e) {
			System.out.println("Have an error");
			e.printStackTrace();
		}
		return s;
	}

	public String[] getRegularExpression() {
		File f = new File(fname);
		int tabCount = -1;
		int regNum = getRegularExpressionNumber();
		String tab1[] = new String[regNum];
		try {
			@SuppressWarnings("resource")
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			long ptr = 0;
			String tab[] = new String[regNum];
			while (ptr < f.length()) {
				String str = raf.readLine();
				ptr = raf.getFilePointer();
				if (str.equals("%%"))
					break;
			}
			tabCount = 0;
			while (tabCount < regNum) {

				String str = raf.readLine();
				ptr = raf.getFilePointer();

				if (!str.equals("") && str.charAt(0) != ' ' && str.charAt(0) != '\t') {
					tab[tabCount] = str;
					tabCount++;
				}

			}

			for (int i = 0; i < regNum; i++) {
				tab1[i] = "";
			}

			for (int i = 0; i < regNum; i++) {
				int temp = 0;
				for (int j = 0; j < tab[i].length(); j++) {

					if (((tab[i].charAt(j) == '[' || tab[i].charAt(j) == '{')) && tab[i].charAt(j + 1) != '"')
						temp++;
					if ((tab[i].charAt(j) == ']' || tab[i].charAt(j) == '}') && tab[i].charAt(j - 1) != '"')
						temp--;

					if ((tab[i].charAt(j) != ' ' && tab[i].charAt(j) != '\t') || (temp != 0)) {
						tab1[i] += tab[i].charAt(j);
					} else
						break;
				}
			}

		} catch (Exception e) {
			System.out.println("Have an error");
			e.printStackTrace();
		}
		return tab1;
	}

	public String[] getCCode() {
		File f = new File(fname);
		int tabCount = -1;
		int regNum = getRegularExpressionNumber();
		String tab1[] = new String[regNum];
		try {
			@SuppressWarnings("resource")
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			long ptr = 0;
			String tab[] = new String[regNum];
			while (ptr < f.length()) {
				String str = raf.readLine();
				ptr = raf.getFilePointer();
				if (str.equals("%%"))
					break;
			}
			tabCount = -1;
			while (tabCount <= regNum - 1) {

				String str = raf.readLine();
				ptr = raf.getFilePointer();

				if (str.equals("%%"))
					break;

				if (!str.equals("") && str.charAt(0) != ' ' && str.charAt(0) != '\t') {
					tabCount++;
					tab[tabCount] = str;

				} else if (!str.equals("") && (str.charAt(0) == ' ' || str.charAt(0) == '\t')) {
					tab[tabCount] += str;
				}

			}

			for (int i = 0; i < regNum; i++) {
				tab1[i] = "";
			}

			for (int i = 0; i < regNum; i++) {
				int temp = 0;
				for (int j = 0; j < tab[i].length(); j++) {
					if (((tab[i].charAt(j) == '[' || tab[i].charAt(j) == '{')) && tab[i].charAt(j + 1) != '"')
						temp++;
					if ((tab[i].charAt(j) == ']' || tab[i].charAt(j) == '}') && tab[i].charAt(j - 1) != '"')
						temp--;

					if (((j + 1) < tab[i].length()) && ((tab[i].charAt(j) == ' ' || tab[i].charAt(j) == '\t'))
							&& (tab[i].charAt(j + 1) != ' ' && tab[i].charAt(j + 1) != '\t') && temp == 0) {

						for (int n = j + 1; n < tab[i].length(); n++) {
							tab1[i] += tab[i].charAt(n);
						}

						break;
					}

				}
			}

		} catch (Exception e) {
			System.out.println("Have an error");
			e.printStackTrace();
		}
		return tab1;
	}

	public String getCDefPart() {
		File f = new File(fname);
		String s = "";
		try {
			@SuppressWarnings("resource")
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			long ptr = 0;
			while (ptr < f.length()) {
				String str = raf.readLine();
				ptr = raf.getFilePointer();
				if (str.equals("%{"))
					break;
			}

			String str = raf.readLine();
			s = str;
			ptr = raf.getFilePointer();
			while (ptr < f.length()) {

				str = raf.readLine();
				ptr = raf.getFilePointer();

				if (str.equals("%}"))
					break;

				s += '\r';
				s += str;
			}
		}

		catch (Exception e) {
			System.out.println("Have an error");
			e.printStackTrace();
		}
		return s;
	}

	public String getCSubRoutine() {
		File f = new File(fname);
		String s = "";
		try {
			int temp = 0;
			@SuppressWarnings("resource")
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			long ptr = 0;
			while (ptr < f.length()) {
				if (temp >= 2)
					break;
				String str = raf.readLine();
				ptr = raf.getFilePointer();
				if (str.equals("%%"))
					temp++;
			}

			String str = raf.readLine();
			s = str;
			ptr = raf.getFilePointer();
			while (ptr < f.length()) {

				str = raf.readLine();
				ptr = raf.getFilePointer();

				s += '\r';
				s += str;
			}
		}

		catch (Exception e) {
			System.out.println("Have an error");
			e.printStackTrace();
		}
		return s;
	}

}
