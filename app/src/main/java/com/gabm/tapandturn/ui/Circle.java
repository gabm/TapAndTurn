package com.gabm.tapandturn.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.gabm.tapandturn.R;

/**
 * Created by gabm on 23/05/17.
 * borrowed from: https://stackoverflow.com/questions/29381474/how-to-draw-a-circle-with-animation-in-android-with-circle-size-based-on-a-value
 */

public class Circle extends View {

    private static final int START_ANGLE_POINT = 0;
    private static final int STROKE_WIDTH = 10;
    private float angleFrom;
    private float angleTo;

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);



        //Initial Angle (optional, it can be zero)
        angleFrom = 0;
        angleTo = 120;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawOvalAndArrow(canvas);


    }
    private void drawOvalAndArrow(Canvas canvas){

        float outerBoxLength = getWidth();

        int strokeWidth = (int)outerBoxLength/10;
        float arrowSize = strokeWidth;
        float arrowOpening = strokeWidth;

        float outerPadding = strokeWidth/2 + arrowOpening/2 + outerBoxLength/10; // on both sides each

        float boxCenter = outerBoxLength/2;
        float circleRadius = boxCenter - outerPadding;

        Paint circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStrokeWidth(strokeWidth);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(getResources().getColor(R.color.colorAccent));

        // draw oval
        Path circlePath = new Path();
        final RectF arrowOval = new RectF(outerPadding, outerPadding, outerBoxLength-outerPadding, outerBoxLength-outerPadding);
        circlePath.addArc(arrowOval, angleFrom,angleTo-angleFrom);
        canvas.drawPath(circlePath, circlePaint);


        // draw arrow head
        Path arrowPath = new Path();
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setStrokeWidth(1);


        float centerDirX = (float)Math.cos(Math.toRadians(angleTo));
        float centerDirY = (float)Math.sin(Math.toRadians(angleTo));
        float tangentDirX = -centerDirY;
        float tangentDirY = centerDirX;

        // calc arrow head
        float arrowTailX = boxCenter + centerDirX*circleRadius;
        float arrowTailY = boxCenter + centerDirY*circleRadius;

        float arrowHeadX = arrowTailX + tangentDirX*arrowSize;
        float arrowHeadY = arrowTailY + tangentDirY*arrowSize;

        float arrowLeftX = arrowTailX + centerDirX*arrowOpening;
        float arrowLeftY = arrowTailY + centerDirY*arrowOpening;

        float arrowRightX = arrowTailX - centerDirX*arrowOpening;
        float arrowRightY = arrowTailY - centerDirY*arrowOpening;

        arrowPath.moveTo(arrowHeadX, arrowHeadY);
        arrowPath.lineTo(arrowLeftX, arrowLeftY);
        arrowPath.lineTo(arrowRightX, arrowRightY);
        arrowPath.lineTo(arrowHeadX, arrowHeadY);

        canvas.drawPath(arrowPath, circlePaint);


    }
    public float getAngleTo() {
        return angleTo;
    }

    public void setAngleTo(float angle) {
        this.angleTo = angle;
    }

    public void setAngleFrom(float angle) {
        this.angleFrom = angle;
    }

    public float getAngleFrom() {
        return angleFrom;
    }
}