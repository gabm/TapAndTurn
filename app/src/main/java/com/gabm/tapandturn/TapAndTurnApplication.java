package com.gabm.tapandturn;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import com.gabm.tapandturn.settings.SettingsKeys;
import com.gabm.tapandturn.settings.SettingsManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by gabm on 17/12/16.
 */

public class TapAndTurnApplication extends Application {

    public static SettingsManager settings;
    private static File logFile = null;
    private static FileOutputStream logfileOutputStream = null;
    private static OutputStreamWriter logfileWriter = null;


    @Override
    public void onCreate() {
        super.onCreate();
        settings = new SettingsManager(getApplicationContext());
        setLoggingEnabled(TapAndTurnApplication.settings.getBoolean(SettingsKeys.LOGGING_ENABLED, false));
    }

    public static void setLoggingEnabled(boolean enabled) {
        if (enabled)
            createLogfile();
        else
            destroyLogfile();
        TapAndTurnApplication.settings.putBoolean(SettingsKeys.LOGGING_ENABLED, enabled);
    }

    private static void createLogfile() {
        try {
            logFile = new File(Environment.getExternalStorageDirectory() + "/tap_and_turn_log_" + System.currentTimeMillis() + ".txt");
            logFile.createNewFile();
            logfileOutputStream = new FileOutputStream(logFile);
            logfileWriter = new OutputStreamWriter(logfileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void destroyLogfile() {
        try {
            if (logfileWriter != null)
                logfileWriter.close();

            if (logfileOutputStream != null)
                logfileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(int level, String tag, String msg) {
        Log.println(level, tag, msg);

        if (!settings.getBoolean(SettingsKeys.LOGGING_ENABLED, false))
            return;

        try {
            Calendar calendar = Calendar.getInstance();
            Date now = calendar.getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
            String timestamp = simpleDateFormat.format(now);
            logfileWriter.write(timestamp + " | " + level + " | " + tag + " | " + msg + "\n");
            logfileWriter.flush();
            logfileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
