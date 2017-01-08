package com.jcimadras.jcimadras.Volley;

import android.app.Application;
import android.content.Context;

public class MyVolleySingleton extends Application {
    private static MyVolleySingleton sInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static MyVolleySingleton getsInstance(){
        return sInstance;
    }

    public static Context getContext(){
        return sInstance.getApplicationContext();
    }
}
