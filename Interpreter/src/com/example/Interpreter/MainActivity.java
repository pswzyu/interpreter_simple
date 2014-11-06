package com.example.Interpreter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.Lock;

import com.iflytek.speech.ErrorCode;
import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.RecognizerListener;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechRecognizer;
import com.iflytek.speech.SpeechSynthesizer;
import com.iflytek.speech.SynthesizerListener;


public class MainActivity extends Activity {
	

    private final int CAMERA_REQUEST = 1;
    private final String LOG_TAG = "Interpreter.MainActivity";
    private Config config;
    //private Audio audio;

    private  Button btnRecord ;
    private  Button btnSend ;
    private  Button btnBack ;
    private  Button btnAddTarget ;
    
    receiveService m_receiveService;
    
    private SpeechRecognizer mIat;
    private SpeechSynthesizer mTts;
    private TextToSpeech android_tts;
    
    private String iat_result;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //audio = new Audio();
        config = Config.getConfig();
		
		mIat = new SpeechRecognizer(this, mInitListener);
		if (config.getSelfLanguage().equals("zh_CN"))
		{
			mTts = new SpeechSynthesizer(this, mTtsInitListener);
	        setTTSParam();
		}else{
			android_tts = new TextToSpeech(this, androidTTSInitListener);
		}
        setIATParam();
        
        // Initialize UI.
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
                        //audio.startRecording();
                        mIat.startListening(mRecognizerListener);
                        btnRecord.setText(R.string.button_recording);
                        btnRecord.setBackgroundColor(getResources().getColor(R.color.button_recording));
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_OUTSIDE:
                        v.setPressed(false);
                        // Stop recording.
                        //audio.stopRecording();
                        mIat.stopListening(mRecognizerListener);
                        btnRecord.setText(R.string.button_waiting);
                        btnRecord.setBackgroundColor(getResources().getColor(R.color.button_waiting));
                        //audio.startPlaying(config.getSendFileName());

                        btnRecord.setVisibility(View.INVISIBLE);
                        btnBack.setVisibility(View.VISIBLE);
                        //btnSend.setVisibility(View.VISIBLE);
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
                if (config.getTargetId().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please add target!", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

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

        if (!config.getTargetId().equals("")) {
            btnAddTarget.setText(config.getTargetRealName()+" (Change Target)");
        }
        btnAddTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(config.getTargetPhotoFileName())));
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }
    
    // this variable is the listerner for xunfei init
    private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(ISpeechModule module, int code) {
			Log.d(LOG_TAG, "SpeechRecognizer init() code = " + code);
		}
    };
    
    private RecognizerListener mRecognizerListener = new RecognizerListener.Stub() {
        
        @Override
        public void onVolumeChanged(int v) throws RemoteException {
        	Log.d(LOG_TAG, "onVolumeChanged："	+ v);
        }
        
        @Override
        public void onResult(final RecognizerResult result, boolean isLast)
                throws RemoteException {
        	runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (null != result) {
		            	// 显示
						String iattext = JsonParser.parseIatResult(result.getResultString());
						Log.d(LOG_TAG, "recognizer result：" + iattext);
						iat_result = iattext;
						btnSend.setVisibility(View.VISIBLE);
		            } else {
		                Log.d(LOG_TAG, "recognizer result : null");
		            }
				}
			});
            
        }
        @Override
        public void onError(int errorCode) throws RemoteException {
			Log.d(LOG_TAG, "onError Code："	+ errorCode);
        }
        @Override
        public void onEndOfSpeech() throws RemoteException {
        	Log.d(LOG_TAG, "onEndOfSpeech");
        }
        @Override
        public void onBeginOfSpeech() throws RemoteException {
        	Log.d(LOG_TAG, "onBeginOfSpeech");
        }
    };
    
    private InitListener mTtsInitListener = new InitListener() {

		@Override
		public void onInit(ISpeechModule arg0, int code) {
			Log.d(LOG_TAG, "InitListener init() code = " + code);
		}
    };

    private SynthesizerListener mTtsListener = new SynthesizerListener.Stub() {
        @Override
        public void onBufferProgress(int progress) throws RemoteException {
        	 Log.d(LOG_TAG, "onBufferProgress :" + progress);
//        	 showTip("onBufferProgress :" + progress);
        }

        @Override
        public void onCompleted(int code) throws RemoteException {
        	Log.d(LOG_TAG, "onCompleted code =" + code);
        }

        @Override
        public void onSpeakBegin() throws RemoteException {
            Log.d(LOG_TAG, "onSpeakBegin");
        }

        @Override
        public void onSpeakPaused() throws RemoteException {
        	 Log.d(LOG_TAG, "onSpeakPaused.");
        }

        @Override
        public void onSpeakProgress(int progress) throws RemoteException {
        	Log.d(LOG_TAG, "onSpeakProgress :" + progress);
        }

        @Override
        public void onSpeakResumed() throws RemoteException {
        	Log.d(LOG_TAG, "onSpeakResumed.");
        }
    };
    
    private OnInitListener androidTTSInitListener = new OnInitListener(){

		@Override
		public void onInit(int status) {
			// TODO Auto-generated method stub
			if (status == TextToSpeech.SUCCESS) {
				android_tts.setLanguage(Locale.ENGLISH);
			}
		}
    	
    };
    
    // this function is used to set xunfei parameters
	public void setIATParam(){
			
		mIat.setParameter(SpeechConstant.LANGUAGE, config.getSelfLanguage().toLowerCase());
		mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
//		mIat.setParameter(SpeechConstant.ACCENT, mSharedPreferences.getString("accent_preference", "mandarin"));
//		mIat.setParameter(SpeechConstant.DOMAIN, mSharedPreferences.getString("domain_perference", "iat"));
		mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
		
		String param = null;
		param = "asr_ptt=1";
		mIat.setParameter(SpeechConstant.PARAMS, param+",asr_audio_path=/sdcard/iflytek/wavaudio.pcm");

	}
	private void setTTSParam(){
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, "local");
		mTts.setParameter(SpeechSynthesizer.VOICE_NAME, "xiaoyan");
		mTts.setParameter(SpeechSynthesizer.SPEED, "50");		
		mTts.setParameter(SpeechSynthesizer.PITCH, "50");		
		mTts.setParameter(SpeechSynthesizer.VOLUME, "50");
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
		m_receiveService.cancel(false);
		super.onPause();
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            // resize the image before sending it
            double scale = Config.target_photo_scale_factor;
            Bitmap photo = BitmapFactory.decodeFile(config.getTargetPhotoFileName());
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

            // delete the file after sending is done in async task class
        }
    }
    @Override
    protected void onDestroy() {
        writeConfig(config);
        mIat.cancel(mRecognizerListener);
        mIat.destory();
        if (mTts != null)
        {
        	mTts.stopSpeaking(mTtsListener);
            mTts.destory();
        }
        if (android_tts != null)
        {
        	android_tts.stop();
        	android_tts.shutdown();
        }
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
    
    private class SendService extends AsyncTask<Void, Void, Void>  
    {  
    	protected Void doInBackground(Void... params)  
        {  
    		try {
    			//lock.lock();
    			String charset = "UTF-8";
    			//config = Config.getConfig();
    	        //File uploadFile = new File(config.getSendFileName());
    	       
                SendUtility multipart = new SendUtility(config.getSendUrl(), charset);
          
                //multipart.addHeaderField("User-Agent", "CodeJava");
                //multipart.addHeaderField("Test-Header", "Header-Value");
                multipart.addFormField("target_id", config.getTargetId());
                multipart.addFormField("self_id", config.getSelfId());
                multipart.addFormField("content", iat_result);
                //multipart.addFilePart("sound_file", uploadFile);
     
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
    		while(!isCancelled()){
    			//lock.lock();
    			//int count = re.revieve(config.getReceiveUrl(),config.getTargetId(),config.getSelfId(),config.getReceiveFileName());	//for test only!!
    			//int count = re.revieve(config.getReceiveUrl(),config.getSelfId(),config.getTargetId(),config.getReceiveFileName());
    			String received = re.receiveString(config.getReceiveUrl(),config.getSelfId(),config.getTargetId());
    			if(!received.equals("")){    
        			System.out.println("Received Success!"+received);
        			//audio.startPlaying(config.getReceiveFileName()+String.valueOf(count));
        			int error_code = 0;
        			if (config.getSelfLanguage().equals("zh_CN"))
        			{
        				error_code = mTts.startSpeaking(received, mTtsListener);
        			}else{
        				error_code = android_tts.speak(received, TextToSpeech.QUEUE_ADD, null);
        			}
					Log.d(LOG_TAG, "start speak error : " + error_code);
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
                Log.i("time","back");
                List<String> response = multipart.finish();
                try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                System.out.println("SERVER REPLIED:");
                Log.i("time","back");
                for (String line : response) {
                    System.out.println(line);
                }
                String result = response.get(0);
                return result;
            } catch (IOException ex) {
                System.err.println(ex);
            }
            return null;
        }
        protected void onPostExecute(String result)
        {
        	if (result == null || result.equals("NULL") )
            {
                // alert the user that the pic is not valid
                Toast toast = Toast.makeText(getApplicationContext(), "Not a valid target!", Toast.LENGTH_SHORT);
                toast.show();
            }else{
            	
                // set the targetid
            	String temp[] = result.split(",");
	        	/////write realname
            	config.setTargetId(temp[0]);
                config.setTargetRealName(temp[1]);
				// delete the tmp files after finish
                File photo_fullsize = new File(config.getTargetPhotoFileName());
                File photo_scaled = new File(config.getScaledTargetPhotoFileName());
                photo_fullsize.delete();
                photo_scaled.delete();
            	btnAddTarget.setText(config.getTargetRealName());
            }
//        	while(config.getTargetId().equals("")){
//        		try {
//        			Log.i("time","front");
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//        	}

        }

    }
}
