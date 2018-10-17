package com.gabm.tapandturn.services;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.gabm.tapandturn.AbsoluteOrientation;
import com.gabm.tapandturn.TapAndTurnApplication;
import com.gabm.tapandturn.sensors.OverlayPermissionSensor;
import com.gabm.tapandturn.sensors.PhysicalOrientationSensor;
import com.gabm.tapandturn.sensors.WindowManagerSensor;
import com.gabm.tapandturn.R;
import com.gabm.tapandturn.settings.SettingsKeys;
import com.gabm.tapandturn.ui.ScreenRotatorOverlay;
import com.gabm.tapandturn.ui.OrientationButtonOverlay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by gabm on 30.10.16.
 */

public class RotationControlService extends Service implements PhysicalOrientationSensor.OrientationListener, View.OnClickListener, OverlayPermissionSensor.OverlayPermissionListener {
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
        TapAndTurnApplication.log(Log.INFO, "OnClick", "User demanded rotation");
        orientationButtonOverlay.hide();
        if (handlerScreenOrientation.equals(physicalOrientationSensor.getCurScreenOrientation()))
            screenRotatorOverlay.forceOrientation(handlerScreenOrientation);
    }

    private BroadcastReceiver toggleActiveBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            toggleActive();
        }
    };

    private BroadcastReceiver screenOffBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            KeyguardManager kgm = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                TapAndTurnApplication.log(Log.INFO, "BroadcastReceiver", "screen off");

                if (isActive) {
                    physicalOrientationSensor.disable();
                    orientationButtonOverlay.hide();

                    if (TapAndTurnApplication.settings.getBoolean(SettingsKeys.RESTORE_DEFAULT_ON_SCREEN_OFF, true))
                        screenRotatorOverlay.forceOrientation(WindowManagerSensor.queryDefaultOrientation(windowManager, getResources().getConfiguration()));

                }
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON) && !kgm.isKeyguardLocked() ||
                       intent.getAction().equals(Intent.ACTION_USER_PRESENT))
            {
                TapAndTurnApplication.log(Log.INFO, "BroadcastReceiver", "screen on or user present");

                if (isActive) {
                    physicalOrientationSensor.enable();
                }
            }
        }
    };

    private void createBroadcastReceivers() {

        IntentFilter filter = new IntentFilter(TOGGLE_ACTIVE_BROADCAST);
        registerReceiver(toggleActiveBroadcastReceiver, filter);

        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        registerReceiver(screenOffBroadcastReceiver, filter);
    }

    private void destroyBroadcastReceivers() {
        unregisterReceiver(toggleActiveBroadcastReceiver);
        unregisterReceiver(screenOffBroadcastReceiver);
    }

    @Override
    public void onCreate() {
        TapAndTurnApplication.log(Log.INFO, "RotationService", "Service started");


        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        physicalOrientationSensor = new PhysicalOrientationSensor(getApplicationContext(), this);
        physicalOrientationSensor.enable();

        // Initialize layout params
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        screenRotatorOverlay = new ScreenRotatorOverlay(getApplicationContext(), windowManager);
        orientationButtonOverlay = new OrientationButtonOverlay(getApplicationContext(), windowManager, this);

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();

        createBroadcastReceivers();

        activate();
    }

    protected void deactivate() {
        if (!isActive)
            return;

        isActive = false;
        screenRotatorOverlay.removeView();
        orientationButtonOverlay.hide();
        physicalOrientationSensor.disable();
        OverlayPermissionSensor.getInstance().removeListener(this);

        curNotificationBuilder
                .setContentTitle(getText(R.string.notification_service_not_active))
                .setContentText(getText(R.string.touch_to_enable));
        mNM.notify(NOTIFICATION, curNotificationBuilder.build());
    }

    protected void activate() {
        if (isActive)
            return;

        TapAndTurnApplication.log(Log.INFO, "RotationService", "activated");

        isActive = true;
        screenRotatorOverlay.forceOrientation(WindowManagerSensor.query(windowManager));
        orientationButtonOverlay.hide();
        physicalOrientationSensor.enable();
        OverlayPermissionSensor.getInstance().addListener(this);

        curNotificationBuilder
                .setContentTitle(getText(R.string.notification_service_active))
                .setContentText(getText(R.string.touch_to_disable));
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
        TapAndTurnApplication.log(Log.INFO, "LocalService", "Received start id " + startId + ": " + intent);
        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        TapAndTurnApplication.log(Log.INFO, "RotationService", "Service stopped");

        deactivate();

        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);

        destroyBroadcastReceivers();

        // Tell the user we stopped.
        Toast.makeText(this, R.string.toast_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //create a intent that you want to start again..
        Intent intent = new Intent(getApplicationContext(), RotationControlService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 500, pendingIntent);
        super.onTaskRemoved(rootIntent);
    }

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
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(text)  // the label of the entry
                .setContentText(getText(R.string.touch_to_enable))  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .setPriority(Notification.PRIORITY_MIN)
                .setShowWhen(false)
                .setOngoing(true);

        // Send the notification.
        mNM.notify(NOTIFICATION, curNotificationBuilder.build());
    }

    @Override
    public void onOrientationChange(AbsoluteOrientation newOrientation) {
        TapAndTurnApplication.log(Log.INFO,"onOrientationChange", "old: " + screenRotatorOverlay.getCurrentlySetScreenOrientation().toString() + " new: " + newOrientation.toString());

        if (!OverlayPermissionSensor.getInstance().query(getApplicationContext())) {
            Toast.makeText(this, R.string.permission_lost, Toast.LENGTH_LONG).show();
            return;
        }

        if (TapAndTurnApplication.settings.getBoolean(SettingsKeys.AUTO_RETURN_TO_DEFAULT, false) && newOrientation.equals(WindowManagerSensor.queryDefaultOrientation(windowManager,getResources().getConfiguration()))) {
            if (orientationButtonOverlay.isActive())
                orientationButtonOverlay.hide();

            TapAndTurnApplication.log(Log.INFO,"onOrientationChange", "setting orientation to " + newOrientation.toString());
            screenRotatorOverlay.forceOrientation(newOrientation);
            return;
        }

        if (!newOrientation.equals(screenRotatorOverlay.getCurrentlySetScreenOrientation()) && !newOrientation.equals(AbsoluteOrientation.Enum.Unknown)) {

            TapAndTurnApplication.log(Log.INFO,"onOrientationChange", "displaying button in " + newOrientation.toString());
            orientationButtonOverlay.show(screenRotatorOverlay.getCurrentlySetScreenOrientation(), newOrientation);

            // if the new orientation is different from what the user requested
            handlerScreenOrientation = newOrientation;

        } else {
            // if the new orientation is the same as what the user already requested
            if (orientationButtonOverlay.isActive())
                orientationButtonOverlay.hide();

        }
    }

    @Override
    public void onOverlayPermissionGranted() {
        screenRotatorOverlay.forceOrientation(WindowManagerSensor.query(windowManager));
    }

    @Override
    public void onOverlayPermissionRemoved() {
        screenRotatorOverlay.removeView();
    }


    public class ServiceRotationControlBinder extends Binder {
        RotationControlService getService() {
            return RotationControlService.this;
        }
    }

    public static void Start(Context context) {
        context.startService(new Intent(context, RotationControlService.class));
    }

    public static void Stop(Context context) {
        context.stopService(new Intent(context, RotationControlService.class));
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new ServiceRotationControlBinder();

}
