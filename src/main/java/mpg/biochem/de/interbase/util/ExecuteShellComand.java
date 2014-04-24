package mpg.biochem.de.interbase.util;
 
public class ExecuteShellComand {
 
	public static void main(String[] args) {
 
		ExecuteShellComand obj = new ExecuteShellComand();
 
		String domainName = "google.com";
 
		//in mac oxs
		String command = "ping " + domainName;
 
		//in windows
		//String command = "ping -n 3 " + domainName;
 
		String output = obj.executeCommand(command);
 
		System.out.println(output);
 
	}
 
	public String executeCommand(String command) {
		
		StringBuffer output = new StringBuffer();
 
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			
			while (true) {
			      int c = p.getInputStream().read();
			      if (c == -1) break;
			      System.out.write((char)c);
			}
			
			p.waitFor();
 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString();
	}
}