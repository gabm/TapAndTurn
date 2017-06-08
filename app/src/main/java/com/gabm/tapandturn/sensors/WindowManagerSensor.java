package com.gabm.tapandturn.sensors;

import android.content.Context;
import android.content.res.Configuration;
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

    public static AbsoluteOrientation queryDefaultOrientation(WindowManager windowManager, Configuration configuration) {
        int rotation = windowManager.getDefaultDisplay().getRotation();
        int orientation = configuration.orientation;

        if( (((rotation == Surface.ROTATION_0) || (rotation == Surface.ROTATION_180)) && (orientation == Configuration.ORIENTATION_LANDSCAPE)) ||
                (((rotation == Surface.ROTATION_90) || (rotation == Surface.ROTATION_270)) && (orientation == Configuration.ORIENTATION_PORTRAIT)))
        {
            return new AbsoluteOrientation(AbsoluteOrientation.Enum.Landscape); //TABLET
        }

        return new AbsoluteOrientation(AbsoluteOrientation.Enum.Portrait); //PHONE


    }
}
