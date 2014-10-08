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
	static String serverUrl = "http://home.cnzy.me:8001/interpreter/test.php";
	String  targetId;
	Lock recordLock;

	static String sendFileUrl = "voice/";
	static String sendFileName = "record.wav";
	String recieveFileUrl;

	public static void main(String[] str){
		send(sendFileUrl,sendFileName,"user2");
	}
	
	private static void send(String sendFileUrl,String sendFileName, String targetId){	 
	        try {
				String charset = "UTF-8";
		        File uploadFile = new File(sendFileUrl+sendFileName);
		        
	            MultipartUtility multipart = new MultipartUtility(serverUrl, charset);
	             
	            //multipart.addHeaderField("User-Agent", "CodeJava");
	            //multipart.addHeaderField("Test-Header", "Header-Value");
	             
	            //multipart.addFormField("description", "Cool Pictures");
	            //multipart.addFormField("keywords", "Java,upload,Spring");
	             
	            multipart.addFilePart("userfile", uploadFile);
	 
	            List<String> response = multipart.finish();
	             
	            System.out.println("SERVER REPLIED:");
	    
	            for (String line : response) {
	                System.out.println(line);
	            }
	        } catch (IOException ex) {
	            System.err.println(ex);
	        }
		
	}

	private void recieve(){
		//always run
		//ask for voiceFile from server
		//store in recieveFile
		//play(String recieveFileUrl);
	}
	 


}