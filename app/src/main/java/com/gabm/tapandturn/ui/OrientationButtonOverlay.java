package com.gabm.tapandturn.ui;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.gabm.tapandturn.R;

/**
 * Created by gabm on 31.10.16.
 */

public class OrientationButtonOverlay {
    private WindowManager curWindowManager;
    private LinearLayout buttonLayout;
    private ImageButton imageButton;
    private WindowManager.LayoutParams layoutParams;
    private Handler timeoutHandler;

    private class HideButtonRunnable implements Runnable {
        @Override
        public void run() {
            hide();
        }
    }

    private HideButtonRunnable hideButtonRunnable;

    public OrientationButtonOverlay(Context context, WindowManager windowManager, View.OnClickListener listener) {
        curWindowManager =windowManager;

        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                PixelFormat.TRANSLUCENT);

        layoutParams.gravity = Gravity.CENTER;

        buttonLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.rotation_button, null);
        buttonLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hide();
                return false;
            }
        });

        imageButton = (ImageButton)buttonLayout.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(listener);

        hideButtonRunnable = new HideButtonRunnable();
        timeoutHandler = new Handler();
    }

    public void show(int current_orientation, int gravity) {
        if (buttonLayout.getParent() == null) {
            layoutParams.screenOrientation = current_orientation;
            layoutParams.gravity = gravity;
            curWindowManager.addView(buttonLayout, layoutParams);

            timeoutHandler.removeCallbacks(hideButtonRunnable);
            timeoutHandler.postDelayed(hideButtonRunnable, 4000);
        }
    }

    public void hide() {
        if (buttonLayout.getParent() != null) {
            timeoutHandler.removeCallbacks(hideButtonRunnable);
            curWindowManager.removeView(buttonLayout);
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        imageButton.setOnClickListener(listener);
    }
}
