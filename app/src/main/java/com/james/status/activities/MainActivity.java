package com.james.status.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.james.status.R;
import com.james.status.services.StatusService;
import com.james.status.utils.ImageUtils;
import com.james.status.utils.PreferenceUtils;
import com.james.status.utils.StaticUtils;
import com.james.status.views.CustomImageView;

public class MainActivity extends AppCompatActivity {

    AppBarLayout appbar;
    AppCompatButton service;
    FloatingActionButton fab;

    SwitchCompat amPm, autoBarColor, darkIcons;
    View barColor;
    CustomImageView barColorView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!StaticUtils.isAccessibilityGranted(this) || !StaticUtils.isNotificationGranted(this) || !StaticUtils.isPermissionsGranted(this))
            startActivity(new Intent(this, StartActivity.class));

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        appbar = (AppBarLayout) findViewById(R.id.appbar);
        service = (AppCompatButton) findViewById(R.id.service);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        amPm = (SwitchCompat) findViewById(R.id.amPmEnabled);
        autoBarColor = (SwitchCompat) findViewById(R.id.autoBarColorEnabled);
        barColor = findViewById(R.id.pickBarColor);
        barColorView = (CustomImageView) findViewById(R.id.pickBarColor_color);
        darkIcons = (SwitchCompat) findViewById(R.id.darkIconsEnabled);

        fab.setImageDrawable(ImageUtils.getVectorDrawable(this, R.drawable.ic_expand));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appbar.setExpanded(false, true);
            }
        });

        Boolean enabled = PreferenceUtils.getBooleanPreference(this, PreferenceUtils.PreferenceIdentifier.STATUS_ENABLED);
        service.setText((enabled != null && enabled) || StaticUtils.isStatusServiceRunning(this) ? R.string.service_stop : R.string.service_start);
        service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StaticUtils.isStatusServiceRunning(MainActivity.this)) {
                    PreferenceUtils.putPreference(MainActivity.this, PreferenceUtils.PreferenceIdentifier.STATUS_ENABLED, false);
                    service.setText(R.string.service_start);

                    Intent intent = new Intent(StatusService.ACTION_STOP);
                    intent.setClass(MainActivity.this, StatusService.class);
                    stopService(intent);
                } else {
                    PreferenceUtils.putPreference(MainActivity.this, PreferenceUtils.PreferenceIdentifier.STATUS_ENABLED, true);
                    service.setText(R.string.service_stop);

                    Intent intent = new Intent(StatusService.ACTION_START);
                    intent.setClass(MainActivity.this, StatusService.class);
                    startService(intent);
                }
            }
        });

        Boolean isAmPmEnabled = PreferenceUtils.getBooleanPreference(this, PreferenceUtils.PreferenceIdentifier.STATUS_CLOCK_AMPM);
        amPm.setChecked(isAmPmEnabled == null || isAmPmEnabled);
        amPm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PreferenceUtils.putPreference(MainActivity.this, PreferenceUtils.PreferenceIdentifier.STATUS_CLOCK_AMPM, b);

                if (StaticUtils.isStatusServiceRunning(MainActivity.this)) {
                    Intent intent = new Intent(StatusService.ACTION_START);
                    intent.setClass(MainActivity.this, StatusService.class);
                    startService(intent);
                }
            }
        });

        Boolean isStatusColorAuto = PreferenceUtils.getBooleanPreference(this, PreferenceUtils.PreferenceIdentifier.STATUS_COLOR_AUTO);
        autoBarColor.setChecked(isStatusColorAuto == null || isStatusColorAuto);
        autoBarColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PreferenceUtils.putPreference(MainActivity.this, PreferenceUtils.PreferenceIdentifier.STATUS_COLOR_AUTO, b);
                barColor.setVisibility(b ? View.GONE : View.VISIBLE);

                if (StaticUtils.isStatusServiceRunning(MainActivity.this)) {
                    Intent intent = new Intent(StatusService.ACTION_START);
                    intent.setClass(MainActivity.this, StatusService.class);
                    startService(intent);
                }
            }
        });

        barColor.setVisibility(isStatusColorAuto == null || isStatusColorAuto ? View.GONE : View.VISIBLE);

        Integer statusBarColor = PreferenceUtils.getIntegerPreference(this, PreferenceUtils.PreferenceIdentifier.STATUS_COLOR);
        if (statusBarColor == null) statusBarColor = Color.BLACK;
        barColorView.setImageDrawable(new ColorDrawable(statusBarColor));

        barColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Boolean isDarkModeEnabled = PreferenceUtils.getBooleanPreference(this, PreferenceUtils.PreferenceIdentifier.STATUS_DARK_ICONS);
        darkIcons.setChecked(isDarkModeEnabled == null || isDarkModeEnabled);
        darkIcons.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PreferenceUtils.putPreference(MainActivity.this, PreferenceUtils.PreferenceIdentifier.STATUS_DARK_ICONS, b);

                if (StaticUtils.isStatusServiceRunning(MainActivity.this)) {
                    Intent intent = new Intent(StatusService.ACTION_START);
                    intent.setClass(MainActivity.this, StatusService.class);
                    startService(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_setup).setIcon(ImageUtils.getVectorDrawable(this, R.drawable.ic_setup));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setup:
                startActivity(new Intent(this, StartActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
