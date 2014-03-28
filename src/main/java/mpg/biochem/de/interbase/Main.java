package mpg.biochem.de.interbase;

import java.io.File;
import java.util.Properties;
import java.util.UUID;

import mpg.biochem.de.interbase.util.PushOverNotificationManager;

import org.apache.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class Main {
	
	private static ApplicationContext ctx = null;
	private static Logger logger = Logger.getLogger(Main.class);
	
	public static void main(String[] args) throws Exception {
		
		/*if(args.length > 1){
			String cmd = args[0];
			
			if(cmd.equalsIgnoreCase("cluster")){
				ctx = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/Cluster.xml");
				
				Job job = (Job) ctx.getBean("clusterJob");
				JobLauncher jobLauncher = (JobLauncher) ctx.getBean("jobLauncher");
				
				JobParametersBuilder jobParameters = new JobParametersBuilder();
				jobParameters.addString("fileName", args[1]);
				
				jobLauncher.run(job, jobParameters.toJobParameters());
				
			}else if(cmd.equalsIgnoreCase("interactome")){
				ctx = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/Interactomes.xml");
				
				Job job = (Job) ctx.getBean("interactomeJob");
				JobLauncher jobLauncher = (JobLauncher) ctx.getBean("jobLauncher");
				
				JobParametersBuilder jobParameters = new JobParametersBuilder();
				jobParameters.addString("fileName", args[1]);
				jobParameters.addString("path", args[2]);
				jobParameters.addString("organisms", args[3]);
				//"9606,7227,10090,10114,7955,3702,6239,559292,284812,83333"
				
				jobLauncher.run(job, jobParameters.toJobParameters());
				
			}
		}else{*/
		
		ctx = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/iAtlasJob.xml");
		
		JobLauncher jobLauncher = (JobLauncher) ctx.getBean(JobLauncher.class);
		
		Resource resource = new ClassPathResource("InterBase.properties");
		Properties props = PropertiesLoaderUtils.loadProperties(resource);
		
		String path = props.getProperty("path");
		new File(path).mkdirs();
		new File(path+"data").mkdirs();
		new File(path+"services").mkdirs();
		new File(path+"mapping").mkdirs();
		
		if(args.length == 0){
			
			UUID id = UUID.randomUUID();
			logger.info("Job with id "+id.toString()+" is about to start");
			
			JobParameters params = new JobParametersBuilder().addString("id", id.toString()).toJobParameters();
			Job job = (Job) ctx.getBean("mainJob");
			
			JobExecution execution = jobLauncher.run(job, params);
		}else if(args.length == 1){
			UUID id = UUID.randomUUID();
			logger.info("Job with id "+id.toString()+" is about to start");
			
			JobParameters params = new JobParametersBuilder().addString("id", id.toString()).toJobParameters();
			Job job = (Job) ctx.getBean(args[0]);
			
			JobExecution execution = jobLauncher.run(job, params);
		}
	}
}
