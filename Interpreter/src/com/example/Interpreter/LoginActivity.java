package com.example.Interpreter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.*;
import java.util.List;



public class LoginActivity extends Activity  {
    private final String SHAREDPREFERENCES_NAME = "Interpreter";
    private Config config;
    private Button btnLogin;
    private EditText editUsername;
    private EditText editPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        config = Config.getConfig();
        // Estimate if it's the first time to start this application.
        SharedPreferences preferences = getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);
        boolean firstStart = preferences.getBoolean("firstStart", true);
        if (firstStart) {
            // Create the root directory.
            File root_path = new File(config.getFilePath());
            if (!root_path.exists())
            {
                root_path.mkdir();
            }

            //writeConfig(config);
            SharedPreferences.Editor prefEditor = preferences.edit();
            prefEditor.putBoolean("firstStart", false);
            prefEditor.commit();
        } else {
            //config = readConfig();
        }

        btnLogin = (Button) findViewById(R.id.button_login);
        editUsername = (EditText) findViewById(R.id.edit_username);
        editPassword = (EditText) findViewById(R.id.edit_password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send the username and password.
                new Login().execute();
            }
        });

        // Install the speech service if it's not.
        if (!speechServiceIsInstalled()) {
            ApkInstaller.installFromAssets(LoginActivity.this, getResources().getString(R.string.speech_service_name));
        }
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

    private class Login extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            LoginConn loginConn = new LoginConn();
            String username = editUsername.getText().toString();
            String password = editPassword.getText().toString();

            if(loginConn.connect(config,username, password)){
                System.out.println("login success!");
                Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(loginIntent);
            }
            else{
                // login fail
                System.out.println("login fail!");
                // write something to handle this
            }

            return null;
        }
    }

    // Test if the speech service is installed.
    private boolean speechServiceIsInstalled(){
        String packageName = "com.iflytek.speechcloud";
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for(int i = 0; i < packages.size(); i++){
            PackageInfo packageInfo = packages.get(i);
            if(packageInfo.packageName.equals(packageName)){
                return true;
                }else{
                continue;
                }
            }
        return false;
    }
}
