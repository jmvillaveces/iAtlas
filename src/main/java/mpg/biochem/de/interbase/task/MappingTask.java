package mpg.biochem.de.interbase.task;

import java.util.ArrayList;
import java.util.List;

import mpg.biochem.de.interbase.batch.InterBaseProcessor;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.task.TaskExecutor;

import psidev.psi.mi.tab.model.BinaryInteraction;

public class MappingTask implements Tasklet {

	private TaskExecutor taskExecutor;
	
	private FlatFileItemWriter<BinaryInteraction> writer;
    private MultiResourceItemReader<BinaryInteraction> reader;
    private String indexPath;
	private String indexLogPath;
	
    public MappingTask(){
    	writer = null;
    	reader = null;
    	indexPath = null;
    	indexLogPath = null;
    }
    
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// Check tasklet's state
		if (writer == null)
			throw new IllegalStateException("no writer specified");
		if (reader == null)
			throw new IllegalStateException("no reader specified");
		if (indexPath == null)
			throw new IllegalStateException("no index path specified");
		if (indexLogPath == null)
			throw new IllegalStateException("no index log path specified");
		
		writer.setTransactional(false);
		writer.open(new ExecutionContext());
		
		InterBaseProcessor processor = new InterBaseProcessor(indexPath, indexLogPath);
		List<BinaryInteraction> writeList = new ArrayList<BinaryInteraction>();
		BinaryInteraction interaction = reader.read();
		while(interaction != null){
			
			String resource = reader.getCurrentResource().getFilename().substring(0, reader.getCurrentResource().getFilename().indexOf("."));
			//processor.setResource(resource);
			
			interaction = processor.process(interaction);
			if (writeList.size() < 100000) {
				writeList.add(interaction);
			} else {
				writer.write(writeList);
				writeList = new ArrayList<BinaryInteraction>();
				System.gc();
				writeList.add(interaction);
			}
			interaction = reader.read();
		}
		if(writeList.size()>0)
			writer.write(writeList);
		
		writer.close();
		return RepeatStatus.FINISHED;
	}

	public void setWriter(FlatFileItemWriter<BinaryInteraction> writer) {
		this.writer = writer;
	}

	public void setReader(MultiResourceItemReader<BinaryInteraction> reader) {
		this.reader = reader;
	}

	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}

	public void setIndexLogPath(String indexLogPath) {
		this.indexLogPath = indexLogPath;
	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}
}
