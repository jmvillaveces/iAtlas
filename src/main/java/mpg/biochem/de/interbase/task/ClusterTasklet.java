package mpg.biochem.de.interbase.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import mpg.biochem.de.interbase.model.Service;
import mpg.biochem.de.interbase.model.ServiceHandler;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.task.TaskExecutor;

public class ClusterTasklet implements Tasklet {

	private TaskExecutor taskExecutor;
	private String path;
	private ServiceHandler serviceHandler;
	private List<String> servicesToIgnore;

	public ClusterTasklet(TaskExecutor taskExecutor, String path, ServiceHandler serviceHandler, String[] servicesToIgnore){
		this.taskExecutor = taskExecutor;
		this.path = path;
		this.serviceHandler = serviceHandler;
		this.servicesToIgnore = Arrays.asList(servicesToIgnore);
	}
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		List<Service> services = serviceHandler.getServiceList(), toCluster = new ArrayList<Service>();
		for(Service service : services){
			if(!service.isClustered() && !servicesToIgnore.contains(service.getName())){
				toCluster.add(service);
			}
		}
		
		CountDownLatch latch = new CountDownLatch(toCluster.size());
		for(Service service : toCluster){
			taskExecutor.execute(new ClusterTask(path + service.getName(), service, serviceHandler, latch));
		}
		
		latch.await();
		
		return RepeatStatus.FINISHED;
	}

}
