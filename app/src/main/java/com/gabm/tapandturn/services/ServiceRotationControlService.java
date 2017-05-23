package com.gabm.tapandturn.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.gabm.tapandturn.AbsoluteOrientation;
import com.gabm.tapandturn.sensors.PhysicalOrientationSensorNG;
import com.gabm.tapandturn.sensors.WindowManagerSensor;
import com.gabm.tapandturn.ui.MainActivity;
import com.gabm.tapandturn.R;
import com.gabm.tapandturn.ui.ScreenRotatorOverlay;
import com.gabm.tapandturn.ui.OrientationButtonOverlay;

/**
 * Created by gabm on 30.10.16.
 */

public class ServiceRotationControlService extends Service implements PhysicalOrientationSensorNG.OrientationListenerNG, View.OnClickListener{
    private NotificationManager mNM;
    private Notification.Builder curNotificationBuilder = null;

    private WindowManagerSensor windowManagerSensor;
    private PhysicalOrientationSensorNG physicalOrientationSensorNG;
    private ScreenRotatorOverlay screenRotatorOverlay;
    private OrientationButtonOverlay orientationButtonOverlay;


    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.orientation_service_started;
    private AbsoluteOrientation handlerScreenOrientation;

    /*
    @Override
    public void onOrientationChange(int newScreenOrientation) {
        Log.i("AbsoluteOrientation", String.valueOf(newScreenOrientation));

        if (!initialized) {
            initialize(newScreenOrientation);
            return;
        }

        int oldScreenOrientation = screenRotatorOverlay.getCurrentlySetScreenOrientation();
        if (newScreenOrientation != oldScreenOrientation) {

            orientationButtonOverlay.show(oldScreenOrientation, newScreenOrientation);
            handlerScreenOrientation = newScreenOrientation;
        }
        else {
            orientationButtonOverlay.hide();
        }
    }*/

    @Override
    public void onClick(View view) {
        orientationButtonOverlay.hide();
        if (handlerScreenOrientation.equals(physicalOrientationSensorNG.getCurScreenOrientation())) {
            screenRotatorOverlay.changeOrientation(handlerScreenOrientation);

            if (screenRotatorOverlay.isDefaultOrientation(handlerScreenOrientation))
                curNotificationBuilder.setContentText(getText(R.string.orientation_service_started) + ": "  + getText(R.string.no_screen_overlay));
            else
                curNotificationBuilder.setContentText(getText(R.string.orientation_service_started) + ": "  + getText(R.string.screen_overlay));

            mNM.notify(NOTIFICATION, curNotificationBuilder.build());
        }
    }

    private void initialize() {

        // Initialize layout params
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        screenRotatorOverlay = new ScreenRotatorOverlay(getApplicationContext(), windowManager);
        orientationButtonOverlay = new OrientationButtonOverlay(getApplicationContext(), windowManager, this);

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        physicalOrientationSensorNG = new PhysicalOrientationSensorNG(getApplicationContext(), SensorManager.SENSOR_DELAY_NORMAL, this);
        physicalOrientationSensorNG.enable();

        windowManagerSensor = new WindowManagerSensor((WindowManager) getSystemService(WINDOW_SERVICE));

        initialize();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("LocalService", "Service stopped");

        screenRotatorOverlay.removeView();
        orientationButtonOverlay.hide();

        physicalOrientationSensorNG.disable();

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

        curNotificationBuilder = new Notification.Builder(this);
        curNotificationBuilder.setSmallIcon(R.mipmap.ic_screen_rotation_black_48dp)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.orientation_service_label))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .setPriority(Notification.PRIORITY_MIN)
                .setShowWhen(false)
                .setOngoing(true);

        // Send the notification.
        mNM.notify(NOTIFICATION, curNotificationBuilder.build());
    }

    @Override
    public void onOrientationChangeNG(AbsoluteOrientation screenOrientation) {
        Log.i("OrientationChangeNG", screenOrientation.toString());
        Log.i("WindowManagerSensor", windowManagerSensor.query().toString());

        if (!screenOrientation.equals(screenRotatorOverlay.getCurrentlySetScreenOrientation())) {
            handlerScreenOrientation = screenOrientation;
            orientationButtonOverlay.show(screenRotatorOverlay.getCurrentlySetScreenOrientation().toActivityInfoOrientation(), screenOrientation.toActivityInfoOrientation());
        }
    }


    public class ServiceRotationControlBinder extends Binder {
        ServiceRotationControlService getService() {
            return ServiceRotationControlService.this;
        }
    }

    public static void Start(Context context) {
        context.startService(new Intent(context, ServiceRotationControlService.class));
    }

    public static void Stop(Context context) {
        context.stopService(new Intent(context, ServiceRotationControlService.class));
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new ServiceRotationControlBinder();

}