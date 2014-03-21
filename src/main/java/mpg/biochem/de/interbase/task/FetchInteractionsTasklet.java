package mpg.biochem.de.interbase.task;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import mpg.biochem.de.interbase.model.Service;
import mpg.biochem.de.interbase.model.ServiceHandler;

import org.hupo.psi.mi.psicquic.registry.ServiceType;
import org.hupo.psi.mi.psicquic.registry.client.registry.PsicquicRegistryClient;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;

public class FetchInteractionsTasklet implements Tasklet{

	private PsicquicRegistryClient registryClient;
	private TaskExecutor taskExecutor;
	private ServiceHandler serviceHandler;
	
	@Value("${path}")
	private String path;
	
	@Value("${cluster.force}")
	private boolean force;
	
	@Value("${psicquic.max.results}")
	private int maxResults;
	
	@Value("${psicquic.max.attempts}")
	private int maxAtempts;
	
	@Value("${psicquic.waiting.interval}")
	private int waiting;
	
	@Value("${services.to.ignore}")
	private String[] servicesToIgnore;
	
	public FetchInteractionsTasklet(PsicquicRegistryClient registryClient, TaskExecutor taskExecutor, ServiceHandler serviceHandler){
		this.registryClient = registryClient;
		this.taskExecutor = taskExecutor;
		this.serviceHandler = serviceHandler;
	}
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		createFile();
		
		List<ServiceType> services; 
		List<Service> toIndex = new ArrayList<Service>();
		List<String> toIgnore = Arrays.asList(servicesToIgnore);
		
		services = registryClient.listServices();	
	
		for (ServiceType service : services) {
			if(service.isActive() && !toIgnore.contains(service.getName())){
					
				Service ser = serviceHandler.getService(service.getName()); 
				if(ser == null){
					ser = new Service(service.getName(), service.getRestUrl(), service.getVersion(), service.getCount());
					serviceHandler.add(ser);
					toIndex.add(ser);
				}else if(!ser.isIndexed()){
					toIndex.add(ser);
				}else if(!ser.getVersion().equals(service.getVersion()) || ser.getCount() != service.getCount()){
					ser.setCount(service.getCount());
					ser.setVersion(service.getVersion());
					
					serviceHandler.add(ser);
					toIndex.add(ser);
				}
			}
		}
		
		boolean bool = true;
		if(toIndex.size() == 0 && !force)
			bool = false;
		
		chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("fetched_something", bool);
		
		CountDownLatch latch = new CountDownLatch(toIndex.size());
		for(Service service : toIndex){
			
			ServerDataFetcherTask task = new ServerDataFetcherTask(service, serviceHandler, latch);
			task.setPath(path+"services/");
			task.setMaxResults(maxResults);
			task.setMaxAtempts(maxAtempts);
			task.setWaiting(waiting);
			taskExecutor.execute(task);
		}
		latch.await();
		
		Thread.sleep(2000);
		return RepeatStatus.FINISHED;
	}
	
	private void createFile(){
		File theDir = new File(path);
		
		// if the directory does not exist, create it
		if (!theDir.exists()){
			theDir.mkdir(); 
		}
	}

}
