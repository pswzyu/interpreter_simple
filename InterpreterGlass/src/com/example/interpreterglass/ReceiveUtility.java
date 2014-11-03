package com.example.interpreterglass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;


public class ReceiveUtility {

	private static final String LOGTAG = "ReceiveUtility";
	private FileOutputStream fs;
	private int count = 0;
	
	public int receive(String url,String selfId,String targetId,String fileName){
		try {
			String path = url+"?self_id="+selfId+"&from_id="+targetId;
			Log.d(LOGTAG, "Receive Url: "+path);
			//StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
			HttpURLConnection conn = (HttpURLConnection)new URL(path).openConnection();
			conn.setConnectTimeout(20000);
			conn.setRequestMethod("GET");
			if(conn.getResponseCode()==200){
				InputStream inStream = (InputStream) conn.getInputStream();  
		        fs = new FileOutputStream(new File(fileName+String.valueOf(count)));  
		        byte[] buffer = new byte[1204];  
		        int byteread = 0;
		        int bytesum = 0;
		        while ((byteread = inStream.read(buffer)) != -1) {  
		            bytesum += byteread;  
		            //System.out.println(buffer);
		            fs.write(buffer, 0, byteread);  
		        } 
		        if(bytesum == 0){
		        	return -1;
		        }
		        else{
		        	//successful receive a new file 
		        	count++;
		        	return count-1;
		        }
			}	
		}catch (Exception ex) {
            System.err.println(ex);
        }
		return -1;
	}
}
