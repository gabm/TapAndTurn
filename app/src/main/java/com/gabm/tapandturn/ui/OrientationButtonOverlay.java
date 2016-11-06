package com.gabm.tapandturn.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gabm.tapandturn.R;

/**
 * Created by gabm on 31.10.16.
 */

public class OrientationButtonOverlay {
    private WindowManager curWindowManager;
    private RelativeLayout buttonLayout;
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

        buttonLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.rotation_button, null);
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

    public void show(int oldOrientation, int newOrientation) {
        if (buttonLayout.getParent() == null) {
            layoutParams.screenOrientation = oldOrientation;

            setButtonAlignment(oldOrientation, newOrientation);
            curWindowManager.addView(buttonLayout, layoutParams);

            timeoutHandler.removeCallbacks(hideButtonRunnable);
            timeoutHandler.postDelayed(hideButtonRunnable, 4000);
        }
    }

    private void setButtonAlignment(int oldScreenOrientation, int newScreenOrientation) {
        Log.i("OrientationChange:", "old: " + oldScreenOrientation + " new: " + newScreenOrientation);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

        // coming from portrait
        if (oldScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                //return Gravity.BOTTOM | Gravity.RIGHT;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }

            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                //return Gravity.TOP | Gravity.LEFT;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }

            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT){
                //return Gravity.BOTTOM | Gravity.LEFT;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }

        }

        // coming from landscape
        if (oldScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                //return Gravity.TOP | Gravity.LEFT;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }

            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                //return Gravity.BOTTOM | Gravity.RIGHT;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }


            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE){
                //return Gravity.BOTTOM | Gravity.LEFT;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }

        }

        // coming from reverse landscape
        if (oldScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                //return Gravity.BOTTOM | Gravity.RIGHT;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }


            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT){
                //return Gravity.TOP | Gravity.LEFT;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }

            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE){
                //return Gravity.BOTTOM | Gravity.LEFT;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }

        }

        // coming from reverse portrait
        if (oldScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE){
                //return Gravity.BOTTOM | Gravity.RIGHT;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }


            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                //return Gravity.TOP | Gravity.LEFT;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }

            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                //return Gravity.BOTTOM | Gravity.LEFT;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }
        }

        imageButton.setLayoutParams(layoutParams);
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
