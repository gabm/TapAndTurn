package com.gabm.tapandturn.sensors;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.view.OrientationEventListener;

/**
 * Created by gabm on 30.10.16.
 */

// borrowed from: http://stackoverflow.com/questions/9021890/get-phone-orientation-but-fix-screen-orientation-to-portrait
public class PhysicalOrientationSensor extends OrientationEventListener {

    private int curScreenOrientation;
    private OrientationListener listener;
    private SharedPreferences curPreferences;

    public PhysicalOrientationSensor(Context context, int rate, OrientationListener listener, SharedPreferences preferences) {
        super(context, rate);
        setListener(listener);
        curPreferences = preferences;
    }

    public PhysicalOrientationSensor(Context context, int rate) {
        super(context, rate);
    }

    public PhysicalOrientationSensor(Context context) {
        super(context);
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (orientation == -1){
            return;
        }

        final boolean useReversePortrait = curPreferences.getBoolean("UseReversePortrait", false);

        int newOrientation;
        if (orientation >= 60 && orientation <= 140){
            newOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        } else if (useReversePortrait && orientation >= 140 && orientation <= 220) {
            newOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
        } else if (orientation >= 220 && orientation <= 300) {
            newOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        } else {
            newOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
        if(newOrientation != curScreenOrientation){
            curScreenOrientation = newOrientation;
            if(listener != null){
                listener.onOrientationChange(curScreenOrientation);
            }
        }
    }

    public void setListener(OrientationListener listener){
        this.listener = listener;
    }

    public int getCurScreenOrientation(){
        return curScreenOrientation;
    }

    public interface OrientationListener {
        void onOrientationChange(int screenOrientation);
    }
}