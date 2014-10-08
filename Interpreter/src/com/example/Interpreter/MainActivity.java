package com.example.Interpreter;

import android.app.Activity;
import android.os.Bundle;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Config config = new Config();
        try
        {
            FileOutputStream fileOut =
                    new FileOutputStream("/data/local/config.cfg");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(config);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in /data/local/config.cfg");
        }catch(IOException i)
        {
            i.printStackTrace();
        }
    }
}
