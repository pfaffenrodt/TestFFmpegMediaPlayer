package de.pfaffenrodt.ffmpegtest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.io.IOException;

import wseemann.media.FFmpegMediaPlayer;


public class MainActivity extends Activity {
    private final static String TAG = MainActivity.class.getSimpleName();

    private final String STREAM_URL ="";
    private boolean isPreparing;
    private boolean isPrepared;
    private boolean stopAfterPrepared;
    private boolean waitUntilPrepared;
    private FFmpegMediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.playPauseButton).setOnClickListener(onClickListener);
        ((CheckBox)findViewById(R.id.waitUntilPreparedCheckBox)).setOnCheckedChangeListener(onCheckedChangeListener);
    }

    private void requireMediaPlayer(){
        if(null == mediaPlayer){
            initMediaPlayer();
        }
    }

    private void initMediaPlayer(){
        mediaPlayer = new FFmpegMediaPlayer();
        mediaPlayer.setOnPreparedListener(onPreparedListener);
        mediaPlayer.setOnErrorListener(onErrorListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG,"onClick");
            if(waitUntilPrepared && stopAfterPrepared){
                //ignore stop
                stopAfterPrepared=false;
                return;
            }
            if(isPrepared || isPreparing){
                stopStream();
            }else{
                startStream();
            }
        }
    };

    private void startStream(){
        Log.i(TAG,"startStream");
        requireMediaPlayer();
        try {
            mediaPlayer.setDataSource(STREAM_URL);
            mediaPlayer.prepareAsync();
            isPreparing =true;
            isPrepared=false;
            stopAfterPrepared=false;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopStream(){
        Log.i(TAG,"stopStream");
        if(waitUntilPrepared && !isPrepared){
            stopAfterPrepared =true;
        }else {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer=null;
            isPreparing =false;
            isPrepared=false;
            stopAfterPrepared=false;
        }
    }

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            waitUntilPrepared=isChecked;
        }
    };


    FFmpegMediaPlayer.OnPreparedListener onPreparedListener = new FFmpegMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(FFmpegMediaPlayer fFmpegMediaPlayer) {
            Log.i(TAG, "onPrepared");
            isPrepared=true;
            if(waitUntilPrepared && stopAfterPrepared){
                stopStream();
                return;
            }
            mediaPlayer.start();
        }
    };

    FFmpegMediaPlayer.OnErrorListener onErrorListener = new FFmpegMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(FFmpegMediaPlayer fFmpegMediaPlayer, int i, int i1) {
            Log.i(TAG,"onError"+i);
            mediaPlayer.release();
            return false;
        }
    };
}
