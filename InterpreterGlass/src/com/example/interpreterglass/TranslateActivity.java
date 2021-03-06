package com.example.interpreterglass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;

public class TranslateActivity extends Activity {

	private final int CAMERA_REQUEST = 1;
	private static final String LOGTAG = "TranslateActivity";
	Config config;
	private Audio audio;
	private GestureDetector mGestureDetector;
	
	TextView tvTargetRealName;
	TextView tvRecording;
	
	receiveService m_receiveService;
	
	long record_started_time = 0;
	long last_swipe_down_time = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_translate);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		config = Config.getConfig();
		audio = new Audio();
		
		mGestureDetector = createGestureDetector(this);
		
		tvTargetRealName = (TextView) findViewById(R.id.tvTargetRealName);
		tvRecording = (TextView) findViewById(R.id.tvRecording);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		//always run
		//ask for voiceFile from server
		//store in recieveFile
		m_receiveService = new receiveService();
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	m_receiveService.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	    } else {
	    	m_receiveService.execute();
	    }
		
		super.onResume();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		// stop the while loop in receive thread, but let it finish
		m_receiveService.cancel(false);
		super.onPause();
	}
	
	@Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }
	
	private GestureDetector createGestureDetector(final Context context) {
		GestureDetector gestureDetector = new GestureDetector(context);
		// Create a base listener for generic gestures
		gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
			@Override
			public boolean onGesture(Gesture gesture) {
				if (gesture == Gesture.TAP) {
					// do something on tap
					return true;
				} else if (gesture == Gesture.TWO_TAP) {
					// do something on two finger tap
					Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	                //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(config.getTargetPhotoFileName())));
	                startActivityForResult(cameraIntent, CAMERA_REQUEST);

					return true;
				} else if (gesture == Gesture.SWIPE_RIGHT) {
					// do something on right (forward) swipe

					return true;
				} else if (gesture == Gesture.SWIPE_LEFT) {
					// do something on left (backwards) swipe

					return true;
				} else if (gesture == Gesture.SWIPE_DOWN) {
					// do something on left (backwards) swipe
					long time_now = System.currentTimeMillis();
					if ( time_now - last_swipe_down_time > 1500)
					{
						last_swipe_down_time = time_now;
						return true;
					}
				}
				return false;
			}
		});
		gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
			@Override
			public void onFingerCountChanged(int previousCount, int currentCount) {
				// do something on finger count changes
				//System.out.println("Pre:"+previousCount+", Now:"+currentCount);
				if (previousCount == 0 && currentCount == 1) // start recording
				{
					if (config.getTargetId().equals(""))
					{
						tvRecording.setText("Please take a picture!");
					}else{
						record_started_time = System.currentTimeMillis();
						tvRecording.setText("Recording...");
						tvRecording.setBackgroundColor(0xFF00FF00);
						audio.startRecording();
					}
				}
				if (previousCount == 1 && currentCount == 0)
				{
					audio.stopRecording();
					long time_now = System.currentTimeMillis();
					if (time_now - record_started_time < 1500)
					{
						tvRecording.setText("Speech is too short!");
					}else if (config.getTargetId().equals(""))
					{
						tvRecording.setText("Please take a picture!");
					}else{
						tvRecording.setText("Sending...");
						send(); // Send the recorded file.
					}
					tvRecording.setBackgroundColor(0xFFFFFFFF);
				}
			}
		});
		gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
			@Override
			public boolean onScroll(float displacement, float delta,
					float velocity) {
				// do something on scrolling
				return false;
			}
		});
		return gestureDetector;
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
        	
        	String picturePath = data.getStringExtra("picture_file_path");
        	//picture_file_path;
        	tvTargetRealName.setText("Processing image...");
            processPictureWhenReady(picturePath);
        	//dumpIntent(data);
            
        }
    }
	public static void dumpIntent(Intent i){

		String LOG_TAG = "INTENTDUMP";
	    Bundle bundle = i.getExtras();
	    if (bundle != null) {
	        Set<String> keys = bundle.keySet();
	        Iterator<String> it = keys.iterator();
	        Log.e(LOG_TAG,"Dumping Intent start");
	        while (it.hasNext()) {
	            String key = it.next();
	            Log.e(LOG_TAG,"[" + key + "=" + bundle.get(key)+"]");
	        }
	        Log.e(LOG_TAG,"Dumping Intent end");
	    }
	}
	
	private void processPictureWhenReady(final String picturePath) {
	    final File pictureFile = new File(picturePath);

	    if (pictureFile.exists()) {
	        // The picture is ready; process it.
	    	
	    	// resize the image before sending it
            double scale = Config.target_photo_scale_factor;
            Bitmap photo = BitmapFactory.decodeFile(picturePath);
            int new_width = (int)(photo.getWidth()*scale);
            int new_height = (int)(photo.getHeight()*scale);
            Bitmap scaled_photo = Bitmap.createScaledBitmap(photo, new_width, new_height, false);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            scaled_photo.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

            File f = new File(config.getScaledTargetPhotoFileName());
            try {
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Send the target photo and get the target ID.
            sendPic();
            // delete the original pic file
            pictureFile.delete();
            // delete the scaled file after sending is done in async task class
	    	
	    } else {
	        // The file does not exist yet. Before starting the file observer, you
	        // can update your UI to let the user know that the application is
	        // waiting for the picture (for example, by displaying the thumbnail
	        // image and a progress indicator).

	        final File parentDirectory = pictureFile.getParentFile();
	        FileObserver observer = new FileObserver(parentDirectory.getPath(),
	                FileObserver.CLOSE_WRITE | FileObserver.MOVED_TO) {
	            // Protect against additional pending events after CLOSE_WRITE
	            // or MOVED_TO is handled.
	            private boolean isFileWritten;

	            @Override
	            public void onEvent(int event, String path) {
	                if (!isFileWritten) {
	                    // For safety, make sure that the file that was created in
	                    // the directory is actually the one that we're expecting.
	                    File affectedFile = new File(parentDirectory, path);
	                    isFileWritten = affectedFile.equals(pictureFile);

	                    if (isFileWritten) {
	                        stopWatching();

	                        // Now that the file is ready, recursively call
	                        // processPictureWhenReady again (on the UI thread).
	                        runOnUiThread(new Runnable() {
	                            @Override
	                            public void run() {
	                                processPictureWhenReady(picturePath);
	                            }
	                        });
	                    }
	                }
	            }
	        };
	        observer.startWatching();
	    }
	}
	
	
	private void send(){
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            new SendService().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new SendService().execute();
        }
    }

    private void sendPic(){
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            new sendPicService().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new sendPicService().execute();
        }

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
                
                String server_reply = "";
                for (String line : response) {
                    server_reply += line;
                }
                Log.d(LOGTAG, "Send voice reply:"+server_reply);
                
                //lock.unlock();
            } catch (IOException ex) {
                System.err.println(ex);
            }
            return null;  
        }  
    	protected void onPostExecute(Void result)  
        {  
    		tvRecording.setText("Sent...");
        } 
    }
    
    private class receiveService extends AsyncTask<Void, Void, Void>  
    {
    	protected Void doInBackground(Void... params)  
        {  
    		ReceiveUtility re = new ReceiveUtility();
    		while(!isCancelled()){
    			//lock.lock();
    			//int count = re.revieve(config.getReceiveUrl(),config.getTargetId(),config.getSelfId(),config.getReceiveFileName());	//for test only!!
    			int count = re.receive(config.getReceiveUrl(),config.getSelfId(),config.getTargetId(),config.getReceiveFileName());
    			if(count!=-1){    
        			Log.d(LOGTAG, "Bytes Received"+String.valueOf(count));
        			audio.startPlaying(config.getReceiveFileName()+String.valueOf(count));
        		}
        		else{
        			//System.out.println("No voice message found!");
        		}
        		try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		//lock.unlock();
    		}
    		return null;
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
                File uploadFile = new File(config.getScaledTargetPhotoFileName());

                SendUtility multipart = new SendUtility(config.getSendPicUrl(), charset);

                //multipart.addHeaderField("User-Agent", "CodeJava");
                //multipart.addHeaderField("Test-Header", "Header-Value");
                multipart.addFormField("action", "recognition");
                
                multipart.addFormField("self_id", config.getSelfId());
                multipart.addFilePart("pic_file", uploadFile);

                List<String> response = multipart.finish();

                String server_reply = "";
                for (String line : response) {
                    server_reply += line;
                }
                Log.d(LOGTAG, "Send pic reply:"+server_reply);
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
            	tvTargetRealName.setText("Recognition Failed, please try again!");
            }else{
            	// set the targetid
            	String temp[] = result.split(",");
	        	/////write realname
            	config.setTargetId(temp[0]);
                config.setTargetRealName(temp[1]);
                // set the targetid
            	tvTargetRealName.setText(result);
				// delete the tmp files after finish
                File photo_scaled = new File(config.getScaledTargetPhotoFileName());
                photo_scaled.delete();
            }
        }

    }
}
