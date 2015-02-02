package com.oleg.volleystat;

import android.app.Application;

/**
 * Created by Oleg on 02.02.2015.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HelperFactory.setHelper(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
