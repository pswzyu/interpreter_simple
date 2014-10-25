package com.example.Interpreter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.*;

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
                        audio.startPlaying();
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
}
