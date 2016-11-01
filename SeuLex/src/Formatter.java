import java.io.*;

public class Formatter {
	
	private String inName;
	private String outName;
	public Formatter( String in, String out ) {
		inName = in;
		outName = out;
	}
	
	public void format() {
		try
		{
			File in = new File(inName);
			File out = new File(outName);
			FileInputStream inFile = new FileInputStream(in);
			FileOutputStream outFile = new FileOutputStream(out);
			byte[]buffer = new byte[1024*1024];
			int i = 0,f=0;
			while((i=inFile.read(buffer))!=-1)
			{
				
				for(int j=0;j<i;j++){
					if(buffer[j]=='{'){
						f++;
					}
					else if(buffer[j]=='}'){
						f--;
					}
					if(buffer[j]=='\n' && buffer[j+1]=='}'){
						int l=f-1;
						outFile.write('\n');
						while(l>0){
							outFile.write('\t');
							l--;
						}
					}
					else{
						if (buffer[j] == '\n') {
							int n = f;
							outFile.write('\n');
							while (n > 0) {
								outFile.write('\t');
								n--;
							}
						} 
						else {
							outFile.write(buffer[j]);
						}
					}
						
				}
			}

		    inFile.close();
		    outFile.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}

}
