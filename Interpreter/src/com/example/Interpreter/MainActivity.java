package com.example.Interpreter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.*;
import java.util.List;


public class MainActivity extends Activity {
    private final String SHAREDPREFERENCES_NAME = "Interpreter";
    private Config config;
    private Audio audio;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Estimate if it's the first time start this activity.
        SharedPreferences preferences = getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);
        boolean firstStart = preferences.getBoolean("firstStart", true);
        if (firstStart) {
            config = Config.getConfig();
            writeConfig(config);
        } else {
            config = readConfig();
        }

        // Initialize UI.
        final Button button = (Button) findViewById(R.id.button);
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        // Start recording.
                        audio = new Audio();
                        audio.startRecording();
                        button.setText(R.string.button_recording);
                        button.setBackgroundColor(getResources().getColor(R.color.button_recording));
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_OUTSIDE:
                        v.setPressed(false);
                        // Stop recording.
                        audio.stopRecording();
                        button.setText(R.string.button_waiting);
                        button.setBackgroundColor(getResources().getColor(R.color.button_waiting));
                        //audio.startPlaying();
                        
                        //send record to server
                        send();
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                }

                return true;
            }
        });


    }

    // Write the configuration to file.
    private void writeConfig(Config config) {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(config.getConfigFileName());
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(config);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in "+config.getConfigFileName());
        } catch(IOException i) {
            i.printStackTrace();
        }
    }

    // Read the configuration from file.
    private Config readConfig() {
        Config config = null;
        try {
            FileInputStream fileIn = new FileInputStream(config.getConfigFileName());
            ObjectInputStream in = new ObjectInputStream(fileIn);
            config = (Config) in.readObject();
            in.close();
            fileIn.close();
        } catch(IOException i) {
            i.printStackTrace();
        } catch(ClassNotFoundException c) {
            System.out.println("Config class not found");
            c.printStackTrace();
        }

        return config;
    }
    
    private void send(){	
    	new SendService().execute();  
    }
    
    private void receive(){
		//always run
		//ask for voiceFile from server
		//store in recieveFile
		//play(String recieveFileUrl);
    	
		ReceiveUtility re = new ReceiveUtility();
		if(re.revieve(config.getReceiveUrl(),config.getSelfId(),config.getFromId(),config.getReceiveFileName())==1){
			System.out.println("Received Success!");
		}
		else{
			System.out.println("No voice message found!");
		}
	}
    
    private class SendService extends AsyncTask<Void, Void, Void>  
    {  
    	protected Void doInBackground(Void... params)  
        {  
    		try {
    			String charset = "UTF-8";
    			//config = Config.getConfig();
    	        File uploadFile = new File(config.getSendFileName());
    	       
                SendUtility multipart = new SendUtility(config.getSendUrl(), charset);
          
                //multipart.addHeaderField("User-Agent", "CodeJava");
                //multipart.addHeaderField("Test-Header", "Header-Value");
                multipart.addFormField("target_id", config.getFromId());
                multipart.addFormField("self_id", config.getSelfId());
                multipart.addFilePart("sound_file", uploadFile);
     
                List<String> response = multipart.finish();
                 
                System.out.println("SERVER REPLIED:");
        
                for (String line : response) {
                    System.out.println(line);
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }
            return null;  
        }  
  
        protected void onPostExecute(Void result)  
        {  
        	
        }  
    }  
}
