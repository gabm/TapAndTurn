package com.gabm.tapandturn.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.gabm.tapandturn.AbsoluteOrientation;
import com.gabm.tapandturn.sensors.PhysicalOrientationSensor;
import com.gabm.tapandturn.sensors.WindowManagerSensor;
import com.gabm.tapandturn.R;
import com.gabm.tapandturn.ui.ScreenRotatorOverlay;
import com.gabm.tapandturn.ui.OrientationButtonOverlay;

/**
 * Created by gabm on 30.10.16.
 */

public class ServiceRotationControlService extends Service implements PhysicalOrientationSensor.OrientationListenerNG, View.OnClickListener{
    private NotificationManager mNM;
    private Notification.Builder curNotificationBuilder = null;

    private PhysicalOrientationSensor physicalOrientationSensor;
    private ScreenRotatorOverlay screenRotatorOverlay;
    private OrientationButtonOverlay orientationButtonOverlay;
    private WindowManager windowManager;
    private boolean isActive = false;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.notification_service_active;
    private AbsoluteOrientation handlerScreenOrientation;
    private final String TOGGLE_ACTIVE_BROADCAST = "ToggleActiveBroadcast";

    @Override
    public void onClick(View view) {
        orientationButtonOverlay.hide();
        if (handlerScreenOrientation.equals(physicalOrientationSensor.getCurScreenOrientation())) {
            screenRotatorOverlay.forceOrientation(handlerScreenOrientation);

            curNotificationBuilder.setContentText(getText(R.string.notification_service_active) + ": "  + getText(R.string.screen_overlay));

            mNM.notify(NOTIFICATION, curNotificationBuilder.build());
        }
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        physicalOrientationSensor = new PhysicalOrientationSensor(getApplicationContext(), SensorManager.SENSOR_DELAY_NORMAL, this);
        physicalOrientationSensor.enable();

        // Initialize layout params
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        screenRotatorOverlay = new ScreenRotatorOverlay(getApplicationContext(), windowManager);
        orientationButtonOverlay = new OrientationButtonOverlay(getApplicationContext(), windowManager, this);


        IntentFilter filter = new IntentFilter(TOGGLE_ACTIVE_BROADCAST);
        registerReceiver(broadcastReceiver, filter);

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();

        activate();
    }

    protected void deactivate() {
        if (!isActive)
            return;

        isActive = false;
        screenRotatorOverlay.removeView();
        orientationButtonOverlay.hide();
        physicalOrientationSensor.disable();

        curNotificationBuilder
                .setTicker(getText(R.string.notification_service_not_active))
                .setContentTitle(getText(R.string.notification_service_not_active))
                .setContentText(getText(R.string.no_screen_overlay));
        mNM.notify(NOTIFICATION, curNotificationBuilder.build());
    }

    protected void activate() {
        if (isActive)
            return;

        isActive = true;
        screenRotatorOverlay.forceOrientation(WindowManagerSensor.query(windowManager));
        orientationButtonOverlay.hide();
        physicalOrientationSensor.enable();

        curNotificationBuilder
                .setTicker(getText(R.string.notification_service_active))
                .setContentTitle(getText(R.string.notification_service_active))
                .setContentText(getText(R.string.screen_overlay));
        mNM.notify(NOTIFICATION, curNotificationBuilder.build());
    }

    protected void toggleActive() {
        if (isActive)
            deactivate();
        else
            activate();
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

        physicalOrientationSensor.disable();

        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.toast_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            toggleActive();
        }
    };
    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.notification_service_not_active);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(TOGGLE_ACTIVE_BROADCAST), 0);

        curNotificationBuilder = new Notification.Builder(this);
        curNotificationBuilder.setSmallIcon(R.mipmap.ic_screen_rotation_black_48dp)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(text)  // the label of the entry
                .setContentText(getText(R.string.no_screen_overlay))  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .setPriority(Notification.PRIORITY_MIN)
                .setShowWhen(false)
                .setOngoing(true);

        // Send the notification.
        mNM.notify(NOTIFICATION, curNotificationBuilder.build());
    }

    @Override
    public void onOrientationChange(AbsoluteOrientation newOrientation) {
        Log.i("OrientationChangeNG", newOrientation.toString());

        if (!newOrientation.equals(screenRotatorOverlay.getCurrentlySetScreenOrientation())) {
            handlerScreenOrientation = newOrientation;
            orientationButtonOverlay.show(screenRotatorOverlay.getCurrentlySetScreenOrientation(), newOrientation);
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