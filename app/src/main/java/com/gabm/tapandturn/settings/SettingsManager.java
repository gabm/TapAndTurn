package com.gabm.tapandturn.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by gabm on 17/12/16.
 */

public class SettingsManager {

    private static final String SettingsName = "ScreenRotationControl";

    private final SharedPreferences preferences;
    private SharedPreferences.Editor editor=null;

    public SettingsManager(Context context) {
        preferences = context.getSharedPreferences(SettingsName, Context.MODE_PRIVATE);
    }

    public void startEditMode() {
        if (!isEditMode())
            editor = preferences.edit();
    }

    public void finishEditMode() {
        if (isEditMode()) {
            editor.apply();
            editor = null;
        }
    }

    public boolean isEditMode() {
        return editor != null;
    }

    public boolean getBoolean(SettingsKeys key, boolean defaultValue) {
        Log.i("getBoolean", key.name());
        return preferences.getBoolean(key.name(), defaultValue);
    }

    public int getInt(SettingsKeys key, int defaultValue) {
        return preferences.getInt(key.name(), defaultValue);
    }

    public void putBoolean(SettingsKeys key, boolean value) {
        if (!isEditMode()) {
            startEditMode();
            editor.putBoolean(key.name(), value);
            finishEditMode();
        } else {
            editor.putBoolean(key.name(), value);
        }
    }

    public void putInt(SettingsKeys key, int value) {
        if (!isEditMode()) {
            startEditMode();
            editor.putInt(key.name(), value);
            finishEditMode();
        } else {
            editor.putInt(key.name(), value);
        }
    }
}
