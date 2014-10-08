package client_simple;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ReceiveUtility {

	public int revieve(String url,String selfId,String targetId,String filePath,String fileName){
		try {
			String path = url+"?self_id="+selfId+"&from_id="+targetId;
			System.out.println(path);
			//StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
			HttpURLConnection conn = (HttpURLConnection)new URL(path).openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			if(conn.getResponseCode()==200){
				InputStream inStream = (InputStream) conn.getInputStream();  
		        FileOutputStream fs = new FileOutputStream(new File(filePath+fileName));  
		        byte[] buffer = new byte[1204];  
		        int byteread = 0;
		        int bytesum = 0;
		        while ((byteread = inStream.read(buffer)) != -1) {  
		            bytesum += byteread;  
		            fs.write(buffer, 0, byteread);  
		        } 
		        if(bytesum == 0){
		        	return 0;
		        }
		        else
		        	return 1;
			}	
		}catch (Exception ex) {
            System.err.println(ex);
        }
		return 0;
	}
}
