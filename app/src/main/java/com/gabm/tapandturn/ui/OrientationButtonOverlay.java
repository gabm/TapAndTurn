package com.gabm.tapandturn.ui;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.gabm.tapandturn.AbsoluteOrientation;
import com.gabm.tapandturn.R;
import com.gabm.tapandturn.TapAndTurnApplication;
import com.gabm.tapandturn.settings.SettingsKeys;

/**
 * Created by gabm on 31.10.16.
 */

public class OrientationButtonOverlay {
    private WindowManager curWindowManager;
    private FrameLayout parentLayout;
    private Circle circle;
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

        circle = (Circle) LayoutInflater.from(context).inflate(R.layout.rotation_button_circle, null);
        circle.setOnClickListener(listener);
        circle.setAngleTo(80);

        hideButtonRunnable = new HideButtonRunnable();
        timeoutHandler = new Handler();
        parentLayout = new FrameLayout(context);
        parentLayout.addView(circle);
    }

    public void show(AbsoluteOrientation oldOrientation, AbsoluteOrientation newOrientation) {
        if (isActive())
            curWindowManager.removeView(parentLayout);

        final int iconSizeDP = TapAndTurnApplication.settings.getInt(SettingsKeys.ICONSIZE, 62);
        final int iconSizePx = (int)(curContext.getResources().getDisplayMetrics().density * iconSizeDP + 0.5);
        final int iconTimeoutMS = TapAndTurnApplication.settings.getInt(SettingsKeys.ICONTIMEOUT, 2000);
        final boolean leftHandedMode = TapAndTurnApplication.settings.getBoolean(SettingsKeys.LEFT_HANDED_MODE, false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                iconSizePx, iconSizePx,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                PixelFormat.TRANSLUCENT);
        AlignmentInfo alignmentInfo = getButtonAlignment(oldOrientation, newOrientation, leftHandedMode);
        layoutParams.gravity = alignmentInfo.gravityFlag;

        circle.setRotation(alignmentInfo.rotation);

        circle.setAngleFrom(0);
        circle.setAngleTo(0);
        circle.setColors(
                TapAndTurnApplication.settings.getInt(SettingsKeys.ICONFOREGROUNDCOLOR, 0x000000),
                TapAndTurnApplication.settings.getInt(SettingsKeys.ICONBACKGROUNDCOLOR, 0x000000));
        curWindowManager.addView(parentLayout, layoutParams);

        timeoutHandler.removeCallbacks(hideButtonRunnable);
        timeoutHandler.postDelayed(hideButtonRunnable, iconTimeoutMS);

        CircleAngleAnimation animation = new CircleAngleAnimation(circle, 45, 315, (int)(iconTimeoutMS*0.5));
        circle.startAnimation(animation);
    }

    class AlignmentInfo {
        int gravityFlag;
        float rotation;
    }

    private AlignmentInfo getButtonAlignment(AbsoluteOrientation oldScreenOrientation, AbsoluteOrientation newScreenOrientation, boolean leftHanded) {
        if (leftHanded)
            return getButtonAlignmentLeftHanded(oldScreenOrientation, newScreenOrientation);
        else
            return getButtonAlignmentRightHanded(oldScreenOrientation, newScreenOrientation);
    }

    private AlignmentInfo getButtonAlignmentLeftHanded(AbsoluteOrientation oldScreenOrientation, AbsoluteOrientation newScreenOrientation) {
        TapAndTurnApplication.log(Log.INFO, "OverlayButton", "left handed, old: " + oldScreenOrientation + " new: " + newScreenOrientation);

        AlignmentInfo alignmentInfo = new AlignmentInfo();
        alignmentInfo.rotation = 0;
        alignmentInfo.gravityFlag = Gravity.CENTER;

        // coming from portrait
        if (oldScreenOrientation.isPortrait()) {
            if (newScreenOrientation.isLandscape()) {
                alignmentInfo.gravityFlag = Gravity.TOP | Gravity.RIGHT;
                alignmentInfo.rotation = 90;
            }

            if (newScreenOrientation.isReverseLandscape()) {
                alignmentInfo.gravityFlag = Gravity.BOTTOM | Gravity.LEFT;
                alignmentInfo.rotation = 270;
            }

            if (newScreenOrientation.isReversePortrait()){
                alignmentInfo.gravityFlag = Gravity.BOTTOM | Gravity.RIGHT;
                alignmentInfo.rotation = 180;
            }

        }

        // coming from landscape
        if (oldScreenOrientation.isLandscape()) {
            if (newScreenOrientation.isPortrait()){
                alignmentInfo.gravityFlag = Gravity.BOTTOM | Gravity.LEFT;
                alignmentInfo.rotation = 270;
            }

            if (newScreenOrientation.isReversePortrait()) {
                alignmentInfo.gravityFlag = Gravity.TOP | Gravity.RIGHT;
                alignmentInfo.rotation = 90;
            }


            if (newScreenOrientation.isReverseLandscape()){
                alignmentInfo.gravityFlag = Gravity.BOTTOM | Gravity.RIGHT;
                alignmentInfo.rotation = 180;
            }

        }

        // coming from reverse landscape
        if (oldScreenOrientation.isReverseLandscape()) {
            if (newScreenOrientation.isPortrait()){
                alignmentInfo.gravityFlag = Gravity.TOP | Gravity.RIGHT;
                alignmentInfo.rotation = 90;
            }


            if (newScreenOrientation.isReversePortrait()){
                alignmentInfo.gravityFlag = Gravity.BOTTOM | Gravity.LEFT;
                alignmentInfo.rotation = 270;
            }

            if (newScreenOrientation.isLandscape()){
                alignmentInfo.gravityFlag = Gravity.BOTTOM | Gravity.RIGHT;
                alignmentInfo.rotation = 180;
            }

        }

        // coming from reverse portrait
        if (oldScreenOrientation.isReversePortrait()) {
            if (newScreenOrientation.isReverseLandscape()){
                alignmentInfo.gravityFlag = Gravity.TOP | Gravity.RIGHT;
                alignmentInfo.rotation = 90;
            }


            if (newScreenOrientation.isLandscape()){
                alignmentInfo.gravityFlag = Gravity.BOTTOM | Gravity.LEFT;
                alignmentInfo.rotation = 270;
            }

            if (newScreenOrientation.isPortrait()) {
                alignmentInfo.gravityFlag = Gravity.BOTTOM | Gravity.RIGHT;
                alignmentInfo.rotation = 180;
            }
        }

        return alignmentInfo;
    }

    private AlignmentInfo getButtonAlignmentRightHanded(AbsoluteOrientation oldScreenOrientation, AbsoluteOrientation newScreenOrientation) {
        TapAndTurnApplication.log(Log.INFO, "OverlayButton", "right handed, old: " + oldScreenOrientation + " new: " + newScreenOrientation);

        AlignmentInfo alignmentInfo = new AlignmentInfo();
        alignmentInfo.rotation = 0;
        alignmentInfo.gravityFlag = Gravity.CENTER;

        // coming from portrait
        if (oldScreenOrientation.isPortrait()) {
            if (newScreenOrientation.isLandscape()) {
                alignmentInfo.gravityFlag = Gravity.BOTTOM | Gravity.RIGHT;
                alignmentInfo.rotation = 90;
            }

            if (newScreenOrientation.isReverseLandscape()) {
                alignmentInfo.gravityFlag = Gravity.TOP | Gravity.LEFT;
                alignmentInfo.rotation = 270;
            }

            if (newScreenOrientation.isReversePortrait()){
                alignmentInfo.gravityFlag = Gravity.BOTTOM | Gravity.LEFT;
                alignmentInfo.rotation = 180;
            }

        }

        // coming from landscape
        if (oldScreenOrientation.isLandscape()) {
            if (newScreenOrientation.isPortrait()){
                alignmentInfo.gravityFlag = Gravity.TOP | Gravity.LEFT;
                alignmentInfo.rotation = 270;
            }

            if (newScreenOrientation.isReversePortrait()) {
                alignmentInfo.gravityFlag = Gravity.BOTTOM | Gravity.RIGHT;
                alignmentInfo.rotation = 90;
            }


            if (newScreenOrientation.isReverseLandscape()){
                alignmentInfo.gravityFlag = Gravity.BOTTOM | Gravity.LEFT;
                alignmentInfo.rotation = 180;
            }

        }

        // coming from reverse landscape
        if (oldScreenOrientation.isReverseLandscape()) {
            if (newScreenOrientation.isPortrait()){
                alignmentInfo.gravityFlag = Gravity.BOTTOM | Gravity.RIGHT;
                alignmentInfo.rotation = 90;
            }


            if (newScreenOrientation.isReversePortrait()){
                alignmentInfo.gravityFlag = Gravity.TOP | Gravity.LEFT;
                alignmentInfo.rotation = 270;
            }

            if (newScreenOrientation.isLandscape()){
                alignmentInfo.gravityFlag = Gravity.BOTTOM | Gravity.LEFT;
                alignmentInfo.rotation = 180;
            }

        }

        // coming from reverse portrait
        if (oldScreenOrientation.isReversePortrait()) {
            if (newScreenOrientation.isReverseLandscape()){
                alignmentInfo.gravityFlag = Gravity.BOTTOM | Gravity.RIGHT;
                alignmentInfo.rotation = 90;
            }


            if (newScreenOrientation.isLandscape()){
                alignmentInfo.gravityFlag = Gravity.TOP | Gravity.LEFT;
                alignmentInfo.rotation = 270;
            }

            if (newScreenOrientation.isPortrait()) {
                alignmentInfo.gravityFlag = Gravity.BOTTOM | Gravity.LEFT;
                alignmentInfo.rotation = 180;
            }
        }

        return alignmentInfo;
    }

    public void hide() {
        if (isActive()) {
            timeoutHandler.removeCallbacks(hideButtonRunnable);
            curWindowManager.removeView(parentLayout);
        }
    }

    public boolean isActive() {
        return parentLayout.getParent() != null;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        circle.setOnClickListener(listener);
    }
}
