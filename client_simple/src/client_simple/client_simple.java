package client_simple;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;

class client_simple{
	static String sendUrl = "http://home.cnzy.me:8001/interpreter/upload.php";
	static String receiveUrl = "http://home.cnzy.me:8001/interpreter/receive.php";
	static String  selfId;
	static String  targetId;
	Lock recordLock;

	static String sendFilePath = "voice/";
	static String sendFileName = "record.wav";
	static String receiveFilePath = "voice/";
	static String receiveFileName = "receive.wav";
	String recieveFileUrl;

	public static void main(String[] str){
		selfId = "1";
		targetId = "2";
		
		InputStream in = System.in;
        char c;
        try {
        	while(true){
	        	System.out.println("input 1 for send a file");
	    		System.out.println("input 2 for recieve a file");
	    		System.out.println("input 0 for exit");
	            while ((c = (char) in.read()) > 0) {
	            	if(c == '1'){
	            		System.out.println("sended");
	            		send();
	            	}
	            	else if(c == '2'){
	            		System.out.println("received");
	            		receive();
	            	}
	            	else if(c == '0'){
	            		return;
	            	}
	            }
        	}
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
	}
	
	private static void send(){	 
	        try {
				String charset = "UTF-8";
		        File uploadFile = new File(sendFilePath+sendFileName);
		        
	            SendUtility multipart = new SendUtility(sendUrl, charset);
	             
	            //multipart.addHeaderField("User-Agent", "CodeJava");
	            //multipart.addHeaderField("Test-Header", "Header-Value");
	            multipart.addFormField("target_id", targetId);
	            multipart.addFormField("self_id", selfId);
	            multipart.addFilePart("sound_file", uploadFile);
	 
	            List<String> response = multipart.finish();
	             
	            System.out.println("SERVER REPLIED:");
	    
	            for (String line : response) {
	                System.out.println(line);
	            }
	        } catch (IOException ex) {
	            System.err.println(ex);
	        }
		
	}

	private static void receive(){
		//always run
		//ask for voiceFile from server
		//store in recieveFile
		//play(String recieveFileUrl);
		ReceiveUtility re = new ReceiveUtility();
		if(re.revieve(receiveUrl,selfId,targetId,receiveFilePath,receiveFileName)==1){
			System.out.println("Received Success!");
		}
		else{
			System.out.println("No voice message found!");
		}
	}
}