package mpg.biochem.de.interbase.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

import mpg.biochem.de.interbase.model.Service;
import mpg.biochem.de.interbase.model.ServiceHandler;

import org.apache.log4j.Logger;
import org.hupo.psi.mi.psicquic.wsclient.PsicquicSimpleClient;
import org.springframework.beans.factory.annotation.Value;

public class ServerDataFetcherTask implements Runnable {

	private String path;
	
	private int maxResults;
	private int maxAtempts;
	private int waiting;
	
	private Service service;
	private ServiceHandler serviceHandler;
	private CountDownLatch latch;
	
	@Value("psicquic.query")
	private String query;
	
	private static final Logger logger = Logger.getLogger(ServerDataFetcherTask.class);
	
	public ServerDataFetcherTask(Service service, ServiceHandler serviceHandler, CountDownLatch latch){
		this.service = service;
		this.serviceHandler = serviceHandler;
		this.latch = latch;
	}
	
	@Override
	public void run() {	
		logger.info("Fetching data form "+service.getName());
		try {
			OutputStream out = new FileOutputStream(new File(path+service.getName()+".tab"));
			PsicquicSimpleClient client = new PsicquicSimpleClient(service.getRestURL());
			
			int i=0, j=1;
			long count = service.getCount();
			boolean errors = false;
			while(i<count){
				
				InputStream response;
				try{
					response = client.getByQuery("*", PsicquicSimpleClient.MITAB25, i, maxResults);
				}catch(Exception e){
					response = null;
				}
				
				if (response == null) {
					
					logger.warn(service.getName()+" - Atempt "+j+" failed getting "+i+" out of "+count);
					if(j>maxAtempts){
						j = 1;
						i += maxResults;
	
						errors = true;
					}else{
						j++;
						Thread.sleep(waiting);
					}
				}else{
					
					logger.debug(service.getName()+" - Getting "+i+" out of "+count);
					
					int read = 0;
					byte[] bytes = new byte[1024];
				 
					while ((read = response.read(bytes)) != -1) {
						out.write(bytes, 0, read);
					}
					response.close();
					out.flush();
					
					i+=maxResults;
				}
			}
			out.close();
			
			if(!errors){
				service.setIndexed(true);
				service.setClustered(false);
				serviceHandler.add(service);
			}
		
			logger.info("Got data from "+service.getName());
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally{
				latch.countDown();
			}
			
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public int getMaxAtempts() {
		return maxAtempts;
	}

	public void setMaxAtempts(int maxAtempts) {
		this.maxAtempts = maxAtempts;
	}

	public int getWaiting() {
		return waiting;
	}

	public void setWaiting(int waiting) {
		this.waiting = waiting;
	}
	
}
