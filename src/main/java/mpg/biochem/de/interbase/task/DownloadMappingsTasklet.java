package mpg.biochem.de.interbase.task;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;

public class DownloadMappingsTasklet implements Tasklet  {

	private Logger logger = Logger.getLogger(DownloadMappingsTasklet.class);
	
	@Value("${mapping.uniprot.url}")
	private String uniprotUrl;
	
	@Value("${path}")
	private String path;
	private String uniprotDownloadPath;
	
	@Value("${mapping.uniprot.download}")
	private boolean uniprotDownload;
	
	@Value("${mapping.ncbi.url}")
	private String ncbiUrl;
	
	private String ncbiDownloadPath;
	
	@Value("${mapping.ncbi.download}")
	private boolean ncbiDownload;
	
	public DownloadMappingsTasklet(){}
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		uniprotDownloadPath = path + "mapping/idmapping_selected.tab.gz";
		ncbiDownloadPath = path + "mapping/gene2accession.gz";
		
		boolean index = false;
		if(uniprotDownload){
			
			File mapping = new File(uniprotDownloadPath);
			if(mapping.exists() && FileUtils.isFileNewer(mapping, new DateTime().minusWeeks(2).toDate())){
				logger.info("Uniprot mapping file is up to date in "+uniprotDownloadPath);
				//Do nothing
			}else{
				//Download the file
				logger.info("Downloading uniprot mapping file to "+uniprotDownloadPath );
				FileUtils.copyURLToFile(new URL(uniprotUrl), mapping);
				index = true;
			}
		}else{
			logger.info("Download disabled, mapping.uniprot.download = false");
		}
		
		if(ncbiDownload){
			
			File mapping = new File(ncbiDownloadPath);
			if(mapping.exists() && FileUtils.isFileNewer(mapping, new DateTime().minusWeeks(2).toDate())){
				logger.info("NCBI mapping file is up to date in "+ncbiDownloadPath);
				//Do nothing
			}else{
				//Download the file
				logger.info("Downloading ncbi mapping file to "+ncbiDownloadPath);
				FileUtils.copyURLToFile(new URL(ncbiUrl), mapping);
				index = true;
			}
		}else{
			logger.info("Download disabled, mapping.ncbi.download = false");
		}
		
		chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("STEP", "create_index");
		chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("INDEX", index);
		return RepeatStatus.FINISHED;
	}
}
