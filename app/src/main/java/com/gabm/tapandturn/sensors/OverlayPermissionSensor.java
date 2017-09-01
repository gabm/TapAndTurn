package com.gabm.tapandturn.sensors;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gabm on 01/09/17.
 */

public class OverlayPermissionSensor {
    public interface OverlayPermissionListener {
        void onOverlayPermissionGranted();
        void onOverlayPermissionRemoved();
    }

    private List<OverlayPermissionListener> _listeners = new ArrayList<>();
    private static boolean _cachedPermissionState = false;
    private static final OverlayPermissionSensor _instance = new OverlayPermissionSensor();

    private OverlayPermissionSensor() {}

    private static boolean hasPermissionToDrawOverApps(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return Settings.canDrawOverlays(context);
        else
            return true;
    }

    public static OverlayPermissionSensor getInstance() {
        return _instance;
    }

    public void addListener(OverlayPermissionListener listener) {
        _listeners.add(listener);
    }

    public void removeListener(OverlayPermissionListener listener) {
        _listeners.remove(listener);
    }

    private boolean updatePermissionState(boolean newState) {
        if (_cachedPermissionState && !newState) {
            for (OverlayPermissionListener listener : _listeners)
                listener.onOverlayPermissionRemoved();
        } else if (!_cachedPermissionState && newState) {
            for (OverlayPermissionListener listener : _listeners)
                listener.onOverlayPermissionGranted();
        }
        _cachedPermissionState = newState;
        return newState;
    }

    public boolean query(Context context) {
        return updatePermissionState(hasPermissionToDrawOverApps(context));
    }

}
