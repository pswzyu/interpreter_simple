package com.example.Interpreter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.*;
import java.util.List;
import java.util.concurrent.locks.Lock;


public class MainActivity extends Activity {
	
    private final String SHAREDPREFERENCES_NAME = "Interpreter";
    private final int CAMERA_REQUEST = 1;
    private final String LOG_TAG = Audio.class.getName();
    private Config config;
    private Audio audio;

    private  EditText targetEdit ;
    private  EditText selfEdit ;
    private  Button btnRecord ;
    private  Button btnSend ;
    private  Button btnBack ;
    private  Button btnAddTarget ;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Estimate if it's the first time to start this application.
        SharedPreferences preferences = getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);
        boolean firstStart = preferences.getBoolean("firstStart", true);
        if (firstStart) {
            config = Config.getConfig();
            writeConfig(config);
            receiveopen();
        } else {
            config = readConfig();
        }

        // Initialize UI.
          targetEdit = (EditText) findViewById(R.id.targetEdit);
          selfEdit = (EditText) findViewById(R.id.selfEdit);
          btnRecord = (Button) findViewById(R.id.button_record);
          btnSend = (Button) findViewById(R.id.button_send);
          btnBack = (Button) findViewById(R.id.button_back_to_record);
          btnAddTarget = (Button) findViewById(R.id.button_add_target);
        btnRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        // Start recording.
                        audio = new Audio();
                        audio.startRecording();
                        btnRecord.setText(R.string.button_recording);
                        btnRecord.setBackgroundColor(getResources().getColor(R.color.button_recording));
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_OUTSIDE:
                        v.setPressed(false);
                        // Stop recording.
                        audio.stopRecording();
                        btnRecord.setText(R.string.button_waiting);
                        btnRecord.setBackgroundColor(getResources().getColor(R.color.button_waiting));
                        //audio.startPlaying(config.getSendFileName());

                        btnRecord.setVisibility(View.INVISIBLE);
                        btnBack.setVisibility(View.VISIBLE);
                        btnSend.setVisibility(View.VISIBLE);
                        btnRecord.setText(R.string.button_waiting);
                        btnRecord.setBackgroundColor(getResources().getColor(R.color.button_waiting));
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

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                config.setTargetId(targetEdit.getText().toString());
                config.setSelfId(selfEdit.getText().toString());
                send(); // Send the recorded file.
                btnSend.setVisibility(View.INVISIBLE);
                btnBack.setVisibility(View.INVISIBLE);
                btnRecord.setVisibility(View.VISIBLE);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSend.setVisibility(View.INVISIBLE);
                btnBack.setVisibility(View.INVISIBLE);
                btnRecord.setVisibility(View.VISIBLE);
            }
        });

        btnAddTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(config.getTargetPhotoFileName())));
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            // Send the target photo and get the target ID.
            sendPic();
        }
    }
    @Override
    protected void onDestroy() {
        writeConfig(config);
        super.onDestroy();
    }
    
    private void send(){
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            new SendService().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new SendService().execute();
        }
    	//new SendService().execute();
    }

    private void sendPic(){
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            new sendPicService().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new sendPicService().execute();
        }

    }

    private void receiveopen(){
		//always run
		//ask for voiceFile from server
		//store in recieveFile
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            new receiveService().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new receiveService().execute();
        }
    	//new receiveService().execute();
	}
    
    private class SendService extends AsyncTask<Void, Void, Void>  
    {  
    	protected Void doInBackground(Void... params)  
        {  
    		try {
    			//lock.lock();
    			String charset = "UTF-8";
    			//config = Config.getConfig();
    	        File uploadFile = new File(config.getSendFileName());
    	       
                SendUtility multipart = new SendUtility(config.getSendUrl(), charset);
          
                //multipart.addHeaderField("User-Agent", "CodeJava");
                //multipart.addHeaderField("Test-Header", "Header-Value");
                multipart.addFormField("target_id", config.getTargetId());
                multipart.addFormField("self_id", config.getSelfId());
                multipart.addFilePart("sound_file", uploadFile);
     
                List<String> response = multipart.finish();
                 
                System.out.println("SERVER REPLIED:");
        
                for (String line : response) {
                    System.out.println(line);
                }
                //lock.unlock();
            } catch (IOException ex) {
                System.err.println(ex);
            }
            return null;  
        }  
    	protected void onPostExecute(Void result)  
        {  
     
        } 
    }
    
    private class receiveService extends AsyncTask<Void, Void, Void>  
    {  
    	protected Void doInBackground(Void... params)  
        {  
    		ReceiveUtility re = new ReceiveUtility();
    		while(true){
    			//lock.lock();
    			int count = re.revieve(config.getReceiveUrl(),config.getTargetId(),config.getSelfId(),config.getReceiveFileName());	//for test only!!
    			//int count = re.revieve(config.getReceiveUrl(),config.getSelfId(),config.getTargetId(),config.getReceiveFileName());
    			if(count!=-1){    
        			System.out.println("Received Success!");
        			audio.startPlaying(config.getReceiveFileName()+String.valueOf(count));
        		}
        		else{
        			//System.out.println("No voice message found!");
        		}
        		try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		//lock.unlock();
    		}  
        }  
    	protected void onPostExecute(Void result)  
        {  
     
        } 

    }

    private class sendPicService extends AsyncTask<Void, Void, String>
    {

        protected String doInBackground(Void... params)
        {
            try {
                String charset = "UTF-8";
                File uploadFile = new File(config.getTargetPhotoFileName());

                SendUtility multipart = new SendUtility(config.getSendPicUrl(), charset);

                //multipart.addHeaderField("User-Agent", "CodeJava");
                //multipart.addHeaderField("Test-Header", "Header-Value");
                multipart.addFormField("action", "recognition");
                multipart.addFormField("self_id", config.getSelfId());
                multipart.addFilePart("pic_file", uploadFile);

                List<String> response = multipart.finish();

                System.out.println("SERVER REPLIED:");


                for (String line : response) {
                    System.out.println(line);
                }
                return response.get(0);
            } catch (IOException ex) {
                System.err.println(ex);
            }
            return null;
        }
        protected void onPostExecute(String result)
        {
            if (result == null || result.equals("NULL"))
            {
                // alert the user that the pic is not valid
            }else{
                // set the targetid
                targetEdit.setText(result);

            }
        }

    }
}
