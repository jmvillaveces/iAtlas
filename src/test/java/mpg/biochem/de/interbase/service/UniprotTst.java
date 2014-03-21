package mpg.biochem.de.interbase.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class UniprotTst {
	
	private UniProtIndex index;
	
	//uniprot [A-Z][0-9][A-Z0-9]{3}[0-9]((-([0-9]+)|:PRO_[0-9]{10}))?
	//NCBI_gi 
	//ensembl regex [0-9]{6,}' }
	//ENS[A-Z0-9]{10,17}
	
	
	public static void main(String[] args) {
		UniprotTst tst;
		try {
			tst = new UniprotTst("data/mapping/index");
			tst.getMappingsForFile("data/top200.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public UniprotTst(String indexPath) throws IOException{
		index = new UniProtIndex(indexPath);
	}
	
	public void getMappingsForFile(String filePath) throws IOException{
		String sCurrentLine;
		 
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		
		int count = 0;
		while ((sCurrentLine = br.readLine()) != null) {
			String x = index.search(sCurrentLine);
			if(x!="") count++;
			System.out.println(sCurrentLine+" - "+x);
		}
		System.out.println(count);
	}

}
