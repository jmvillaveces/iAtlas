package mpg.biochem.de.interbase.batch;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.beans.factory.annotation.Value;

public class IndexDecider implements JobExecutionDecider {

	@Value("${path}")
	private String path;
	
	@Value("${index.create}")
	private boolean createIndex;
	
	private Logger logger = Logger.getLogger(IndexDecider.class);
	
	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		
		//Skip index creation
		String step = (String) jobExecution.getExecutionContext().get("STEP");
		if(!createIndex){
			logger.info("index.create = false. Decided to SKIP "+step);
			return new FlowExecutionStatus("SKIP");
		}
		
		
		if(step.equals("create_index")){
			//Get index value from Mappings tasklet
			boolean value = (Boolean) jobExecution.getExecutionContext().get("INDEX");
			
			File f = new File(path+"mapping_index");
			if (!value) {
				if(f.exists()){
					logger.info("Decided to SKIP "+step);
					jobExecution.getExecutionContext().put("STEP", "create_mapping_file");
					return new FlowExecutionStatus("SKIP");
				}
		    }
		    
			logger.info("Decided to CONTINUE "+step);
			if(f.exists()){
		    	try {
					FileUtils.deleteDirectory(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
			
			jobExecution.getExecutionContext().put("INDEX", true);
			jobExecution.getExecutionContext().put("STEP", "create_mapping_file");
		    return new FlowExecutionStatus("CONTINUE");
		}else if(step.equals("create_mapping_file")){
			boolean fetched = (Boolean) jobExecution.getExecutionContext().get("fetched_something");
			boolean indexed = (Boolean) jobExecution.getExecutionContext().get("INDEX");
			
			if(fetched || indexed){
				logger.info("Decided to CONTINUE "+step);
				return new FlowExecutionStatus("CONTINUE");
			}
			logger.info("Decided to SKIP "+step);
			return new FlowExecutionStatus("SKIP");
		}
		
		return null;
	}

}
