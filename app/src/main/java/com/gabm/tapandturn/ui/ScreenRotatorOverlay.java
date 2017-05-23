package com.gabm.tapandturn.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.gabm.tapandturn.AbsoluteOrientation;
import com.gabm.tapandturn.sensors.WindowManagerSensor;

/**
 * Created by gabm on 31.10.16.
 */

public class ScreenRotatorOverlay {
    private LinearLayout dummyLayout;
    private WindowManager curWindowManager;
    private AbsoluteOrientation currentlySetScreenOrientation ;
    private WindowManagerSensor windowManagerSensor;

    public ScreenRotatorOverlay(Context context, WindowManager windowManager) {
        dummyLayout = new LinearLayout(context);
        curWindowManager = windowManager;
        windowManagerSensor = new WindowManagerSensor(windowManager);
        currentlySetScreenOrientation = windowManagerSensor.query();
    }

    public boolean isDefaultOrientation(AbsoluteOrientation orientation) {
        return windowManagerSensor.query().equals(orientation);
    }
    public AbsoluteOrientation getCurrentlySetScreenOrientation() {
        return currentlySetScreenOrientation;
    }

    public void changeOrientation(AbsoluteOrientation orientation) {
        removeView();

        // if requested orientation is different from the device configured orientation
        // then enforece the new rotation by adding an overlay
       // if (!isDefaultOrientation(orientation)) {

            Log.i("Overlay", "Adding for " + orientation.toString());

            WindowManager.LayoutParams dummyParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, 0, PixelFormat.RGBA_8888);
            dummyParams.screenOrientation = orientation.toActivityInfoOrientation();

            curWindowManager.addView(dummyLayout, dummyParams);

        /*} else {
            Log.i("Overlay", "Not adding anything");
        }*/

        currentlySetScreenOrientation = orientation;
    }

    // Immidiately removes the current view
    public void removeView() {
        if (dummyLayout.getParent() != null) {
            Log.i("Overlay", "Removing overlay");

            curWindowManager.removeView(dummyLayout);
        }
    }
}
