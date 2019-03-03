package com.android.launcher3;

import android.content.Intent;
import android.graphics.Color;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.launcher3.widgets.MyPreferenceCategory;

public class SettingsActivityBackups extends AppCompatActivity {
    static Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity_backups);
        TextView t=(TextView)findViewById(R.id.title);
        t.setText("备份信息");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        Intent intent = getIntent();
        bundle = intent.getExtras();
    }
}
