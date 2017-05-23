package com.gabm.tapandturn.ui;

import android.content.Context;
import android.graphics.Color;
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

    public ScreenRotatorOverlay(Context context, WindowManager windowManager) {
        dummyLayout = new LinearLayout(context);

        curWindowManager = windowManager;
        currentlySetScreenOrientation = WindowManagerSensor.query(windowManager);

        forceOrientation(currentlySetScreenOrientation);
    }

    public AbsoluteOrientation getCurrentlySetScreenOrientation() {
        return currentlySetScreenOrientation;
    }

    public void forceOrientation(AbsoluteOrientation orientation) {
        removeView();

        Log.i("Overlay", "Adding for " + orientation.toString());

        WindowManager.LayoutParams dummyParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, 0, PixelFormat.RGBA_8888);
        dummyParams.screenOrientation = orientation.toActivityInfoOrientation();
        dummyParams.height = 0;
        dummyParams.width = 0;


        curWindowManager.addView(dummyLayout, dummyParams);


        currentlySetScreenOrientation = orientation;
    }

    // Immidiately removes the current view
    public void removeView() {
        if (isActive()) {
            Log.i("Overlay", "Removing overlay");

            curWindowManager.removeView(dummyLayout);
        }
    }

    public boolean isActive() {
        return dummyLayout.getParent() != null;
    }
}
