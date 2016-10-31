package com.gabm.screenrotationcontrol.sensors;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.OrientationEventListener;

/**
 * Created by gabm on 30.10.16.
 */

// borrowed from: http://stackoverflow.com/questions/9021890/get-phone-orientation-but-fix-screen-orientation-to-portrait
public class PhysicalOrientationSensor extends OrientationEventListener {

    private int curScreenOrientation;
    private OrientationListener listener;

    public PhysicalOrientationSensor(Context context, int rate, OrientationListener listener) {
        super(context, rate);
        setListener(listener);
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
        int newOrientation;
        if (orientation >= 60 && orientation <= 140){
            newOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        } else if (orientation >= 140 && orientation <= 220) {
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