package com.gabm.tapandturn.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.gabm.tapandturn.MainActivity;
import com.gabm.tapandturn.R;
import com.gabm.tapandturn.sensors.PhysicalOrientationSensor;
import com.gabm.tapandturn.ui.ScreenRotatorOverlay;
import com.gabm.tapandturn.ui.OrientationButtonOverlay;

/**
 * Created by gabm on 30.10.16.
 */

public class ServiceRotationControlService extends Service implements PhysicalOrientationSensor.OrientationListener, View.OnClickListener{
    private NotificationManager mNM;



    private PhysicalOrientationSensor physicalOrientationSensor;

    private ScreenRotatorOverlay screenRotatorOverlay;
    private OrientationButtonOverlay orientationButtonOverlay;


    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.orientation_service_started;
    private int handlerScreenOrientation;

    @Override
    public void onOrientationChange(int newScreenOrientation) {
        Log.i("Orientation", String.valueOf(newScreenOrientation));

        int oldScreenOrientation = screenRotatorOverlay.getCurrentlySetScreenOrientation();
        if (newScreenOrientation != oldScreenOrientation) {

            orientationButtonOverlay.show(oldScreenOrientation, newScreenOrientation);
            handlerScreenOrientation = newScreenOrientation;
        }
        else
            orientationButtonOverlay.hide();

    }

    @Override
    public void onClick(View view) {
        orientationButtonOverlay.hide();
        if (handlerScreenOrientation == physicalOrientationSensor.getCurScreenOrientation()) {
            screenRotatorOverlay.changeOrientation(handlerScreenOrientation);

        }

    }


    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

       // Initialize layout params
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        screenRotatorOverlay = new ScreenRotatorOverlay(getApplicationContext(), windowManager, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        orientationButtonOverlay = new OrientationButtonOverlay(getApplicationContext(), windowManager, this);

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();

        physicalOrientationSensor = new PhysicalOrientationSensor(getApplicationContext(), SensorManager.SENSOR_DELAY_NORMAL, this);
        physicalOrientationSensor.enable();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        physicalOrientationSensor.disable();

        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.orientation_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.orientation_service_started);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_menu_rotate)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.orientation_service_label))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .setPriority(Notification.PRIORITY_MIN)
                .setShowWhen(false)
                .setOngoing(true)
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }


    public class ServiceRotationControlBinder extends Binder {
        ServiceRotationControlService getService() {
            return ServiceRotationControlService.this;
        }
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new ServiceRotationControlBinder();

}