package mpg.biochem.de.interbase.task;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;

public class DownloadNCBIMappingsTasklet implements Tasklet{

	private Logger logger = Logger.getLogger(DownloadNCBIMappingsTasklet.class);
	
	@Value("${ftp.proxy.url}")
	private String ftpProxy;
	
	@Value("${ftp.proxy.port}")
	private String ftpPort;
	
	@Value("${mapping.ncbi.url}")
	private String url;
	
	@Value("${path}")
	private String downloadPath;
	
	@Value("${mapping.ncbi.download}")
	private boolean download;
	
	@Value("${ncbi.ftp}")
	private String ftp;
	
	@Value("${ncbi.username}")
	private String username;
	
	@Value("${ncbi.password}")
	private String password;
	
	public DownloadNCBIMappingsTasklet(){
		if(password == null)
			password = "";
		
		downloadPath += "/mapping/gene2accession.gz";
	}
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		boolean index = false;
		DateTime ftpModDate = getLastModifiedDate();
		if(download){
			
			File mappings = new File(downloadPath);
			if(mappings.exists() && FileUtils.isFileNewer(mappings, ftpModDate.toDate())){
				logger.info("Mappings file is up to date");
				//Do nothing
			}else{
				//Download the file
				logger.info("Downloading ncbi mapping file");
				FileUtils.copyURLToFile(new URL(url), mappings);
				index = true;
			}
			
		}else{
			logger.info("Download disabled, mapping.download = false");
		}
		
		chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("STEP", "create_index");
		chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("INDEX", index);
		return RepeatStatus.FINISHED;
	}
	
	public DateTime getLastModifiedDate() throws SocketException, IOException{
		
		FTPClient ftpClient = new FTPClient();
		if(ftpProxy.equals("") || ftpPort.equals("")){
			ftpClient.connect(ftp);
			ftpClient.login(username, password);
		}else{
			ftpClient.connect(ftpProxy, Integer.parseInt(ftpPort));
			ftpClient.login(username+"@"+ftp, password);
		}
		
		DateTime date = null;
		/*if(!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
			logger.warn("Uniprot FTP server refused connection.");
		}else{*/
			String mDate = ftpClient.getModificationTime(url.substring(url.lastIndexOf(ftp)+ftp.length(), url.length()));
			mDate = mDate.substring(mDate.indexOf(" "), mDate.length()).trim();
			
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMddHHmmss");
			date = fmt.parseDateTime(mDate);
		//}
		ftpClient.disconnect();
		
		
		return date;
	}
}
