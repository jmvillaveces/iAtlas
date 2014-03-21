package mpg.biochem.de.interbase.service;

import static org.junit.Assert.assertNotNull;

import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

public class IDRegexFinderTest {

	private String matcher = "uniprotkb:P01106-2 3113320	uniprotkb:P29590-5refseq:XP_005272110refseq:NM_002467|refseq:NP_002458|string:9615.ENSCAFP00000034971	string:9615.ENSCAFP00000035384";
	private String m = "ENSCAFP00000034971 ENSCAFP00000035384 unknown:Ylr392c(shortLabel)	unknown:Jsn1(shortLabel)";
	private String r = "refseq:NP_057853.1	genbank indentifier:1	-	-	unknown:Tat(shortLabel)	unknown:HIV LTR(shortLabel)";
	
	private IDRegexFinder regFinder;
	
	@Before
    public void setUp() {
		regFinder = new IDRegexFinder(r+m+matcher); 
    }
	
	@Test
	public void testUniprot() throws Exception {
		
		System.out.println(regFinder.getUniprot());
		System.out.println(regFinder.getRefseq());
		System.out.println(regFinder.getNcbiGi());
		System.out.println(regFinder.getEnsembl());
	}	
}