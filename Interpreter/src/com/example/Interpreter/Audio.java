package com.example.interpreter;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

public class Audio {
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private String mFileName;
    private String LOG_TAG;
    public Audio() {
        mFileName = Config.getSendFileName();
        LOG_TAG = Audio.class.getName();
    }

    // Record the audio through the MIC.
    public void startRecord() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }


    // Stop recording.
    public void stopRecord() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    // Play the audio.
    public void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    // Stop playing the audio.
    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

}
