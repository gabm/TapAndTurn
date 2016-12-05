package com.gabm.tapandturn.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by gabm on 31.10.16.
 */

public class ScreenRotatorOverlay {
    private LinearLayout dummyLayout;
    private WindowManager curWindowManager;
    private int currentlySetScreenOrientation ;


    public ScreenRotatorOverlay(Context context, WindowManager windowManager, int orientation) {
        dummyLayout = new LinearLayout(context);
        curWindowManager = windowManager;

        changeOrientation(orientation);
    }

    public int getCurrentlySetScreenOrientation() {
        return currentlySetScreenOrientation;
    }

    public void changeOrientation(int orientation) {
        if (dummyLayout.getParent() != null)
            curWindowManager.removeView(dummyLayout);

        WindowManager.LayoutParams dummyParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, 0, PixelFormat.RGBA_8888);
        dummyParams.screenOrientation = orientation;

        curWindowManager.addView(dummyLayout, dummyParams);

        currentlySetScreenOrientation = orientation;
    }

    // Immidiately removes the current view
    public void removeView() {
        if (dummyLayout.getParent() != null)
            curWindowManager.removeView(dummyLayout);
    }
}
