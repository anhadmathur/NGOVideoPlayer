package com.anhad.ngovideoplayer.Utility;

import android.util.Log;

/**
 * Created by Anhad on 07-12-2016.
 */
public class Logger {
    public static void Log(String message){
        Log.d("Logs",message);
    }
    public static void Log(String tag, String message){
        Log.d(tag,message);
    }
}
