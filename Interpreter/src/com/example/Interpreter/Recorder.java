package com.example.Interpreter;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

public class Recorder {
    private MediaRecorder mRecorder;
    private String mFileName;
    private String LOG_TAG;
    public Recorder() {
        mRecorder = new MediaRecorder();
        mFileName = Config.getSendFileName();
        LOG_TAG = Recorder.class.getName();
    }

    // Record the audio through the MIC
    void record() {
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }


}
