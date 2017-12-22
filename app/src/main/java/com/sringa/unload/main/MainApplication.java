package com.sringa.unload.main;

import android.app.Application;

public class MainApplication extends Application {

    public static final String PRIMARY_CHANNEL = "default";

    @Override
    public void onCreate() {
        super.onCreate();
        System.setProperty("http.keepAliveDuration", String.valueOf(30 * 60 * 1000));
    }
}
