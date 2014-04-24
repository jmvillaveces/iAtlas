package mpg.biochem.de.interbase.task;

import java.io.File;

import mpg.biochem.de.interbase.util.ExecuteShellComand;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;


public class MiTabIndexerTask implements Tasklet {
	
	@Value("${path}")
	private String path;
	
	@Value("${solr.path}")
	private String solr;
	
	@Value("${psicquic.solr}")
	private String psicquicSolr;
	
	public MiTabIndexerTask() {}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
	
		File clustered = new File(path + "data/clustered.tab");
		File indexer = new File(psicquicSolr);
		
		ExecuteShellComand obj = new ExecuteShellComand();
		
		String cmd = "bash "+indexer.getAbsolutePath()+" "+clustered.getAbsolutePath()+" "+solr;
		obj.executeCommand(cmd);
		
		return null;
	}

}
