package mpg.biochem.de.interbase.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

public class FTPDownloadFile {
	
	private FTPClient ftpClient;
	private String server;
	private Logger logger = Logger.getLogger(FTPDownloadFile.class);
    
    public FTPDownloadFile(String server){
    	this.server = server;
    	ftpClient = new FTPClient();
    }
    
    public void downloadFile(String ftpPath, String downloadPath) throws SocketException, IOException{
    	
    	boolean success = false;
    	FileOutputStream fos = null;
		try {
    		ftpClient.connect(server);
    		ftpClient.login("anonymous", "");
    		ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
    		ftpClient.enterLocalPassiveMode(); 
    		fos = new FileOutputStream(downloadPath);            
    		success = ftpClient.retrieveFile(ftpPath, fos);
    		
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		
		logger.info("File download "+success);
    	
    	/*ftpClient.connect(server);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
      
        File downloadFile = new File(downloadPath);
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
        boolean success = ftpClient.retrieveFile(ftpPath, outputStream);
        outputStream.close();
        logger.info("File download "+success);*/
    }
}

