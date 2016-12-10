package com.anhad.ngovideoplayer;

import android.app.Application;

import com.anhad.ngovideoplayer.preference.Preference;
/**
 * Created by Anhad on 07-12-2016.
 */
public class NGOVApplication extends Application{

    private static NGOVApplication application;

    public static NGOVApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        Preference.init(this);
    }
}
