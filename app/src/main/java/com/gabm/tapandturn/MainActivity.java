package com.gabm.tapandturn;

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

import com.gabm.tapandturn.services.ServiceRotationControlService;
import com.gabm.tapandturn.settings.SettingsKeys;
import com.gabm.tapandturn.settings.SettingsManager;

public class MainActivity extends AppCompatActivity implements Switch.OnCheckedChangeListener, Button.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private Switch serviceStateSwitch;
    private Switch useReversePortraitSwitch;
    private Button requestPermissionButton;

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
            setPermissionGranted(hasPermissionToDrawOverApps());
    }

    private boolean hasPermissionToDrawOverApps() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return Settings.canDrawOverlays(this);
        else
            return true;
    }

    @Override
    protected void onStop() {
        Log.i("Main", "stopped");
        super.onStop();

        final SettingsManager appSettings = TapAndTurnApplication.settings;
        appSettings.startEditMode();
        appSettings.putBoolean(SettingsKeys.PREFS_KEY_SERVICESTATE, serviceStateSwitch.isChecked());
        appSettings.putBoolean(SettingsKeys.PREFS_KEY_USE_REVERSE_PORTRAIT, useReversePortraitSwitch.isChecked());
        appSettings.finishEditMode();
    }

    @Override
    protected void onStart() {
        Log.i("Main", "started");
        super.onStart();
        setServiceState(TapAndTurnApplication.settings.getBoolean(SettingsKeys.PREFS_KEY_SERVICESTATE, false));
        setPermissionGranted(hasPermissionToDrawOverApps());

        iconSizeSeekbar.setProgress(TapAndTurnApplication.settings.getInt(SettingsKeys.PREFS_KEY_ICONSIZE, 40));
        iconTimeoutSeekbar.setProgress(TapAndTurnApplication.settings.getInt(SettingsKeys.PREFS_KEY_ICONTIMEOUT, 4000));
        useReversePortraitSwitch.setChecked(TapAndTurnApplication.settings.getBoolean(SettingsKeys.PREFS_KEY_USE_REVERSE_PORTRAIT, false));
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == serviceStateSwitch)
            setServiceState(b);

        if (compoundButton == useReversePortraitSwitch)
            TapAndTurnApplication.settings.putBoolean(SettingsKeys.PREFS_KEY_USE_REVERSE_PORTRAIT, b);
    }

    private void setServiceState(boolean started) {
        if (started) {
            if (hasPermissionToDrawOverApps()) {
                startService(new Intent(this, ServiceRotationControlService.class));
                serviceStateSwitch.setChecked(true);
            } else {
                Toast.makeText(getApplicationContext(), R.string.permission_missing, Toast.LENGTH_SHORT).show();
                serviceStateSwitch.setChecked(false);
            }
        } else {
            stopService(new Intent(this, ServiceRotationControlService.class));
            serviceStateSwitch.setChecked(false);
        }

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
        if (!hasPermissionToDrawOverApps())
            requestPermission();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar == iconSizeSeekbar) {
            iconSizeTextView.setText("Icon Size: " + seekBar.getProgress() + "dp");
            TapAndTurnApplication.settings.putInt(SettingsKeys.PREFS_KEY_ICONSIZE, seekBar.getProgress());
        } else if  (seekBar == iconTimeoutSeekbar) {
            iconTimeoutTextView.setText("Icon Timeout: " + seekBar.getProgress() + "ms");
            TapAndTurnApplication.settings.putInt(SettingsKeys.PREFS_KEY_ICONTIMEOUT, seekBar.getProgress());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
