package mpg.biochem.de.interbase.task;

import java.io.File;

import mpg.biochem.de.interbase.util.PushOverNotificationManager;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;

public class TestRunTask {
	
	@Value("${proxy.url}")
	private String proxy;
	
	@Value("${proxy.port}")
	private String port;
	
	@Value("${path}")
	private String path;
	
	private JobLauncher jobLauncher;
	private Job job;
	
	public TestRunTask(JobLauncher jobLauncher, Job job){
		this.jobLauncher = jobLauncher;
		this.job = job;
	}
	
	public void start() throws Exception {
		
		//prepare folders
		new File(path).mkdirs();
		new File(path+"data").mkdirs();
		new File(path+"services").mkdirs();
		new File(path+"mapping").mkdirs();
		
		PushOverNotificationManager.sendNotification("About to start the iAtlas job");
		jobLauncher.run(job, new JobParametersBuilder().toJobParameters());
	}
}
