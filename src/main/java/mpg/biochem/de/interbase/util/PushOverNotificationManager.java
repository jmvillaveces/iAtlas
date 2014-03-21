package mpg.biochem.de.interbase.util;

import java.io.DataOutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class PushOverNotificationManager {

	private static final String SERVER = "https://api.pushover.net/1/messages.json";
	private static final String USER_AGENT = "Mozilla/5.0";
	private static final String TOKEN = "awDwumfqMZNQoGSS8WNUcSqg397zow";
	private static final String USER = "uU2dPrsc9hRNVYQEc7ABdQwco88r41";
	
	public static void sendNotification(String msg) throws Exception {
		sendNotification(null, msg, -1);
	}
	
	public static void sendNotification(String title, String msg) throws Exception {
		sendNotification(title, msg, -1);
	}

	public static void sendNotification(String title, String msg, int priority) throws Exception {
		URL obj = new URL(SERVER);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
 
		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
 
		String urlParameters = "token="+TOKEN;
		urlParameters += "&user="+USER;
		urlParameters += "&message="+msg;
		urlParameters += "&priority="+priority;
		
		if(title != null)
			urlParameters += "&title="+title;
 
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
 
		con.getResponseCode();
	}
	
}
