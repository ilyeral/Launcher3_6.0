package com.android.launcher3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by 25077 on 2019/2/18.
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkStateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "network state changed.");
        switch (getWifiState(context)){
            case 1:
                SettingsActivityWifi.LauncherSettingsFragment.pref_wifi_control.setChecked(false);
                break;
            case 3:
                SettingsActivityWifi.LauncherSettingsFragment.pref_wifi_control.setChecked(true);
                break;
        }
    }

    /**
     * 网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
    public static int getWifiState(Context context) {

        WifiManager mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        return mWifiManager.getWifiState();
    }

}