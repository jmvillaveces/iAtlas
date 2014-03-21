package mpg.biochem.de.interbase.util;

import java.util.Map;

import mpg.biochem.de.interbase.service.PICRService;

public class PICRCache {
	
	private Map<String, String> cache;
	private PICRService service;
	
	/*public PICRCache(){
		cache = new HashMap<String, String>();
		service = new PICRService();
	}
	
	public String getUniprotBestGuessFor(String accession, String taxonId){
		
		if(cache.size() >= 1000)
			cache.clear();
		
		String newAccession = "";
		if(cache.containsKey(accession)){
			newAccession = cache.get(accession);
		}else{
			String result = service.getUniprotBestGuessFor(accession, taxonId);
			if(!result.equals("")){
				cache.put(accession, result);
				newAccession = result;
			}else{
				cache.put(accession, accession);
				newAccession = accession;
			}
		}
		return newAccession;
	}
	
	public void clear(){
		cache.clear();
	}*/
	
}
