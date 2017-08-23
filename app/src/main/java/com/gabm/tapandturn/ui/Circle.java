package com.gabm.tapandturn.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
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
    private int foregroundColor = Color.parseColor("#FFF26419");
    private int backgroundColor = Color.parseColor("#44000000");

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

        int strokeWidth = (int)(outerBoxLength*0.12f);
        float arrowSize = strokeWidth;
        float arrowOpening = strokeWidth;

        float outerPadding = strokeWidth/2 + arrowOpening/2 + outerBoxLength/6; // on both sides each

        float boxCenter = outerBoxLength/2;
        float circleRadius = boxCenter - outerPadding;

        Paint paint = new Paint();
        paint.setShadowLayer(12,0,0, 0x44000000);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        setLayerType(LAYER_TYPE_SOFTWARE, paint);

        // draw background
        paint.setColor(backgroundColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(boxCenter, boxCenter, boxCenter-12, paint);

        // draw oval
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(foregroundColor);
        Path circlePath = new Path();
        final RectF arrowOval = new RectF(outerPadding, outerPadding, outerBoxLength-outerPadding, outerBoxLength-outerPadding);
        circlePath.addArc(arrowOval, angleFrom,angleTo-angleFrom);
        canvas.drawPath(circlePath, paint);


        // draw arrow head
        Path arrowPath = new Path();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);


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

        canvas.drawPath(arrowPath, paint);

    }
    public float getAngleTo() {
        return angleTo;
    }

    public void setAngleTo(float angle) {
        this.angleTo = angle;
    }

    public void setColors(int foregroundColor, int backgroundColor) {
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
    }

    public void setAngleFrom(float angle) {
        this.angleFrom = angle;
    }

    public float getAngleFrom() {
        return angleFrom;
    }
}