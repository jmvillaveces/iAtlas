package mpg.biochem.de.interbase.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexStruff {

	public static void main(String[] args) {
		
		String uniprot = "[A-Z][0-9][A-Z0-9]{3}[0-9]?";
		String NCBI_gi = "[0-9]{6,}' }";
		String ensembl = "ENS[A-Z0-9]{10,17}";
		String refseq = "(NC|AC|NG|NT|NW|NZ|NM|NR|XM|XR|NP|AP|XP|YP|ZP)_[0-9]+";
		//ensembl regex [0-9]{6,}' }
		//ENS[A-Z0-9]{10,17}
		
		String sample = "uniprotkb:P00734.1P00734.1uniprotkb:O00232	refseq:NP_057853uniprotkb:P01106-2uniprotkb:P05019-2 P05020_2	refseq:NP_057853";
		
		List<String> allMatches = new ArrayList<String>();
		Matcher m = Pattern.compile(refseq).matcher(sample);
		
		while (m.find()) {
			allMatches.add(m.group());
		}
		
		System.out.println(allMatches);
		
		
		
	}
}
