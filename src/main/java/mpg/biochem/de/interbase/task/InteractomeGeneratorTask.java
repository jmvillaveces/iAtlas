package mpg.biochem.de.interbase.task;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mpg.biochem.de.interbase.batch.FlatFileItemIoFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.FileSystemResource;

import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.Interactor;
import psidev.psi.mi.tab.model.Organism;

public class InteractomeGeneratorTask implements Tasklet {

	private static final Log logger = LogFactory.getLog(InteractomeGeneratorTask.class);
	
	private FlatFileItemIoFactory<BinaryInteraction> outputIoFactory;
	private FlatFileItemIoFactory<BinaryInteraction> inputIoFactory;
	private FileSystemResource inputResource;
	private String organisms;
	private List<String> orgs;
	private String path;
	private Map<String, FlatFileItemWriter<BinaryInteraction>> writers;
	private Map<String, List<BinaryInteraction>> interactions;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		// Check tasklet's state
		if (inputResource == null)
			throw new IllegalStateException("no input resource specified");
		if (inputIoFactory == null)
			throw new IllegalStateException("no input I/O factory defined");
		if (outputIoFactory == null)
			throw new IllegalStateException("no output I/O factory defined");
		if (path == null)
			throw new IllegalStateException("no output path defined");
		if (organisms == null)
			throw new IllegalStateException("no organism(s) defined");
		
		orgs = Arrays.asList(organisms.split(","));
		if (orgs == null || orgs.size() ==0)
			return  RepeatStatus.FINISHED;
		
		//Init Interactions
		interactions = new HashMap<String, List<BinaryInteraction>>();
		//Init Writers
		writers = new HashMap<String, FlatFileItemWriter<BinaryInteraction>>();
		
		
		// Get output and input files
		File inputFile = inputResource.getFile();

		// Execution context
		ExecutionContext context = new ExecutionContext();

		// Prepare reader and writer
		FlatFileItemReader<BinaryInteraction> reader = inputIoFactory.getReader(inputResource);
		reader.open(context);
				
		logger.info("input file, '" + inputFile.getAbsolutePath()+ "' opened for reading");
		
		BinaryInteraction interaction = reader.read();
		while(interaction != null){
			Interactor intA = interaction.getInteractorA();
			Interactor intB = interaction.getInteractorB();
			
			Organism orgA = intA.getOrganism();
			String taxIdA = orgA.getTaxid();
			
			String taxIdB = taxIdA;
			if(intB != null)
				taxIdB = intB.getOrganism().getTaxid();
			
			if(taxIdA != null && taxIdB != null && taxIdA.equals(taxIdB) && orgs.contains(taxIdA)){
				saveInteraction(interaction, taxIdA);
			}
			interaction = reader.read();
		}
		
		for(String taxId : interactions.keySet()){
			if(interactions.get(taxId).size()>0){
				writeInteractions(interactions.get(taxId), taxId);
			}
			writers.get(taxId).close();
		}
		
		return RepeatStatus.FINISHED;
	}
	
	private void saveInteraction(BinaryInteraction interaction, String taxId) throws Exception{
		List<BinaryInteraction> lst;
		if(!interactions.containsKey(taxId)){
			lst = new ArrayList<BinaryInteraction>();
			interactions.put(taxId, lst);
			
		}else{
			lst = interactions.get(taxId);
		}
		lst.add(interaction);
		
		if(lst.size() >= 500){
			writeInteractions(lst, taxId);
			lst.clear();
		}
	}
	
	private void writeInteractions(List<BinaryInteraction> interactions, String taxId) throws Exception{
		FlatFileItemWriter<BinaryInteraction> writer;
		if(!writers.containsKey(taxId)){
			writer = inputIoFactory.getWriter(new FileSystemResource(path+taxId+".tab"));
			writer.open(new ExecutionContext());
			writers.put(taxId, writer);
		}else{
			writer = writers.get(taxId);
		}
		writer.write(interactions);
	}
	
	public void setInputResource(FileSystemResource inputResource) {
		this.inputResource = inputResource;
	}

	public void setOrganisms(String organisms) {
		this.organisms = organisms;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public void setInputIoFactory(FlatFileItemIoFactory<BinaryInteraction> inputIoFactory) {
		this.inputIoFactory = inputIoFactory;
	}
	
	public void setOutputIoFactory(FlatFileItemIoFactory<BinaryInteraction> outputIoFactory) {
		this.outputIoFactory = outputIoFactory;
	}
}
