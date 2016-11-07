package com.gabm.tapandturn.ui;

import android.content.Context;
import android.content.SharedPreferences;
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
    private ImageButton imageButton;
    private Handler timeoutHandler;
    private Context curContext;
    private SharedPreferences curPreferences;

    private class HideButtonRunnable implements Runnable {
        @Override
        public void run() {
            hide();
        }
    }

    private HideButtonRunnable hideButtonRunnable;

    public OrientationButtonOverlay(Context context, WindowManager windowManager, View.OnClickListener listener, SharedPreferences preferences) {
        curWindowManager =windowManager;
        curContext = context;
        curPreferences = preferences;

        imageButton = (ImageButton) LayoutInflater.from(context).inflate(R.layout.rotation_button, null);
        imageButton.setOnClickListener(listener);

        hideButtonRunnable = new HideButtonRunnable();
        timeoutHandler = new Handler();
    }

    public void show(int oldOrientation, int newOrientation) {
        if (imageButton.getParent() != null)
            hide();

        int iconSizeDP = curPreferences.getInt("IconSize", 40);
        final int iconSizePx = (int)(curContext.getResources().getDisplayMetrics().density * iconSizeDP + 0.5);


        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                iconSizePx, iconSizePx,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                PixelFormat.TRANSLUCENT);

        layoutParams.screenOrientation = oldOrientation;
        layoutParams.gravity = setButtonAlignment(oldOrientation, newOrientation);
        curWindowManager.addView(imageButton, layoutParams);

        timeoutHandler.removeCallbacks(hideButtonRunnable);
        timeoutHandler.postDelayed(hideButtonRunnable, curPreferences.getInt("IconTimeout", 4000));
    }

    private int setButtonAlignment(int oldScreenOrientation, int newScreenOrientation) {
        Log.i("OrientationChange:", "old: " + oldScreenOrientation + " new: " + newScreenOrientation);


        // coming from portrait
        if (oldScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                return Gravity.BOTTOM | Gravity.RIGHT;
            }

            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                return Gravity.TOP | Gravity.LEFT;
            }

            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT){
                return Gravity.BOTTOM | Gravity.LEFT;
            }

        }

        // coming from landscape
        if (oldScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                return Gravity.TOP | Gravity.LEFT;
            }

            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                return Gravity.BOTTOM | Gravity.RIGHT;
            }


            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE){
                return Gravity.BOTTOM | Gravity.LEFT;
            }

        }

        // coming from reverse landscape
        if (oldScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                return Gravity.BOTTOM | Gravity.RIGHT;
            }


            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT){
                return Gravity.TOP | Gravity.LEFT;
            }

            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                return Gravity.BOTTOM | Gravity.LEFT;
            }

        }

        // coming from reverse portrait
        if (oldScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE){
                return Gravity.BOTTOM | Gravity.RIGHT;
            }


            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                return Gravity.TOP | Gravity.LEFT;
            }

            if (newScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                return Gravity.BOTTOM | Gravity.LEFT;
            }
        }

        return Gravity.CENTER;
    }

    public void hide() {
        if (imageButton.getParent() != null) {
            timeoutHandler.removeCallbacks(hideButtonRunnable);
            curWindowManager.removeView(imageButton);
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        imageButton.setOnClickListener(listener);
    }
}
