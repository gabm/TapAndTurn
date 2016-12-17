package com.gabm.tapandturn.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gabm.tapandturn.TapAndTurnApplication;
import com.gabm.tapandturn.settings.SettingsKeys;

/**
 * Created by gabm on 17/12/16.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (TapAndTurnApplication.settings.getBoolean(SettingsKeys.SERVICESTATE, false))
            ServiceRotationControlService.Start(context);
    }
}
