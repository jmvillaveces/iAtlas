package mpg.biochem.de.interbase;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
	
	private static ApplicationContext ctx = null;
	
	public static void main(String[] args) throws Exception {
		
		if(args.length > 1){
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
		}else{
			ctx = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/iAtlasJob.xml");
		}
	}
}
