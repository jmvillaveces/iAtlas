package mpg.biochem.de.interbase.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class PatternTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*Pattern uniprot = Pattern.compile("[A-NR-Z][0-9][A-Z][A-Z0-9][A-Z0-9][0-9]|[OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9]");
		Pattern dotNumber = Pattern.compile("^.+(\\.|-)\\d$");
		
		String[] ids = new String[]{"A2BC19.1", "P12345-1", "P29358"};
		
		
		for(String id : ids){
			String[] ops = id.split("\\.|-\\d");
			
			
			System.out.println(ops[0]);
		}*/
		
		/*Matcher matcher = dotNumber.matcher(ids[0]);
		System.out.println(matcher.matches());
		
		matcher = dotNumber.matcher(ids[1]);
		System.out.println(matcher.matches());
		
		matcher = dotNumber.matcher(ids[2]);
		System.out.println(matcher.matches());*/
		
		
		String keggorganisms="aac,abo,abu,aca,ace,acl,acr,ade,adg,aeh,ajs,ami,amr,amt,amu,ana,ape,apl,app,apr,aps,apt,ath,avn,bba,bbr,bbu,bci,bcv,bfa,bfl,bhe,bid,bja,blo,bme,bpi,bps,bsu,bth,buc,cac,cai,cal,cau,cbu,ccr,ccu,cef,cel,chu,chy,cje,cko,cma,cmi,coc,cpa,cpc,cpi,cpn,cpo,cps,crp,csa,csc,cte,cts,ctt,cvi,cyt,dac,dar,dau,dda,det,dfe,dia,dka,dme,dno,dol,dosa,dps,dra,dre,drm,dsh,dsy,dth,dvu,eba,eca,eco,eel,efa,ele,eli,emi,eru,esa,esi,fba,fjo,fma,fno,fra,fsu,ftu,gau,gbe,gbr,gdi,gfo,gga,gka,gox,gsu,gvi,hau,hba,hsa,las,lsp,mag,mdo,mla,mlu,mma,mms,mmu,osa,pdi,pol,pvi,rha,rno,sce,scl,sco,spo,sro,sus,xla";
		String[] orgs = keggorganisms.split(",");
		
		BufferedReader br = null;
		 
		try {
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader("/users/jvillaveces/Desktop/kegg2taxonomy.txt"));
 
			while ((sCurrentLine = br.readLine()) != null) {
				
				for(String org : orgs){
					if(sCurrentLine.startsWith(org+" ")){
						System.out.println(sCurrentLine);
						break;
					}
				}
				
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
 
	}
		
	

}
