/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.launcher3.util.WifiAdminUtil;
import com.android.launcher3.widgets.MyPreferenceCategory;

/**
 * Settings activity for Launcher. Currently implements the following setting: Allow rotation
 */
public class SettingsActivityWifi extends Activity {
    // 要申请的权限
    private String[] permissions = {android.Manifest.permission.CHANGE_WIFI_STATE};
    private AlertDialog dialog;
    public static WifiAdminUtil wifiAdminUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity_wifi);
        TextView t=(TextView)findViewById(R.id.title);
        t.setText("Wifi设置");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.setting_list, new LauncherSettingsFragment())
                .commit();

        wifiAdminUtil=new WifiAdminUtil(this);
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                showDialogTipUserRequestPermission();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    // 提示用户该请求权限的弹出框
    private void showDialogTipUserRequestPermission() {

        new AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
                .setMessage("由于支付宝需要获取存储空间，为你存储个人信息；\n否则，您将无法正常使用支付宝")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestPermission();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }
    // 开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }
    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("permissions",""+permissions.length);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    } else
                        finish();
                } else {
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    // 提示用户去应用设置界面手动开启权限

    private void showDialogTipUserGoToAppSettting() {

        dialog = new AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
                .setMessage("请在-应用设置-权限-中，允许支付宝使用存储权限来保存用户数据")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, 123);
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查该权限是否已经获取
                int i = ContextCompat.checkSelfPermission(this, permissions[0]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 提示用户应该去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSettting();
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    /**
     * This fragment shows the launcher preferences.
     */
    public static class LauncherSettingsFragment extends PreferenceFragment
            implements OnPreferenceChangeListener {
        static SwitchPreference pref_wifi_control;
        static MyPreferenceCategory bar2;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.e("LauncherSettings","onCreate");
            addPreferencesFromResource(R.xml.settings_preferences_wifi);

            initBar2();
            initWifiControlSetting();
            initWifiControllerSetting();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            ListView lv = (ListView)view.findViewById(android.R.id.list);
            lv.setBackgroundColor(Color.argb(255,255, 255, 255));
            return view;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Bundle extras = new Bundle();
            extras.putBoolean(LauncherSettings.Settings.EXTRA_VALUE, (Boolean) newValue);
            getActivity().getContentResolver().call(
                    LauncherSettings.Settings.CONTENT_URI,
                    LauncherSettings.Settings.METHOD_SET_BOOLEAN,
                    preference.getKey(), extras);
            switch (preference.getKey()){
                case Utilities.ALLOW_WIFI_CONTROLLER_KEY:
                    if((boolean)newValue==false){
                        PreferenceScreen screen = getPreferenceScreen();
                        screen.removePreference( bar2);
                    }else{
//                        initBar2();
//                        initWifiControlSetting();
                        PreferenceScreen screen = getPreferenceScreen();
                        screen.addPreference( bar2);
                    }
                    break;
                case Utilities.WIFI_CONTROL_KEY:
                    if((boolean)newValue==false){
                        wifiAdminUtil.closeWifi();
                    }else{
                        wifiAdminUtil.openWifi();
                    }
                    break;
            }
            return true;
        }

        public void initWifiControllerSetting(){
            SwitchPreference pref = (SwitchPreference) findPreference(
                    Utilities.ALLOW_WIFI_CONTROLLER_KEY);
            pref.setPersistent(true);

            Bundle extras = new Bundle();
            extras.putBoolean(LauncherSettings.Settings.EXTRA_DEFAULT_VALUE, false);
            Bundle value = getActivity().getContentResolver().call(
                    LauncherSettings.Settings.CONTENT_URI,
                    LauncherSettings.Settings.METHOD_GET_BOOLEAN,
                    Utilities.ALLOW_WIFI_CONTROLLER_KEY, extras);
            pref.setChecked(value.getBoolean(LauncherSettings.Settings.EXTRA_VALUE));
            pref.setOnPreferenceChangeListener(this);
            if(value.getBoolean(LauncherSettings.Settings.EXTRA_VALUE)){
                PreferenceScreen screen = getPreferenceScreen();
                screen.addPreference( bar2);
            }else{
                PreferenceScreen screen = getPreferenceScreen();
                screen.removePreference( bar2);
            }
        }
        public void initBar2(){
            bar2 = (MyPreferenceCategory) findPreference(
                    "bar2");
        }
        public void initWifiControlSetting(){
            pref_wifi_control = (SwitchPreference) findPreference(
                    Utilities.WIFI_CONTROL_KEY);
            pref_wifi_control.setPersistent(true);
            pref_wifi_control.setChecked(wifiAdminUtil.getWifiIsOpen());
            pref_wifi_control.setOnPreferenceChangeListener(this);
        }
    }
}
