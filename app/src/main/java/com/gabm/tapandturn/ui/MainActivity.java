package com.gabm.tapandturn.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gabm.tapandturn.R;
import com.gabm.tapandturn.TapAndTurnApplication;
import com.gabm.tapandturn.services.ServiceRotationControlService;
import com.gabm.tapandturn.settings.SettingsKeys;
import com.gabm.tapandturn.settings.SettingsManager;

public class MainActivity extends AppCompatActivity implements Switch.OnCheckedChangeListener, Button.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private Switch serviceStateSwitch;
    private Switch useReversePortraitSwitch;
    private Switch autoStartBootSwtich;
    private Switch leftHandedModeSwitch;
    private Button requestPermissionButton;
    private Switch restoreDefaultOnScreenOff;

    private SeekBar iconSizeSeekbar;
    private TextView iconSizeTextView;

    private SeekBar iconTimeoutSeekbar;
    private TextView iconTimeoutTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        iconSizeSeekbar = (SeekBar)findViewById(R.id.icon_size_seekbar);
        iconSizeTextView = (TextView)findViewById(R.id.icon_size_text);

        iconTimeoutSeekbar = (SeekBar)findViewById(R.id.icon_timeout_seekbar);
        iconTimeoutTextView = (TextView)findViewById(R.id.icon_timeout_text);

        setSupportActionBar(toolbar);

        serviceStateSwitch = (Switch)findViewById(R.id.service_state_switch);
        serviceStateSwitch.setOnCheckedChangeListener(this);

        useReversePortraitSwitch = (Switch)findViewById(R.id.use_reverse_portrait_switch);
        useReversePortraitSwitch.setOnCheckedChangeListener(this);

        autoStartBootSwtich = (Switch)findViewById(R.id.start_on_boot_switch);
        autoStartBootSwtich.setOnCheckedChangeListener(this);

        leftHandedModeSwitch = (Switch)findViewById(R.id.left_handed_mode_switch);
        leftHandedModeSwitch.setOnCheckedChangeListener(this);

        restoreDefaultOnScreenOff = (Switch)findViewById(R.id.restore_default_orientation_screen_off);
        restoreDefaultOnScreenOff.setOnClickListener(this);

        requestPermissionButton = (Button)findViewById(R.id.request_button);
        requestPermissionButton.setOnClickListener(this);

        iconSizeSeekbar.setOnSeekBarChangeListener(this);
        iconTimeoutSeekbar.setOnSeekBarChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** code to post/handler request for permission */
    public final static int REQUEST_CODE = 5463;

    public void requestPermission() {
        /** check if we already  have permission to draw over other apps */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                /** if not construct intent to request permission */
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                /** request permission via start activity for result */
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if (requestCode == REQUEST_CODE)
            setPermissionGranted(TapAndTurnApplication.hasPermissionToDrawOverApps(this));
    }

    @Override
    protected void onStop() {
        Log.i("Main", "stopped");
        super.onStop();

        final SettingsManager appSettings = TapAndTurnApplication.settings;
        appSettings.startEditMode();
        appSettings.putBoolean(SettingsKeys.USE_REVERSE_PORTRAIT, useReversePortraitSwitch.isChecked());
        appSettings.putBoolean(SettingsKeys.START_ON_BOOT, autoStartBootSwtich.isChecked());
        appSettings.putBoolean(SettingsKeys.LEFT_HANDED_MODE, leftHandedModeSwitch.isChecked());
        appSettings.putBoolean(SettingsKeys.RESTORE_DEFAULT_ON_SCREEN_OFF, restoreDefaultOnScreenOff.isChecked());
        appSettings.finishEditMode();
    }

    @Override
    protected void onStart() {
        Log.i("Main", "started");
        super.onStart();
        setServiceStateSwitch(isServiceRunning(ServiceRotationControlService.class));
        setPermissionGranted(TapAndTurnApplication.hasPermissionToDrawOverApps(this));

        iconSizeSeekbar.setProgress(TapAndTurnApplication.settings.getInt(SettingsKeys.ICONSIZE, 62));
        iconTimeoutSeekbar.setProgress(TapAndTurnApplication.settings.getInt(SettingsKeys.ICONTIMEOUT, 2000));
        useReversePortraitSwitch.setChecked(TapAndTurnApplication.settings.getBoolean(SettingsKeys.USE_REVERSE_PORTRAIT, false));
        autoStartBootSwtich.setChecked(TapAndTurnApplication.settings.getBoolean(SettingsKeys.START_ON_BOOT, false));
        leftHandedModeSwitch.setChecked(TapAndTurnApplication.settings.getBoolean(SettingsKeys.LEFT_HANDED_MODE, false));
        restoreDefaultOnScreenOff.setChecked(TapAndTurnApplication.settings.getBoolean(SettingsKeys.RESTORE_DEFAULT_ON_SCREEN_OFF, true));
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == serviceStateSwitch)
            setServiceStateSwitch(applyNewServiceState(b));

        if (compoundButton == useReversePortraitSwitch)
            TapAndTurnApplication.settings.putBoolean(SettingsKeys.USE_REVERSE_PORTRAIT, b);

        if (compoundButton == autoStartBootSwtich)
            TapAndTurnApplication.settings.putBoolean(SettingsKeys.START_ON_BOOT, b);

        if (compoundButton == leftHandedModeSwitch)
            TapAndTurnApplication.settings.putBoolean(SettingsKeys.LEFT_HANDED_MODE, b);

        if (compoundButton == restoreDefaultOnScreenOff)
            TapAndTurnApplication.settings.putBoolean(SettingsKeys.RESTORE_DEFAULT_ON_SCREEN_OFF, b);
    }

    // borrowed from: https://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private boolean applyNewServiceState(boolean newState) {
        if (newState)
            startupService();
        else
            shutdownService();

        return isServiceRunning(ServiceRotationControlService.class);
    }

    private void startupService() {
        if (isServiceRunning(ServiceRotationControlService.class))
            return;

        if (!TapAndTurnApplication.hasPermissionToDrawOverApps(this)) {
            Toast.makeText(getApplicationContext(), R.string.permission_missing, Toast.LENGTH_SHORT).show();
            return;
        }

        ServiceRotationControlService.Start(this);
    }

    private void shutdownService() {
        if (!isServiceRunning(ServiceRotationControlService.class))
            return;

        ServiceRotationControlService.Stop(this);
    }

    private void setServiceStateSwitch(boolean started) {
        serviceStateSwitch.setChecked(started);
    }

    private void setPermissionGranted(boolean granted) {
        requestPermissionButton.setEnabled(!granted);

        if (granted)
            requestPermissionButton.setText(R.string.permission_granted);
        else
            requestPermissionButton.setText(R.string.request_permission);
    }

    @Override
    public void onClick(View view) {
        if (!TapAndTurnApplication.hasPermissionToDrawOverApps(this))
            requestPermission();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar == iconSizeSeekbar) {
            iconSizeTextView.setText("Icon Size: " + seekBar.getProgress() + "dp");
            TapAndTurnApplication.settings.putInt(SettingsKeys.ICONSIZE, seekBar.getProgress());
        } else if  (seekBar == iconTimeoutSeekbar) {
            iconTimeoutTextView.setText("Icon Timeout: " + seekBar.getProgress() + "ms");
            TapAndTurnApplication.settings.putInt(SettingsKeys.ICONTIMEOUT, seekBar.getProgress());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
