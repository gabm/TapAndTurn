package com.gabm.tapandturn.sensors;

import android.view.Surface;
import android.view.WindowManager;

import com.gabm.tapandturn.AbsoluteOrientation;

/**
 * Created by gabm on 23/05/17.
 */

public class WindowManagerSensor {

    public static AbsoluteOrientation query(WindowManager windowManager) {
        int rawResult = windowManager.getDefaultDisplay().getRotation();

        if (rawResult == Surface.ROTATION_90)
            return new AbsoluteOrientation(AbsoluteOrientation.Enum.Landscape);

        if (rawResult == Surface.ROTATION_180)
            return new AbsoluteOrientation(AbsoluteOrientation.Enum.Reverse_Portrait);

        if (rawResult == Surface.ROTATION_270)
            return new AbsoluteOrientation(AbsoluteOrientation.Enum.Reverse_Landscape);

        return new AbsoluteOrientation(AbsoluteOrientation.Enum.Portrait);
    }
}
