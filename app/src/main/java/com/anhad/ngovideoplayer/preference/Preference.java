package com.anhad.ngovideoplayer.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.anhad.ngovideoplayer.NGOVApplication;

/**
 * Created by Anhad on 07-12-2016.
 */
public class Preference {

    private static Preference mInstance = new Preference();
    private static final String PREFERENCE_NAME = "ngo_preference";
    private static SharedPreferences preferences;

    // Keys
    private static final String FILE_LENGTH = "fileLENGTH";

    private Preference() {}

    public static void init(Context context) {
        preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static Preference getInstance() {

        if (preferences == null) {
            init(NGOVApplication.getInstance().getApplicationContext());
        }
        return mInstance;
    }

    public boolean saveFileLength(long length) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(FILE_LENGTH, length);
        return editor.commit();
    }

    public long getFileLength() {
        return preferences.getLong(FILE_LENGTH, 0);
    }
}
