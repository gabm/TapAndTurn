package com.gabm.tapandturn;

import android.app.Application;

import com.gabm.tapandturn.settings.SettingsManager;

/**
 * Created by gabm on 17/12/16.
 */

public class TapAndTurnApplication extends Application {

    public static SettingsManager settings;
    @Override
    public void onCreate() {
        super.onCreate();
        settings = new SettingsManager(getApplicationContext());
    }
}
