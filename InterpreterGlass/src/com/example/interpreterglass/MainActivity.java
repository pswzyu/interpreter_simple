package com.example.interpreterglass;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

public class MainActivity extends Activity {

	private final String SHAREDPREFERENCES_NAME = "Interpreter";
	private Config config;

	HashMap<String, String> user_infos;
	ArrayList<String> usernames;
	ArrayList<String> passwords;
	int current_user = 0;

	TextView tv_SelectedUser;
	TextView tv_LoginResult;
	private GestureDetector mGestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Estimate if it's the first time to start this application.
		SharedPreferences preferences = getSharedPreferences(
				SHAREDPREFERENCES_NAME, MODE_PRIVATE);
		boolean firstStart = preferences.getBoolean("firstStart", true);
		if (firstStart) {
			config = Config.getConfig();
			writeConfig(config);
		} else {
			config = readConfig();
		}

		// Predefined users ////
		usernames = new ArrayList<String>();
		usernames.add("paul");
		usernames.add("pswzyu");
		usernames.add("xinyuyua");
		usernames.add("sunsetrider");
		passwords = new ArrayList<String>();
		passwords.add("123qwe");
		passwords.add("123qwe");
		passwords.add("123qwe");
		passwords.add("123qwe");
		user_infos = new HashMap<String, String>();
		user_infos.put("paul", "RealName: paul\nSpeaks English");
		user_infos.put("pswzyu", "RealName: zhangyu\nSpeaks English");
		user_infos.put("xinyuyua", "RealName: yuanxinyu\nSpeaks Chinese");
		user_infos.put("sunsetrider", "RealName: yongshengsong\nSpeaks Chinese");
		////////////////////////

		tv_SelectedUser = (TextView) findViewById(R.id.tvSelectedUser);
		tv_LoginResult = (TextView) findViewById(R.id.tvLoginResult);
		
		tv_SelectedUser.setText("Username:"
				+ usernames.get(current_user) + "\n" + "UserInfo:"
				+ user_infos.get(usernames.get(current_user)));
		mGestureDetector = createGestureDetector(this);
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
					// aftre tap, try to login
					new LoginAT().execute(usernames.get(current_user),passwords.get(current_user));
					return true;
				} else if (gesture == Gesture.TWO_TAP) {
					// do something on two finger tap
					return true;
				} else if (gesture == Gesture.SWIPE_RIGHT) {
					// do something on right (forward) swipe
					current_user++;
					if (current_user >= usernames.size())
						current_user = usernames.size() - 1;
					// display the user name and user info in the text view
					tv_SelectedUser.setText("Username:"
							+ usernames.get(current_user) + "\n" + "UserInfo:"
							+ user_infos.get(usernames.get(current_user)));

					return true;
				} else if (gesture == Gesture.SWIPE_LEFT) {
					// do something on left (backwards) swipe
					current_user--;
					if (current_user < 0)
						current_user = 0;
					// display the user name and user info in the text view
					tv_SelectedUser.setText("Username:"
							+ usernames.get(current_user) + "\n" + "UserInfo:"
							+ user_infos.get(usernames.get(current_user)));

					return true;
				}
				return false;
			}
		});
		gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
			@Override
			public void onFingerCountChanged(int previousCount, int currentCount) {
				// do something on finger count changes
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

	class LoginAT extends AsyncTask<String, Void, Void> {
		String real_name = "";
		String user_id = "";

		protected Void doInBackground(String... params) {
			// params[0] is username
			// params[1] is password
			try {
				// lock.lock();
				String charset = "UTF-8";
				// config = Config.getConfig();
				// File uploadFile = new File(config.getSendFileName());

				SendUtility multipart = new SendUtility(config.getloginUrl(),
						charset);
				// multipart.addHeaderField("User-Agent", "CodeJava");
				// multipart.addHeaderField("Test-Header", "Header-Value");
				multipart.addFormField("username", params[0]);
				multipart.addFormField("password", params[1]);

				List<String> response = multipart.finish();

				String all_response = "";
				for (String line : response) {
					all_response += line;
				}
				String[] splited = all_response.split(",");
				if (splited.length > 0)
				{
					user_id = splited[0];
					
					if (!user_id.equals("0"))
					{
						real_name = splited[1];
						config.setSelfLanguage(splited[2]);
					}
				}
				
				// parse the server response
				// lock.unlock();
			} catch (IOException ex) {
				System.err.println(ex);
			}
			return null;

		}

		protected void onPostExecute(Void result) {
			// start translate activity using the
			if (user_id.equals("0") || user_id.equals(""))
			{
				// notify user
				tv_LoginResult.setText("Login Failed");
				return;
			}
			// set the id in config object
			config.setSelfId(user_id);
			config.setSelfName(usernames.get(current_user));
			//startActivity();
			Intent intent = new Intent(MainActivity.this, TranslateActivity.class);
			startActivity(intent);
		}

	}

	// Write the configuration to file.
	private void writeConfig(Config config) {
		try {
			FileOutputStream fileOut = new FileOutputStream(
					config.getConfigFileName());
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(config);
			out.close();
			fileOut.close();
			System.out.printf("Serialized data is saved in "
					+ config.getConfigFileName());
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	// Read the configuration from file.
	private Config readConfig() {
		Config config = null;
		try {
			FileInputStream fileIn = new FileInputStream(
					config.getConfigFileName());
			ObjectInputStream in = new ObjectInputStream(fileIn);
			config = (Config) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (ClassNotFoundException c) {
			System.out.println("Config class not found");
			c.printStackTrace();
		}

		return config;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		writeConfig(config);
		super.onDestroy();
	}
	
}
