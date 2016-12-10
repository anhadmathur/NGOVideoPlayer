package com.anhad.ngovideoplayer.async;

import android.os.AsyncTask;
import android.os.Environment;

import com.anhad.ngovideoplayer.Callbacks.VideoDownloadListener;
import com.anhad.ngovideoplayer.Utility.Logger;
import com.anhad.ngovideoplayer.preference.Preference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
/**
 * Created by Anhad on 07-12-2016.
 */
public class VideoDownloadingTask extends AsyncTask<String,byte[],Boolean> {

    private VideoDownloadListener listener;

    public VideoDownloadingTask(VideoDownloadListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) {

        try{
            if (params == null || params.length == 0) {
                return false;
            }

            File file = new File(Environment.getExternalStorageDirectory() + "/video.mp4");
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.mkdirs();
                }
                file.createNewFile();
            }
            Logger.Log("File Size : "+file.length());
            //For appending incoming bytes
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            if (file.length() > 0) {
                Logger.Log(getClass().getName() + " Playing from file");
                byte[] bytes = new byte[(int)file.length()];
                FileInputStream fileInputStream = new FileInputStream(file);
                fileInputStream.read(bytes);
                fileInputStream.close();
                listener.updateVideoBytes(bytes);
            } else {
                Logger.Log(getClass().getName() + " PLaying from url");
                final String videoUrl = params[0];
                URL url = new URL(videoUrl);
                //Open the stream for the file.
                URLConnection connection = url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();

                Logger.Log("Content Size : "+connection.getContentLength());
                Preference.getInstance().saveFileLength(connection.getContentLength());
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                int nRead;
                byte[] data = new byte[16384];

                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    byteArrayOutputStream.write(data, 0, nRead);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    publishProgress(byteArray);
                    fileOutputStream.write(byteArray);

                    byteArrayOutputStream.flush();
                    byteArrayOutputStream.reset();
                    byteArrayOutputStream.close();
                    byteArrayOutputStream = new ByteArrayOutputStream();
                }

                inputStream.close();
                fileOutputStream.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(byte[]... values) {
        super.onProgressUpdate(values);
        listener.updateVideoBytes(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }
}
