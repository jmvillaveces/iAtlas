package mpg.biochem.de.interbase.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration({"/test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class RegexTest {

	private Pattern uniprot = Pattern.compile("[A-NR-Z][0-9][A-Z][A-Z0-9][A-Z0-9][0-9]|[OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9]");
	private	Pattern dotNumber = Pattern.compile("^.+(\\.|-)\\d$");
	
	
	//refseq (NC|AC|NG|NT|NW|NZ|NM|NR|XM|XR|NP|AP|XP|YP|ZP)_[0-9]+
	
	//uniparc UPI[A-F0-9]{10}
	
	// IPI[0-9]{8}
	
	//Q63644-1
	
	@Test
	public void testDotNumber() throws Exception {
		String[] ids = new String[]{"A2BC19.1", "P12345-1", "P29358"};
		
		
		Matcher matcher = dotNumber.matcher(ids[0]);
		assertTrue(matcher.matches());
		
		matcher = dotNumber.matcher(ids[1]);
		assertTrue(matcher.matches());
		
		matcher = dotNumber.matcher(ids[2]);
		assertFalse(matcher.matches());
	}
	
	@Test
	public void testUniprotPattern() throws Exception {
		String[] ids = new String[]{"A2BC19", "P12345", "P29358", "P68250", "P68251", "Q63644"};
		
		for(String id : ids){
			Matcher matcher = uniprot.matcher(id);
			assertTrue(matcher.matches());
		}
		
		Matcher matcher = uniprot.matcher("not uniprot id");
		assertFalse(matcher.matches());
	}
	
}
