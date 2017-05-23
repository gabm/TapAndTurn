package com.gabm.tapandturn.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.gabm.tapandturn.AbsoluteOrientation;
import com.gabm.tapandturn.R;
import com.gabm.tapandturn.TapAndTurnApplication;
import com.gabm.tapandturn.settings.SettingsKeys;

/**
 * Created by gabm on 31.10.16.
 */

public class OrientationButtonOverlay {
    private WindowManager curWindowManager;
    private ImageButton imageButton;
    private Handler timeoutHandler;
    private Context curContext;

    private class HideButtonRunnable implements Runnable {
        @Override
        public void run() {
            hide();
        }
    }

    private HideButtonRunnable hideButtonRunnable;

    public OrientationButtonOverlay(Context context, WindowManager windowManager, View.OnClickListener listener) {
        curWindowManager =windowManager;
        curContext = context;

        imageButton = (ImageButton) LayoutInflater.from(context).inflate(R.layout.rotation_button, null);
        imageButton.setOnClickListener(listener);

        hideButtonRunnable = new HideButtonRunnable();
        timeoutHandler = new Handler();
    }

    public void show(AbsoluteOrientation oldOrientation, AbsoluteOrientation newOrientation) {
        if (isActive())
            curWindowManager.removeView(imageButton);

        int iconSizeDP = TapAndTurnApplication.settings.getInt(SettingsKeys.ICONSIZE, 62);
        final int iconSizePx = (int)(curContext.getResources().getDisplayMetrics().density * iconSizeDP + 0.5);


        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                iconSizePx, iconSizePx,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                PixelFormat.TRANSLUCENT);

        //layoutParams.screenOrientation = oldOrientation;
        layoutParams.gravity = getButtonAlignment(oldOrientation, newOrientation, TapAndTurnApplication.settings.getBoolean(SettingsKeys.LEFT_HANDED_MODE, false));
        curWindowManager.addView(imageButton, layoutParams);

        timeoutHandler.removeCallbacks(hideButtonRunnable);
        timeoutHandler.postDelayed(hideButtonRunnable, TapAndTurnApplication.settings.getInt(SettingsKeys.ICONTIMEOUT, 2000));
    }

    private int getButtonAlignment(AbsoluteOrientation oldScreenOrientation, AbsoluteOrientation newScreenOrientation, boolean leftHanded) {
        if (leftHanded)
            return getButtonAlignmentLeftHanded(oldScreenOrientation, newScreenOrientation);
        else
            return getButtonAlignmentRightHanded(oldScreenOrientation, newScreenOrientation);
    }

    private int getButtonAlignmentLeftHanded(AbsoluteOrientation oldScreenOrientation, AbsoluteOrientation newScreenOrientation) {
        Log.i("OrientationChange:", "right handed, old: " + oldScreenOrientation + " new: " + newScreenOrientation);


        // coming from portrait
        if (oldScreenOrientation.isPortrait()) {
            if (newScreenOrientation.isLandscape()) {
                return Gravity.TOP | Gravity.RIGHT;
            }

            if (newScreenOrientation.isReverseLandscape()) {
                return Gravity.BOTTOM | Gravity.LEFT;
            }

            if (newScreenOrientation.isReversePortrait()){
                return Gravity.BOTTOM | Gravity.RIGHT;
            }

        }

        // coming from landscape
        if (oldScreenOrientation.isLandscape()) {
            if (newScreenOrientation.isPortrait()){
                return Gravity.BOTTOM | Gravity.LEFT;
            }

            if (newScreenOrientation.isReversePortrait()) {
                return Gravity.TOP | Gravity.RIGHT;
            }


            if (newScreenOrientation.isReverseLandscape()){
                return Gravity.BOTTOM | Gravity.RIGHT;
            }

        }

        // coming from reverse landscape
        if (oldScreenOrientation.isReverseLandscape()) {
            if (newScreenOrientation.isPortrait()){
                return Gravity.TOP | Gravity.RIGHT;
            }


            if (newScreenOrientation.isReversePortrait()){
                return Gravity.BOTTOM | Gravity.LEFT;
            }

            if (newScreenOrientation.isLandscape()){
                return Gravity.BOTTOM | Gravity.RIGHT;
            }

        }

        // coming from reverse portrait
        if (oldScreenOrientation.isReversePortrait()) {
            if (newScreenOrientation.isReverseLandscape()){
                return Gravity.TOP | Gravity.RIGHT;
            }


            if (newScreenOrientation.isLandscape()){
                return Gravity.BOTTOM | Gravity.LEFT;
            }

            if (newScreenOrientation.isPortrait()) {
                return Gravity.BOTTOM | Gravity.RIGHT;
            }
        }

        return Gravity.CENTER;
    }

    private int getButtonAlignmentRightHanded(AbsoluteOrientation oldScreenOrientation, AbsoluteOrientation newScreenOrientation) {
        Log.i("OrientationChange:", "left handed, old: " + oldScreenOrientation + " new: " + newScreenOrientation);


        // coming from portrait
        if (oldScreenOrientation.isPortrait()) {
            if (newScreenOrientation.isLandscape()) {
                return Gravity.BOTTOM | Gravity.RIGHT;
            }

            if (newScreenOrientation.isReverseLandscape()) {
                return Gravity.TOP | Gravity.LEFT;
            }

            if (newScreenOrientation.isReversePortrait()){
                return Gravity.BOTTOM | Gravity.LEFT;
            }

        }

        // coming from landscape
        if (oldScreenOrientation.isLandscape()) {
            if (newScreenOrientation.isPortrait()){
                return Gravity.TOP | Gravity.LEFT;
            }

            if (newScreenOrientation.isReversePortrait()) {
                return Gravity.BOTTOM | Gravity.RIGHT;
            }


            if (newScreenOrientation.isReverseLandscape()){
                return Gravity.BOTTOM | Gravity.LEFT;
            }

        }

        // coming from reverse landscape
        if (oldScreenOrientation.isReverseLandscape()) {
            if (newScreenOrientation.isPortrait()){
                return Gravity.BOTTOM | Gravity.RIGHT;
            }


            if (newScreenOrientation.isReverseLandscape()){
                return Gravity.TOP | Gravity.LEFT;
            }

            if (newScreenOrientation.isLandscape()){
                return Gravity.BOTTOM | Gravity.LEFT;
            }

        }

        // coming from reverse portrait
        if (oldScreenOrientation.isReversePortrait()) {
            if (newScreenOrientation.isReverseLandscape()){
                return Gravity.BOTTOM | Gravity.RIGHT;
            }


            if (newScreenOrientation.isLandscape()){
                return Gravity.TOP | Gravity.LEFT;
            }

            if (newScreenOrientation.isPortrait()) {
                return Gravity.BOTTOM | Gravity.LEFT;
            }
        }

        return Gravity.CENTER;
    }

    public void hide() {
        if (isActive()) {
            timeoutHandler.removeCallbacks(hideButtonRunnable);
            curWindowManager.removeView(imageButton);
        }
    }

    public boolean isActive() {
        return imageButton.getParent() != null;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        imageButton.setOnClickListener(listener);
    }
}
