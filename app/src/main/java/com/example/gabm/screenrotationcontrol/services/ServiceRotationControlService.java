package com.example.gabm.screenrotationcontrol.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.gabm.screenrotationcontrol.MainActivity;
import com.example.gabm.screenrotationcontrol.R;
import com.example.gabm.screenrotationcontrol.sensors.PhysicalOrientationSensor;
import com.example.gabm.screenrotationcontrol.ui.DummyOverlay;

/**
 * Created by gabm on 30.10.16.
 */

public class ServiceRotationControlService extends Service implements PhysicalOrientationSensor.OrientationListener{
    private NotificationManager mNM;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.local_service_started;
    private int handlerScreenOrientation;

    @Override
    public void onOrientationChange(int screenOrientation) {
        Log.i("Orientation", String.valueOf(screenOrientation));


        if (buttonLayout.getParent() != null)
            return;

        handlerScreenOrientation = screenOrientation;
        params.screenOrientation = screenOrientation;
        windowManager.addView(buttonLayout, params);

        Handler timeoutHandler = new Handler();
        timeoutHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (buttonLayout.getParent() != null)
                    windowManager.removeView(buttonLayout);
            }
        }, 4000);
    }

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class ServiceRotationControlBinder extends Binder {
        ServiceRotationControlService getService() {
            return ServiceRotationControlService.this;
        }
    }

    private PhysicalOrientationSensor orientationManager;
    private LinearLayout buttonLayout;
    private WindowManager windowManager;
    private ImageButton imageButton;
    private WindowManager.LayoutParams params;

    private DummyOverlay dummyOverlay;
    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

       // Initialize layout params
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        dummyOverlay = new DummyOverlay(getApplicationContext(), windowManager, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT):


        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;
        buttonLayout = (LinearLayout)LayoutInflater.from(getApplicationContext()).inflate(R.layout.rotation_button, null);
        imageButton = (ImageButton)buttonLayout.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (handlerScreenOrientation == orientationManager.getCurScreenOrientation())
                    dummyOverlay.changeOrientation(handlerScreenOrientation);

                windowManager.removeView(buttonLayout);
            }
        });


        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();

        orientationManager = new PhysicalOrientationSensor(getApplicationContext(), SensorManager.SENSOR_DELAY_UI, this);
        orientationManager.enable();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        orientationManager.disable();

        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new ServiceRotationControlBinder();

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.local_service_started);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.sym_def_app_icon)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.local_service_label))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .setPriority(Notification.PRIORITY_MIN)
                .setShowWhen(false)
                .setOngoing(true)
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }
}