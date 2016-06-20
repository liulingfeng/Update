package com.llf.update;

import android.app.Application;

/**
 * Created by llf on 2016/6/20.
 */
public class MyApplication extends Application{
    public static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }
}
