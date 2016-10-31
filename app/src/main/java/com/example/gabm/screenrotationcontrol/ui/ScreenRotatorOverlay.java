package com.example.gabm.screenrotationcontrol.ui;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by gabm on 31.10.16.
 */

public class ScreenRotatorOverlay {
    private LinearLayout dummyLayout;
    private WindowManager curWindowManager;

    public ScreenRotatorOverlay(Context context, WindowManager windowManager, int orientation) {
        dummyLayout = new LinearLayout(context);
        curWindowManager = windowManager;
    }

    public void changeOrientation(int orientation) {
        if (dummyLayout.getParent() != null)
            curWindowManager.removeView(dummyLayout);

        WindowManager.LayoutParams dummyParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, 0, PixelFormat.RGBA_8888);
        dummyParams.screenOrientation = orientation;

        curWindowManager.addView(dummyLayout, dummyParams);
    }
}
