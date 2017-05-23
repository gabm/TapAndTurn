package com.gabm.tapandturn.sensors;

import android.content.Context;
import android.view.OrientationEventListener;

import com.gabm.tapandturn.AbsoluteOrientation;
import com.gabm.tapandturn.TapAndTurnApplication;
import com.gabm.tapandturn.settings.SettingsKeys;

/**
 * Created by gabm on 30.10.16.
 */

// borrowed from: http://stackoverflow.com/questions/9021890/get-phone-orientation-but-fix-screen-orientation-to-portrait
public class PhysicalOrientationSensor extends OrientationEventListener {

    private AbsoluteOrientation curScreenOrientation = new AbsoluteOrientation(AbsoluteOrientation.Enum.Unknown);
    private OrientationListenerNG listener;

    public PhysicalOrientationSensor(Context context, int rate, OrientationListenerNG listener) {
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
    public void enable() {
        curScreenOrientation = new AbsoluteOrientation(AbsoluteOrientation.Enum.Unknown);
        super.enable();
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (orientation == -1){
            return;
        }

        AbsoluteOrientation newOrientation;
        if (orientation >= 60 && orientation <= 140){
            newOrientation = new AbsoluteOrientation(AbsoluteOrientation.Enum.Reverse_Landscape);
        } else if (orientation >= 140 && orientation <= 220) {
            newOrientation = new AbsoluteOrientation(AbsoluteOrientation.Enum.Reverse_Portrait);
        } else if (orientation >= 220 && orientation <= 300) {
            newOrientation = new AbsoluteOrientation(AbsoluteOrientation.Enum.Landscape);
        } else {
            newOrientation = new AbsoluteOrientation(AbsoluteOrientation.Enum.Portrait);
        }

        if(!newOrientation.equals(curScreenOrientation)) {

            // if the device is in reverse portrait mode
            if (newOrientation.equals(AbsoluteOrientation.Enum.Reverse_Portrait))
                // and reverse portrait mode got disabled
                if (!TapAndTurnApplication.settings.getBoolean(SettingsKeys.USE_REVERSE_PORTRAIT, false))
                    // then ignore
                    return;

            curScreenOrientation = newOrientation;
            if(listener != null){
                listener.onOrientationChange(curScreenOrientation);
            }
        }
    }

    public void setListener(OrientationListenerNG listener){
        this.listener = listener;
    }

    public AbsoluteOrientation getCurScreenOrientation(){
        return curScreenOrientation;
    }

    public interface OrientationListenerNG {
        void onOrientationChange(AbsoluteOrientation screenOrientation);
    }
}