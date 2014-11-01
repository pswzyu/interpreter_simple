package com.example.Interpreter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class LoginConn {
	

	public boolean connect(Config config,String username,String password){
		String charset = "UTF-8";
        

        SendUtility multipart;
		try {
			multipart = new SendUtility(config.getloginUrl(), charset);
			//multipart.addHeaderField("User-Agent", "CodeJava");
	        //multipart.addHeaderField("Test-Header", "Header-Value");
	        multipart.addFormField("username", username);
	        multipart.addFormField("password", password);
	        List<String> response = multipart.finish();

	        for (String line : response) {
	            System.out.println(line);
	        }
	        String a = response.get(0);
	        if(a.startsWith("0"))
	        	return false;
	        else{
	        	String temp[] = a.split(",");
	        	config.setSelfId(temp[0]);
	        	/////write realname
	        	return true;
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

      return false;

		

	}
}
