package mpg.biochem.de.interbase.task;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mpg.biochem.de.interbase.service.KeggService;
import mpg.biochem.de.interbase.util.Item;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;

public class KeggMappingIndexTask implements Tasklet {

	private IndexWriter indexWriter;
	private Version lVersion = Version.LUCENE_35;
	private String path;
	
	@Value("${kegg.organisms}")
	private String[] organisms;
	
	private Logger logger = Logger.getLogger(KeggMappingIndexTask.class);
	
	public KeggMappingIndexTask(String path){
		this.path = path;
	}
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		IndexWriterConfig conf = new IndexWriterConfig(lVersion, new StandardAnalyzer(lVersion));
		indexWriter = new IndexWriter(FSDirectory.open(new File(path)), conf);
		
		KeggService service = KeggService.getInstance();
	
		for(int i=0; i<organisms.length; i++){
			String[] organism = organisms[i].split(" ");
			
			logger.info("Indexing organism "+StringUtils.join(organism,":")+" "+(i+1)+" out of "+organisms.length);
			
			Item[] uniprotIds = service.getMappingsByOrganism(organism[0], service.UNIPROT);
			Item[] ncbiGeneIds = service.getMappingsByOrganism(organism[0], service.NCBI_GENE_ID);
			Item[] ncbiGI = service.getMappingsByOrganism(organism[0], service.NCBI_GI);
			
			Map<String, List<String>> uniprot = getMap(uniprotIds);
			Map<String, List<String>> ids = getMap((Item[]) ArrayUtils.addAll(ncbiGeneIds, ncbiGI));
			
			for(String key : uniprot.keySet()){
				
				String uid = uniprot.get(key).get(0);
				if(!uid.equals("")){
					Document doc = new Document();
					
					doc.add(new Field("id", uid, Field.Store.YES, Field.Index.NOT_ANALYZED));
					doc.add(new Field("altId", uid, Field.Store.YES, Field.Index.NOT_ANALYZED));
					
					doc.add(new Field("taxId", organism[1], Field.Store.YES, Field.Index.NOT_ANALYZED));
					
					if(ids.containsKey(key)){
						List<String> lst = ids.get(key);
						for(String id : lst)
							if(!id.equals(""))
								doc.add(new Field("altId", id, Field.Store.YES, Field.Index.NOT_ANALYZED));
					}
					
					indexWriter.addDocument(doc);
				}
			}
			indexWriter.commit();
		}
		
		indexWriter.close();
		return  RepeatStatus.FINISHED;
	}
	
	private Map<String, List<String>> getMap(Item[] items){
		Map<String, List<String>> ids = new HashMap<String, List<String>>();
		for(Item item : items){
			String key = item.getId();
			
			if(ids.containsKey(key)){
				ids.get(key).add(item.getDescription());
			}else{
				List<String> lst = new ArrayList<String>();
				lst.add(item.getDescription());
				ids.put(key, lst);
			}
		}
		return ids;
	}
	
}
