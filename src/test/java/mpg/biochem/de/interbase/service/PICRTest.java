package mpg.biochem.de.interbase.service;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import mpg.biochem.de.interbase.service.PICRService.PICRResult;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.google.common.collect.Lists;

public class PICRTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws InterruptedException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static void main(String[] args) throws JDOMException, IOException, InterruptedException, ParserConfigurationException, SAXException {
		
		String[] accessions = {"P37173","P011137","Q99474", "Q0VFR8", "O60320", "Q9CWN7"};
		
		Logger log = Logger.getLogger(PICRTest.class);
		
		log.info("about to start");
		PICRService service = new PICRService();
		for(int i=0; i<accessions.length;i++){
			log.info("before");
			service.getUniprotBestGuessFor(accessions[i], "9606");
			log.info("after");
		}
		
		/*PICRService service = new PICRService();
		
		System.out.println(service.getUniprotBestGuessFor("ghgfhgf", null));
		
		String[] arr = {"E5KSD5","E9PE82","P16219"};
		List<String> asList = Arrays.asList(arr);
		TreeSet<PICRResult> map = service.getUniprotBestGuessFor(asList, null);
		
		System.out.println(map);*/
	}

}
