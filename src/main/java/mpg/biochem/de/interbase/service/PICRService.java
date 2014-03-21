package mpg.biochem.de.interbase.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class PICRService extends DefaultHandler{

	private final String restURLForUniprotBestGuess = "http://www.ebi.ac.uk/Tools/picr/rest/getUniProtBestGuess?";
	private Logger logger = Logger.getLogger(PICRService.class);
	
	private boolean isAccession = false, isSequence = false, isDatabase = false;
	private String accession, sequence, database;
	
	private SAXParserFactory factory;
	private SAXParser saxParser;
	private CloseableHttpClient client;
	
	
	
	public PICRService(){
		factory = SAXParserFactory.newInstance();
		client = HttpClients.custom().setRetryHandler(new DefaultHttpRequestRetryHandler()).build();
		try {
			saxParser = factory.newSAXParser();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("accession")) isAccession = true;
		if (qName.equalsIgnoreCase("sequence")) isSequence = true;
		if (qName.equalsIgnoreCase("databaseName")) isDatabase = true;
	}
	
	public void characters(char ch[], int start, int length) throws SAXException {
		
		if (isSequence) {
			sequence = new String(ch, start, length);
			isSequence = false;
		}
 
		if (isAccession) {
			accession = new String(ch, start, length);
			isAccession = false;
		}
		
		if (isDatabase) {
			database = new String(ch, start, length);
			isDatabase = false;
		}
	}
	
	public void endDocument(){
		accession = "";
		sequence = "";
		database = "";
	}
	
	private InputSource getInputSource(String query) throws IllegalStateException, IOException{
		
		HttpGet httpget = new HttpGet(query);
		CloseableHttpResponse response = client.execute(httpget);
		
		if (response.getStatusLine().getStatusCode() == 200) {
			return new InputSource(new InputStreamReader((response.getEntity().getContent())));
		}
		return null;
	}
	
	public PICRResult getUniprotBestGuessFor(String accession, String taxonId) throws SAXException, MalformedURLException, IOException, InterruptedException{
		String query = restURLForUniprotBestGuess + "accession=" + accession;
		if (taxonId != null) query += "&taxid=" + taxonId;
		  
		InputSource is = getInputSource(query);
		if (is == null) return null;
		
		XMLReader xr = saxParser.getXMLReader();
        xr.setContentHandler(this);
        xr.parse(is);
		
        PICRResult result = (database.equalsIgnoreCase("") || accession.equalsIgnoreCase("") || sequence.equalsIgnoreCase("")) ? null : new PICRResult(database, accession, sequence);
        return result;
	}
	
	public TreeSet<PICRResult> getUniprotBestGuessFor(Collection<String> accessions, String taxonId) throws MalformedURLException, SAXException, IOException, InterruptedException {
		
		TreeSet<PICRResult> results = new TreeSet<PICRResult>(new Comparator<PICRResult>(){
            public int compare(PICRResult a, PICRResult b){
            	return (a.getSequence().length()<b.getSequence().length() ? -1 : (a.getSequence().length()==b.getSequence().length() ? 0 : 1));
            }
		});
		
		Iterator<String> i = accessions.iterator();
		while(i.hasNext()){
			PICRResult mapped = getUniprotBestGuessFor(i.next(), taxonId);
			if(mapped != null)
				results.add(mapped);
				
		}
		
		return results;
	}
	
	public class PICRResult{
		private String database, accession, sequence;

		public PICRResult(String database, String accession, String sequence) {
			this.database = database;
			this.accession = accession;
			this.sequence = sequence;
		}

		public String getDatabase() {
			return database;
		}

		public String getAccession() {
			return accession;
		}

		public String getSequence() {
			return sequence;
		}

		@Override
		public boolean equals(Object other) {
			if (other == null) return false;
		    if (other == this) return true;
		    if (!(other instanceof PICRResult)) return false;
		    
		    PICRResult otherPICRResult = (PICRResult) other;
		    
			return this.getAccession().equals(otherPICRResult.getAccession());
		}

		@Override
		public String toString() {
			
			StringBuilder result = new StringBuilder();
		    String NEW_LINE = System.getProperty("line.separator");

		    result.append(this.getClass().getName() + " Object {" + NEW_LINE);
		    result.append(" Accession: " + accession + NEW_LINE);
		    result.append(" database: " + database + NEW_LINE);
		    result.append(" sequence: " + sequence + NEW_LINE );
		    result.append("}");

		    return result.toString();
		}
	}
}

