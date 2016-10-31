package com.gabm.tapandturn.sensors;

import android.content.Context;

/**
 * Created by gabm on 31.10.16.
 */

public class AndroidOrientationSettings {
    private Context curContext;
    public AndroidOrientationSettings(Context context) {
        curContext = context;
    }

    public int getCurrentOrientation() {
        return curContext.getResources().getConfiguration().orientation;
    }
}
