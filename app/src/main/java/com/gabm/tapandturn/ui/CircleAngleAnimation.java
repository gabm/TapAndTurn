package com.gabm.tapandturn.ui;

import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

/**
 * Created by gabm on 23/05/17.
 * borrowed from: https://stackoverflow.com/questions/29381474/how-to-draw-a-circle-with-animation-in-android-with-circle-size-based-on-a-value
 */


public class CircleAngleAnimation extends Animation {

    private Circle circle;

    private float oldAngleTo;
    private float newAngleTo;

    private float oldAngleFrom;
    private float newAngleFrom;

    public CircleAngleAnimation(Circle circle, int newAngleFrom, int newAngleTo, int duratonMS) {
        this.oldAngleTo = circle.getAngleTo();
        this.newAngleTo = newAngleTo;
        this.oldAngleFrom = circle.getAngleFrom();
        this.newAngleFrom = newAngleFrom;
        this.circle = circle;
        this.setDuration(duratonMS);


        this.setInterpolator(new DecelerateInterpolator(1.3f));
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        float angleTo = oldAngleTo + ((newAngleTo - oldAngleTo) * interpolatedTime);
        float angleFrom = oldAngleFrom + ((newAngleFrom - oldAngleFrom)*interpolatedTime);

        circle.setAngleTo(angleTo);
        circle.setAngleFrom(angleFrom);
        circle.requestLayout();
    }
}