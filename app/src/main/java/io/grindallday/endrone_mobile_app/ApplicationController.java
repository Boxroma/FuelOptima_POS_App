package io.grindallday.endrone_mobile_app;

import android.app.Application;
import timber.log.Timber;

public class ApplicationController extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // This will initialise Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
