package com.gabm.tapandturn.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by gabm on 05/06/17.
 */

public class PhysicalOrientationSensor2 implements SensorEventListener{

    private final SensorManager _sensorManager;
    private final Sensor _rotationVectorSensor;

    public PhysicalOrientationSensor2(Context context) {
        _sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        _rotationVectorSensor = _sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    public void enable() {
        _sensorManager.registerListener(this, _rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void disable() {
        _sensorManager.unregisterListener(this, _rotationVectorSensor);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // It is good practice to check that we received the proper sensor event
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
        {
            float[] mRotationMatrix = new float[16];
            float[] orientationVals = new float[3];
            // Convert the rotation-vector to a 4x4 matrix.
            SensorManager.getRotationMatrixFromVector(mRotationMatrix,
                    event.values);
            SensorManager
                    .remapCoordinateSystem(mRotationMatrix,
                            SensorManager.AXIS_X, SensorManager.AXIS_Z,
                            mRotationMatrix);
            SensorManager.getOrientation(mRotationMatrix, orientationVals);

            // Optionally convert the result from radians to degrees
            orientationVals[0] = (float) Math.toDegrees(orientationVals[0]);
            orientationVals[1] = (float) Math.toDegrees(orientationVals[1]);
            orientationVals[2] = (float) Math.toDegrees(orientationVals[2]);

            Log.i("PO2", " Yaw: " + orientationVals[0] + "\n Pitch: "
                    + orientationVals[1] + "\n Roll (not used): "
                    + orientationVals[2]);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
