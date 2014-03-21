package mpg.biochem.de.interbase.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mpg.biochem.de.interbase.model.Service;
import mpg.biochem.de.interbase.model.ServiceHandler;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import psidev.psi.mi.tab.PsimiTabWriter;
import psidev.psi.mi.tab.model.BinaryInteraction;
import uk.ac.ebi.enfin.mi.cluster.Encore2Binary;
import uk.ac.ebi.enfin.mi.cluster.EncoreBinaryInteraction;
import uk.ac.ebi.enfin.mi.cluster.score.InteractionClusterScore;

public class ScoreTasklet implements Tasklet {


	private List<String> servicesToIgnore;
	private ServiceHandler serviceHandler;
	private String path;
	
	private static final Logger logger = Logger.getLogger(ScoreTasklet.class);
	
	public ScoreTasklet(String path, ServiceHandler serviceHandler, String[] servicesToIgnore){
		this.serviceHandler = serviceHandler;
		this.servicesToIgnore = Arrays.asList(servicesToIgnore);
		this.path = path;
	}
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		logger.info("About to create the MEGA-BASE");
		
		mergeFiles();
		
		//send to properties file
		final String allMappingNames = "uniprotkb,intact,ddbj/embl/genbank,chebi,irefindex,hgnc,ensembl";
       
		InputStream is = new FileInputStream(path+"union.clustered");
		
		/* Cluster data */
        InteractionClusterScore iCluster = new InteractionClusterScore();
        iCluster.setBinaryInteractionIterator(is, true);
        iCluster.setScoreName("miscore");
        iCluster.setMappingIdDbNames(allMappingNames);
        iCluster.runService();
        
        ArrayList<BinaryInteraction> interactions = new ArrayList<BinaryInteraction>();
	    Encore2Binary iConverter = new Encore2Binary(iCluster.getMappingIdDbNames());
	    for(EncoreBinaryInteraction eI : iCluster.getInteractionMapping().values()){
	    	interactions.add(iConverter.getBinaryInteraction(eI));
	    }
	    
	    /* Save to file */
	    File file = new File(path+"INTERBASE");
		FileOutputStream fop = new FileOutputStream(file);
	    
	    /* Print PSI MITAB clustered binary interactions */
        PsimiTabWriter writer = new PsimiTabWriter();
        writer.write(interactions, fop);
		
        logger.info("MEGA-BASE created");
        
		return RepeatStatus.FINISHED;
	}
	
	private void mergeFiles() throws IOException{
		
		logger.info("About to create the unified file");
		
		OutputStream out = new FileOutputStream(new File(path+"union.clustered"));
		List<Service> services = serviceHandler.getServiceList();
		for(Service service : services){
			if(service.isClustered() && !servicesToIgnore.contains(service.getName())){
				
				logger.info("Adding file "+path+service.getName()+".clustered");
				
				InputStream io = new FileInputStream(path+service.getName()+".clustered");
				
				boolean first = true;
				int read = 0;
				byte[] bytes = new byte[1024];
				while ((read = io.read(bytes)) != -1) {
					if(first){
						first = false;
					}else{
						out.write(bytes, 0, read);
					}
				}
				io.close();
				out.flush();
				
				logger.info(path+service.getName()+".clustered File added" );
			}
		}
		out.close();
		
		logger.info("Unified file created");
	}

}
