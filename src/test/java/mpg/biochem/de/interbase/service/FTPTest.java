package mpg.biochem.de.interbase.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.hupo.psi.mi.psicquic.wsclient.PsicquicSimpleClient;

public class FTPTest {

	
	
	
	public static void main(String[] args) throws IOException{
		
		String proxy = "proxy01.biochem.mpg.de";
		
		String port = "3128";
		
		//Proxy Settings
		if(!proxy.equals("") && !port.equals("")){
					
			//System.getProperties().put("ftp.proxyHost", proxy);
			//System.getProperties().put("ftp.proxyPort", port);
		}
		
			
		URL url = new URL("ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/idmapping/idmapping_selected.tab.gz");

	    System.out.println("URL:- " +url);
	    URLConnection connection = url.openConnection();


	    System.out.println(connection.getHeaderField("Last-Modified"));
	    
	    
		
		PsicquicSimpleClient client = new PsicquicSimpleClient("http://tyersrest.tyerslab.com:8805/psicquic/webservices/current/search/");
		
        try {
        	
            final InputStream result = client.getByQuery("*", PsicquicSimpleClient.MITAB25, 0, 10);

            BufferedReader in = new BufferedReader(new InputStreamReader(result));

            String line;

            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		
	}
	
	
}
