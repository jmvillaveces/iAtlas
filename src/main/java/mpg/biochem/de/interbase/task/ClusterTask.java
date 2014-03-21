package mpg.biochem.de.interbase.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import mpg.biochem.de.interbase.model.Service;
import mpg.biochem.de.interbase.model.ServiceHandler;

import org.apache.log4j.Logger;

import psidev.psi.mi.tab.PsimiTabWriter;
import psidev.psi.mi.tab.model.BinaryInteraction;
import uk.ac.ebi.enfin.mi.cluster.ClusterServiceException;
import uk.ac.ebi.enfin.mi.cluster.Encore2Binary;
import uk.ac.ebi.enfin.mi.cluster.EncoreBinaryInteraction;
import uk.ac.ebi.enfin.mi.cluster.InteractionCluster;

public class ClusterTask implements Runnable {

	/* Set priority for molecule accession mapping (find more database names in the MI Ontology, MI:0473) */
    private final String allMappingNames = "uniprotkb,intact,ddbj/embl/genbank,chebi,irefindex,hgnc,ensembl";
	private String path;
	private Service service;
	private ServiceHandler serviceHandler;
	private CountDownLatch latch;
    
	private static final Logger logger = Logger.getLogger(ClusterTask.class);
	
    public ClusterTask(String path, Service service, ServiceHandler serviceHandler, CountDownLatch latch){
    	this.path = path;
    	this.service = service;
    	this.serviceHandler = serviceHandler;
    	this.latch = latch;
    }
    
	@Override
	public void run() {
		
		logger.info("About to cluster file "+path);
		
		try{
			InputStream is = new FileInputStream(path);
			
			/* Cluster data */
			InteractionCluster iCluster = new InteractionCluster();
	        iCluster.setBinaryInteractionIterator(is, false);
	        iCluster.setMappingIdDbNames(allMappingNames);
	        iCluster.runService();
	        
	        ArrayList<BinaryInteraction> interactions = new ArrayList<BinaryInteraction>();
		    Encore2Binary iConverter = new Encore2Binary(iCluster.getMappingIdDbNames());
		    for(EncoreBinaryInteraction eI : iCluster.getInteractionMapping().values()){
		    	interactions.add(iConverter.getBinaryInteraction(eI));
		    }
		    
		    /* Save to file */
		    File file = new File(path+".clustered");
			FileOutputStream fop = new FileOutputStream(file);
		    
		    /* Print PSI MITAB clustered binary interactions */
	        PsimiTabWriter writer = new PsimiTabWriter();
	        writer.write(interactions, fop);
	        
	        /* Save service */
	        service.setCountAfterClustering(interactions.size());
	        service.setClustered(true);
	        serviceHandler.add(service);
	        
	        logger.info("Clustered file "+path);
		}catch (ClusterServiceException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			latch.countDown();
		}
		
	}

}
