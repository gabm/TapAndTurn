package com.gabm.tapandturn.ui;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gabm.tapandturn.R;
import com.gabm.tapandturn.TapAndTurnApplication;
import com.gabm.tapandturn.sensors.OverlayPermissionSensor;
import com.gabm.tapandturn.services.RotationControlService;
import com.gabm.tapandturn.settings.SettingsKeys;
import com.gabm.tapandturn.settings.SettingsManager;

public class MainActivity extends AppCompatActivity implements Switch.OnCheckedChangeListener, Button.OnClickListener {
    private Switch serviceStateSwitch;
    private Button requestOverlayPermissionButton;
    private Switch loggingStateSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        serviceStateSwitch = (Switch) findViewById(R.id.service_state_switch);
        serviceStateSwitch.setOnCheckedChangeListener(this);

        loggingStateSwitch = (Switch)findViewById(R.id.logging_switch);
        loggingStateSwitch.setOnCheckedChangeListener(this);;

        requestOverlayPermissionButton = (Button) findViewById(R.id.request_button);
        requestOverlayPermissionButton.setOnClickListener(this);

        final ScrollView scrollview = ((ScrollView) findViewById(R.id.scrollview_content_main));
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesName(SettingsManager.SettingsName);
            addPreferencesFromResource(R.xml.preferences);
        }


        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);


            if (getView() != null) {

                ListView listView = (ListView) getView().findViewById(android.R.id.list);
                Adapter adapter = listView.getAdapter();

                if (adapter != null) {
                    int height = 0;
                    //int height = listView.getPaddingTop() + listView.getPaddingBottom();

                    for (int i = 0; i < adapter.getCount(); i++) {
                        View item = adapter.getView(i, null, listView);

                        item.measure(0, 0);
                        height += item.getMeasuredHeight();
                    }

                    LinearLayout frame = (LinearLayout) getActivity().findViewById(R.id.preference_fragment); //Modify this for your fragment

                    ViewGroup.LayoutParams param = frame.getLayoutParams();
                    param.height = height + (listView.getDividerHeight() * adapter.getCount());
                    frame.setLayoutParams(param);
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private Spanned renderHTML(int inputid, Object... formatArgs) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)  {
            return Html.fromHtml(getString(inputid, formatArgs), Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(getString(inputid,formatArgs));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            LayoutInflater factory = LayoutInflater.from( this );

            View titleView = factory.inflate(R.layout.info_screen_header, null);

            AlertDialog.Builder adb = new AlertDialog.Builder( this )
                    .setCustomTitle(titleView)
                    .setPositiveButton( "Ok", null );
            View tvs = factory.inflate( R.layout.info_screen, null );
            if( tvs != null ) {
                adb.setView( tvs );
                TextView tv = (TextView)tvs.findViewById(R.id.text_view);
                tv.setText(renderHTML(R.string.about_text, getPackageVersion()));
                tv.setMovementMethod(LinkMovementMethod.getInstance());
            } else
                adb.setMessage( "" );
            adb.create().show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** code to post/handler request for permission */
    public final static int REQUEST_OVERLAY_CODE = 5463;
    public final static int REQUEST_WRITE_PERMISSION = 5464;

    public void requestOverlayPermission() {
        /* check if we already  have permission to draw over other apps */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                /* if not construct intent to request permission */
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                /* request permission via start activity for result */
                startActivityForResult(intent, REQUEST_OVERLAY_CODE);
            }
        }
    }

    public boolean hasWritePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions, REQUEST_WRITE_PERMISSION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if (requestCode == REQUEST_OVERLAY_CODE)
            setOverlayPermissionGranted(OverlayPermissionSensor.getInstance().query(this));
        else if (requestCode == REQUEST_WRITE_PERMISSION) {
            TapAndTurnApplication.setLoggingEnabled(hasWritePermission());
            setServiceStateSwitch(hasWritePermission());
        }
    }


    @Override
    protected void onStop() {
        Log.i("Main", "stopped");
        super.onStop();
    }

    @Override
    protected void onStart() {
        Log.i("Main", "started");
        super.onStart();


        setServiceStateSwitch(isServiceRunning(RotationControlService.class));
        setOverlayPermissionGranted(OverlayPermissionSensor.getInstance().query(this));
        setLoggingStateSwitch(TapAndTurnApplication.settings.getBoolean(SettingsKeys.LOGGING_ENABLED, false));
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == serviceStateSwitch)
            setServiceStateSwitch(applyNewServiceState(b));
        else if (compoundButton == loggingStateSwitch) {
            if (b) {
                if (!hasWritePermission()) {
                    requestWritePermission();
                } else {
                    TapAndTurnApplication.setLoggingEnabled(true);
                }
            }
            else {
                TapAndTurnApplication.setLoggingEnabled(false);
            }
        }

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

        return isServiceRunning(RotationControlService.class);
    }

    private void startupService() {
        if (isServiceRunning(RotationControlService.class))
            return;

        if (!OverlayPermissionSensor.getInstance().query(this)) {
            Toast.makeText(getApplicationContext(), R.string.permission_missing, Toast.LENGTH_SHORT).show();
            return;
        }

        RotationControlService.Start(this);
    }

    private void shutdownService() {
        if (!isServiceRunning(RotationControlService.class))
            return;

        RotationControlService.Stop(this);
    }

    private void setServiceStateSwitch(boolean started) {
        serviceStateSwitch.setChecked(started);
    }

    private void setLoggingStateSwitch(boolean on) {
        loggingStateSwitch.setChecked(on);
    }

    private void setOverlayPermissionGranted(boolean granted) {
        requestOverlayPermissionButton.setEnabled(!granted);

        if (granted)
            requestOverlayPermissionButton.setText(R.string.permission_granted);
        else
            requestOverlayPermissionButton.setText(R.string.request_permission);
    }

    @Override
    public void onClick(View view) {
        if (!OverlayPermissionSensor.getInstance().query(this))
            requestOverlayPermission();
    }


    private String getPackageVersion() {
        PackageInfo pi = null;
        try {
            pi = getPackageManager().getPackageInfo( getPackageName(), 0 );
            return pi.versionName;
        } catch( PackageManager.NameNotFoundException e ) {
            Log.e( "d", "Package name not found", e );
            return "";
        }
    }
}
