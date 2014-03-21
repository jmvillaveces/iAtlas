package mpg.biochem.de.interbase.task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import mpg.biochem.de.interbase.batch.FlatFileItemIoFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.FileSystemResource;

import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.Confidence;



public class ScoreDistributionTask implements Tasklet {

    private static final Log logger = LogFactory.getLog(ScoreDistributionTask.class);
	
    private String scoreName;
    private String outPath;
    private FileSystemResource inputResource;
    private FlatFileItemIoFactory<BinaryInteraction> inputIoFactory;
    
    private FlatFileItemReader<BinaryInteraction> reader;

	public ScoreDistributionTask(){
		inputResource = null;
		inputIoFactory = null;
		scoreName = null;
		outPath = null;
	}
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		// Check tasklet's state
		if (inputResource == null)
			throw new IllegalStateException("no input resource specified");
		if (inputIoFactory == null)
			throw new IllegalStateException("no input I/O factory defined");
		if (outPath == null)
			throw new IllegalStateException("no output path defined");
		if (scoreName == null)
			throw new IllegalStateException("no score name defined");
		
		// Get output and input files
		File inputFile = inputResource.getFile();

		// Execution context
		ExecutionContext context = new ExecutionContext();

		// Prepare reader and writer
		reader = inputIoFactory.getReader(inputResource);
		reader.open(context);
		
		logger.info("input file, '" + inputFile.getAbsolutePath()+ "' opened for reading");
		
		int[] dist = new int[]{0,0,0,0,0,0,0,0,0,0};
		BinaryInteraction interaction = reader.read();
		while(interaction != null){
			List<Confidence> scores = interaction.getConfidenceValues();
			for(Confidence c : scores){
				if(c.getType().equals(scoreName)){
					double value = Double.parseDouble(c.getValue());
					if(value < 0.1){
						dist[0] = dist[0] + 1;
					}else if(value < 0.2){
						dist[1] = dist[1] + 1;
					}else if(value < 0.3){
						dist[2] = dist[2] + 1;
					}else if(value < 0.4){
						dist[3] = dist[3] + 1;
					}else if(value < 0.5){
						dist[4] = dist[4] + 1;
					}else if(value < 0.6){
						dist[5] = dist[5] + 1;
					}else if(value < 0.7){
						dist[6] = dist[6] + 1;
					}else if(value < 0.8){
						dist[7] = dist[7] + 1;
					}else if(value < 0.9){
						dist[8] = dist[8] + 1;
					}else if(value < 1){
						dist[9] = dist[9] + 1;
					}
					break;
				}
			}
			interaction = reader.read();
		}
		reader.close();
			
		File file = new File(outPath);
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		int total = 0;
		for(int i=0; i<dist.length; i++){
			total += dist[i];
			bw.write(dist[i]+"\n");
		}
		
		bw.write("total\t"+total);
		bw.close();
		
		return  RepeatStatus.FINISHED;
	}
	
	public void setInputResource(FileSystemResource inputResource) {
		this.inputResource = inputResource;
	}

	public void setInputIoFactory(FlatFileItemIoFactory<BinaryInteraction> inputIoFactory) {
		this.inputIoFactory = inputIoFactory;
	}

	public void setOutPath(String outPath) {
		this.outPath = outPath;
	}
	
	public void setScoreName(String scoreName) {
		this.scoreName = scoreName;
	}
}
