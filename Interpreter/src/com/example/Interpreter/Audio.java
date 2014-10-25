package com.example.Interpreter;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

public class Audio {
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private final String LOG_TAG = Audio.class.getName();
    private Config config = Config.getConfig();

    // Record the audio through the MIC.
    public void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(config.getSendFileName());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }


    // Stop recording.
    public void stopRecording() {
        try {
            mRecorder.stop();
        } catch (Exception e) {
            Log.v(LOG_TAG, "Nothing is recorded");
            mRecorder = null;
            return;
        }

        mRecorder.release();
        mRecorder = null;
    }

    // Play the audio.
    public void startPlaying(String fileName) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(fileName);
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
