package mpg.biochem.de.interbase.batch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.core.task.TaskExecutor;

import psidev.psi.mi.tab.PsimiTabWriter;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.builder.MitabWriterUtils;
import psidev.psi.mi.tab.model.builder.PsimiTabVersion;
import uk.ac.ebi.enfin.mi.cluster.Encore2Binary;
import uk.ac.ebi.enfin.mi.cluster.EncoreBinaryInteraction;
import uk.ac.ebi.enfin.mi.cluster.EncoreInteraction;
import uk.ac.ebi.enfin.mi.cluster.InteractionCluster;

public class ClusterWriter implements ItemStreamWriter<BinaryInteraction>{

	private String filePath, mappings="uniprotkb,chebi,refseq,ddbj/embl/genbank,irefindex";
	private PsimiTabWriter writer;
	private FileOutputStream fop;
	
	private List<List<? extends BinaryInteraction>> queue;
	private CountDownLatch latch;
	private TaskExecutor taskExecutor;
	
	public ClusterWriter(String filePath, TaskExecutor taskExecutor){
		this.filePath = filePath;
		this.taskExecutor = taskExecutor;
	}
	
	@Override
	public void close() throws ItemStreamException {
		checkQueue(true);
		try {
			fop.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void open(ExecutionContext arg0) throws ItemStreamException {
		queue = new ArrayList<List<? extends BinaryInteraction>>();
		try{
			/* Save to file */
		    File file = new File(filePath);
			fop = new FileOutputStream(file);
		    
		    /* Print PSI MITAB clustered binary interactions */
	        writer = new PsimiTabWriter();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void update(ExecutionContext arg0) throws ItemStreamException {
		// TODO Auto-generated method stub
	}

	@Override
	public void write(List<? extends BinaryInteraction> interactions) throws Exception {
		queue.add(interactions);
		checkQueue(false);
	}
	
	private void checkQueue(boolean close) {
		if(close || queue.size() == 10){
			latch = new CountDownLatch(queue.size());
			for(List<? extends BinaryInteraction> lines : queue){
				ClusterTask task = new ClusterTask(lines);
				taskExecutor.execute(task);
			}
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			queue.clear();
		}
	}
	
	private synchronized void write(Collection<EncoreInteraction> interactions,  Encore2Binary iConverter) throws IOException{
		for(EncoreBinaryInteraction eI : interactions){
	    	//Mitab String
			String intStr = MitabWriterUtils.buildLine(iConverter.getBinaryInteraction(eI), PsimiTabVersion.v2_5);
	    	fop.write(intStr.getBytes());
	    }
	    fop.flush();
	}
	
	public class ClusterTask implements Runnable {

		private List<? extends BinaryInteraction> interactions;
		
		public ClusterTask(List<? extends BinaryInteraction> interactions){
			this.interactions = interactions;
		}
		
		@Override
		public void run() {
			/* Cluster data */
			InteractionCluster iCluster = new InteractionCluster();
	        iCluster.setBinaryInteractionIterator((Iterator<BinaryInteraction>) interactions.iterator());
	        iCluster.setMappingIdDbNames(mappings);
	        iCluster.runService();
	        
	        try {
				write(iCluster.getInteractionMapping().values(), new Encore2Binary(iCluster.getMappingIdDbNames()));
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				latch.countDown();
			}
		}
		
	}
}
