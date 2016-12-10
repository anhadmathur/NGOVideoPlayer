package com.anhad.ngovideoplayer.DataSource;

import android.annotation.TargetApi;
import android.media.MediaDataSource;
import android.os.Build;

import com.anhad.ngovideoplayer.Callbacks.PlayerListener;
import com.anhad.ngovideoplayer.Callbacks.VideoDownloadListener;
import com.anhad.ngovideoplayer.Utility.Logger;
import com.anhad.ngovideoplayer.preference.Preference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Anhad on 07-12-2016.
 */

@TargetApi(Build.VERSION_CODES.M)
public class VideoDataSource extends MediaDataSource implements VideoDownloadListener{

    ByteArrayOutputStream byteArrayOutputStream;
    private volatile byte[] videoBuffer;
    private Object syncObject = new Object();
    private PlayerListener playerListener;
    private boolean videoFinished = false;
    private long lastPositionToplay = -1;

    public VideoDataSource(PlayerListener playerListener) {
        this.playerListener = playerListener;
    }

    @Override
    public synchronized int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
        synchronized (syncObject){
            Logger.Log(getClass().getName() + " readAt position - " + position);
            int length = (int) Preference.getInstance().getFileLength();
            int bufferLength = videoBuffer.length;
            if (position >= length) {
                videoFinished = true;
                return -1; // -1 indicates EOF
            } else if (position < length && position > videoBuffer.length) {
                Logger.Log("Length of video buffer is : "+ bufferLength);
                playerListener.pausePlayer();
                lastPositionToplay = position;
                return -1;
                // ask media player to wait.
            } else if (position + size > bufferLength) {
                size -= (position + size) - bufferLength;
            }
            System.arraycopy(videoBuffer, (int)position, buffer, offset, size);
            return size;
        }
    }

    @Override
    public synchronized long getSize() throws IOException {
        Logger.Log(getClass().getName() + " File SIZE " + Preference.getInstance().getFileLength());
        return Preference.getInstance().getFileLength();
    }

    @Override
    public synchronized void close() throws IOException {

    }

    @Override
    public void updateVideoBytes(byte[] data) {
        try {
            synchronized (syncObject) {
                Logger.Log(getClass().getName() + " updating video bytes");
                if (byteArrayOutputStream == null) {
                    byteArrayOutputStream = new ByteArrayOutputStream();
                }
                if (videoBuffer != null) {
                    byteArrayOutputStream.write(videoBuffer);
                }
                byteArrayOutputStream.write(data);
                byteArrayOutputStream.flush();
                videoBuffer = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
                byteArrayOutputStream = null;
                playerListener.resumePlayer(lastPositionToplay);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}