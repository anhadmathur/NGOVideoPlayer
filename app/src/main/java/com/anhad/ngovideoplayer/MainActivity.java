package com.anhad.ngovideoplayer;

import android.annotation.TargetApi;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.anhad.ngovideoplayer.Callbacks.PlayerListener;
import com.anhad.ngovideoplayer.Callbacks.VideoDownloadListener;
import com.anhad.ngovideoplayer.DataSource.VideoDataSource;
import com.anhad.ngovideoplayer.Utility.Logger;
import com.anhad.ngovideoplayer.async.VideoDownloadingTask;

import java.io.IOException;
/**
 * Created by Anhad on 07-12-2016.
 */
public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, PlayerListener {

    MediaPlayer mp;
    boolean isPaused;
    boolean dataSourceSet = false;
    public static String VIDEO_URL = "https://socialcops.com/video/main.mp4";
    private VideoDataSource dataSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surface);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(this);
        mp = new MediaPlayer();
        isPaused = false;
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mp.setDisplay(holder);
            if(!isPaused) {
                dataSource = new VideoDataSource(this);
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        Logger.Log("MP Started");
                    }
                });

                new VideoDownloadingTask(dataSource).execute(VIDEO_URL);
            }else {
                mp.start();
            }
            isPaused = false;
        } catch (IllegalStateException is){
            is.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mp.isPlaying() && !isPaused) {
            mp.pause();
            isPaused = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mp != null) {
            mp.stop();
            mp.release();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void pausePlayer() {
        mp.pause();
    }

    @Override
    public void resumePlayer(long position) {
        if (!dataSourceSet) {
            mp.setDataSource(dataSource);
        }
        mp.prepareAsync();
    }
}