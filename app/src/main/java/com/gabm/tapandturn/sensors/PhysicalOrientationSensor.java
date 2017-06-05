package com.gabm.tapandturn.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.gabm.tapandturn.AbsoluteOrientation;
import com.gabm.tapandturn.TapAndTurnApplication;
import com.gabm.tapandturn.settings.SettingsKeys;

/**
 * Created by gabm on 30.10.16.
 */

// borrowed from: http://stackoverflow.com/questions/9021890/get-phone-orientation-but-fix-screen-orientation-to-portrait
public class PhysicalOrientationSensor implements SensorEventListener {

    private AbsoluteOrientation curScreenOrientation = new AbsoluteOrientation(AbsoluteOrientation.Enum.Unknown);
    private OrientationListener listener;
    private final SensorManager _sensorManager;
    private final Sensor _accelerationSensor;

    public PhysicalOrientationSensor(Context context, OrientationListener listener) {
        _sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        _accelerationSensor = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        setListener(listener);
    }

    public void enable() {
        curScreenOrientation = new AbsoluteOrientation(AbsoluteOrientation.Enum.Unknown);
        _sensorManager.registerListener(this, _accelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void disable() {
        _sensorManager.unregisterListener(this, _accelerationSensor);
    }

    public void calculateAndNotify(int inclination, int orientation) {
        if (inclination < 25 || inclination > 155)
        {
            // device is flat
            if (!curScreenOrientation.equals(AbsoluteOrientation.Enum.Unknown) && listener != null)
                listener.onOrientationChange(new AbsoluteOrientation(AbsoluteOrientation.Enum.Unknown));

            curScreenOrientation = new AbsoluteOrientation(AbsoluteOrientation.Enum.Unknown);
            return;
        }


        // else device is not flat, lets calculate the orientation
        AbsoluteOrientation newOrientation;
        if (orientation >= -115 && orientation <= -65){
            newOrientation = new AbsoluteOrientation(AbsoluteOrientation.Enum.Reverse_Landscape);
        } else if ((orientation >= 155 && orientation <= 180) || orientation >= -180 && orientation <= -155) {
            newOrientation = new AbsoluteOrientation(AbsoluteOrientation.Enum.Reverse_Portrait);
        } else if (orientation >= 65 && orientation <= 115) {
            newOrientation = new AbsoluteOrientation(AbsoluteOrientation.Enum.Landscape);
        } else if (orientation >= -25 && orientation < 25) {
            newOrientation = new AbsoluteOrientation(AbsoluteOrientation.Enum.Portrait);
        } else {
            return;
        }

        if(!newOrientation.equals(curScreenOrientation)) {

            // if the device is in reverse portrait mode
            if (newOrientation.equals(AbsoluteOrientation.Enum.Reverse_Portrait))
                // and reverse portrait mode got disabled
                if (!TapAndTurnApplication.settings.getBoolean(SettingsKeys.USE_REVERSE_PORTRAIT, false))
                    // then ignore
                    return;

            curScreenOrientation = newOrientation;
            if(listener != null){
                listener.onOrientationChange(curScreenOrientation);
            }
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        // It is good practice to check that we received the proper sensor event
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            float[] g = event.values.clone();

            float norm_Of_g = (float)Math.sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2]);

            // Normalize the accelerometer vector
            g[0] = g[0] / norm_Of_g;
            g[1] = g[1] / norm_Of_g;
            g[2] = g[2] / norm_Of_g;

            // device is not flat
            int rotation = (int) Math.round(Math.toDegrees(Math.atan2(g[0], g[1])));
            int inclination = (int) Math.round(Math.toDegrees(Math.acos(g[2])));

            //Log.i("PS", String.valueOf(rotation));
            calculateAndNotify(inclination, rotation);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void setListener(OrientationListener listener){
        this.listener = listener;
    }

    public AbsoluteOrientation getCurScreenOrientation(){
        return curScreenOrientation;
    }

    public interface OrientationListener {
        void onOrientationChange(AbsoluteOrientation screenOrientation);
    }
}