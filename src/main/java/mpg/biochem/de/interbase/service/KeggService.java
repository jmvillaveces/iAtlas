package mpg.biochem.de.interbase.service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.http.HTTPException;

import mpg.biochem.de.interbase.util.Item;

import org.apache.log4j.Logger;

public class KeggService {
        
        private static KeggService instance = new KeggService();
        private String baseUrl = "http://rest.kegg.jp/";
        public final String LIST = "list", GET = "get", INFO = "info", FIND = "find", CONV = "conv", LINK = "link";
        public final String NCBI_GENE_ID = "ncbi-geneid", UNIPROT = "uniprot", NCBI_GI = "ncbi-gi";
        private Logger logger = Logger.getLogger(KeggService.class);
        
        /**
         * Constructor, private because of singleton
         */
        private KeggService() {}

        /**
         * Get the current instance
         * @return KeggService
         */
        public static KeggService getInstance() {
                return instance;
        }
        
        public String[] getOrganisms(){
        	String[] arguments = new String[]{LIST, "organism"};
            String url = addArguments(baseUrl, arguments);
            
            try {
            	List<String> orgs = new ArrayList<String>();
                HttpURLConnection conn = openConnection(url);
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                
                String line = "";
                while ((line = br.readLine()) != null){
                	String[] lineArr = line.split("\t");
                    orgs.add(lineArr[1]);
		        }
		        br.close();
		        closeConnection(conn);
		                
		        return orgs.toArray(new String[0]);
            } catch (IOException e) {
            	e.printStackTrace();
		    }
            return new String[0];
        }
        
        public Item[] getGenesByOrganism(String organism){
        	String[] arguments = new String[]{LIST, organism};
            String url = addArguments(baseUrl, arguments);
            
            List<Item> items = new ArrayList<Item>();
            try {
            	HttpURLConnection conn = openConnection(url);
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                
                String line = "";
                while ((line = br.readLine()) != null){
                	String[] lineArr = line.split("\t");
                    items.add(new Item(lineArr[0], lineArr[1].substring(0,lineArr[1].indexOf(";"))));
		        }
		        br.close();
		        closeConnection(conn);
            } catch (IOException e) {
            	e.printStackTrace();
		    }
            return items.toArray(new Item[0]);
        }
        
        public Item[] getMappingsByOrganism(String organism, String database){
        	String[] arguments = new String[]{CONV, database, organism};
            String url = addArguments(baseUrl, arguments);
            
            List<Item> items = new ArrayList<Item>();
            try {
            	HttpURLConnection conn = openConnection(url);
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                
                String line = "";
                while ((line = br.readLine()) != null){
                	String[] lineArr = line.split("\t");
                	
                	if(lineArr.length>1){
	                	String idOne = lineArr[0].substring(lineArr[0].indexOf(":")+1, lineArr[0].length());
	                	String idTwo = lineArr[1].substring(lineArr[1].indexOf(":")+1, lineArr[1].length());
	                    items.add(new Item(idOne, idTwo));
                	}
		        }
		        br.close();
		        closeConnection(conn);
            } catch (IOException e) {
            	e.printStackTrace();
		    }
            return items.toArray(new Item[0]);
        }
        
        private HttpURLConnection openConnection(String url){
            HttpURLConnection huc = null;
            
            try {
                    URL u = new URL(url); 
                    huc =  (HttpURLConnection)  u.openConnection();
                    huc.setRequestMethod("GET"); 
                    huc.connect();
                        
                    if(huc.getResponseCode() != 200){
                    	logger.error("Server returned error code in url="+url, new HTTPException(huc.getResponseCode()));
                    }
            } catch (IOException e) {
                    e.printStackTrace();
            }
            return huc;
        }
        
        private void closeConnection(HttpURLConnection conn){
            conn.disconnect();
            conn = null;
        }
        
        private String addArguments(String url, String[] arguments){
                for(String arg : arguments)
                        url = addArgument(url, arg);
                return url;
        }
        
        private String addArgument(String url, String argument){
                return url+argument+"/";
        }
        
        public static void main(String[] args){
        	//KeggService.getInstance().getOrganisms();
        	//KeggService.getInstance().getGenesByOrganism("eco");
        	KeggService.getInstance().getMappingsByOrganism("cvp", KeggService.getInstance().UNIPROT);
        }
}