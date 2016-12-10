package com.sensetime.bughit.crashreport.bean;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.util.Log;


/**
 * Created by inx on 2016/8/31.
 */

public class DeviceState {
    private final Long freeMemory;
    private final Long maxMemory;
    private final String orientation;
    private final Float batteryLevel;
    private final Long freeDisk;
    private final Boolean charging;
    private final String locationStatus;
    private final String networkAccess;

    public DeviceState(Context context) {

        freeMemory = getFreeMemory();
        maxMemory = getMaxMemory();
        orientation = getOrientation(context);
        batteryLevel = getBatteryLevel(context);
        freeDisk = getFreeDisk();
        charging = isCharging(context);
        locationStatus = getLocationStatus(context);
        networkAccess = getNetworkAccess(context);
    }

    /**
     * Get the amount of memory remaining that the VM can allocate
     */
    private static Long getFreeMemory() {
        if (Runtime.getRuntime().maxMemory() != Long.MAX_VALUE) {
            return Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory();
        } else {
            return Runtime.getRuntime().freeMemory();
        }
    }

    /**
     * Get the total memory available on the current Android device, in bytes
     */
    private static Long getMaxMemory() {
        if (Runtime.getRuntime().maxMemory() != Long.MAX_VALUE) {
            return Runtime.getRuntime().maxMemory();
        } else {
            return Runtime.getRuntime().totalMemory();
        }
    }

    /**
     * Get the device orientation, eg. "landscape"
     */
    private static String getOrientation(Context appContext) {
        String orientation = null;
        switch (appContext.getResources().getConfiguration().orientation) {
            case android.content.res.Configuration.ORIENTATION_LANDSCAPE:
                orientation = "landscape";
                break;
            case android.content.res.Configuration.ORIENTATION_PORTRAIT:
                orientation = "portrait";
                break;
            default:
                orientation = null;
                break;
        }
        return orientation;
    }

    /**
     * Get the current battery charge level, eg 0.3
     */
    private static Float getBatteryLevel(Context appContext) {
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = appContext.registerReceiver(null, ifilter);

            return batteryStatus.getIntExtra("level", -1) / (float) batteryStatus.getIntExtra("scale", -1);
        } catch (Exception e) {
            Log.w(TAG, "Could not get batteryLevel");
        }
        return null;
    }

    /**
     * Get the free disk space on the smallest disk
     */
    private static Long getFreeDisk() {
        try {
            StatFs externalStat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long externalBytesAvailable = (long) externalStat.getBlockSize() * (long) externalStat.getBlockCount();

            StatFs internalStat = new StatFs(Environment.getDataDirectory().getPath());
            long internalBytesAvailable = (long) internalStat.getBlockSize() * (long) internalStat.getBlockCount();

            return Math.min(internalBytesAvailable, externalBytesAvailable);
        } catch (Exception e) {
            Log.w(TAG, "Could not get freeDisk");
        }
        return null;
    }

    /**
     * Is the device currently charging/full battery?
     */
    private static Boolean isCharging(Context appContext) {
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = appContext.registerReceiver(null, ifilter);

            int status = batteryStatus.getIntExtra("status", -1);
            return (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL);
        } catch (Exception e) {
            Log.w(TAG, "Could not get charging status");
        }
        return null;
    }

    /**
     * Get the current status of location services
     */
    private static String getLocationStatus(Context appContext) {
        try {
            ContentResolver cr = appContext.getContentResolver();
            String providersAllowed = Settings.Secure.getString(cr, Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (providersAllowed != null && providersAllowed.length() > 0) {
                return "allowed";
            } else {
                return "disallowed";
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not get locationStatus");
        }
        return null;
    }

    /**
     * Get the current status of network access, eg "cellular"
     */
    private static String getNetworkAccess(Context appContext) {
        try {
            ConnectivityManager cm = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                if (activeNetwork.getType() == 1) {
                    return "wifi";
                } else if (activeNetwork.getType() == 9) {
                    return "ethernet";
                } else {
                    // default cellular
                    return "cellular";
                }
            } else {
                return "none";
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not get network access information, we recommend granting the 'android.permission.ACCESS_NETWORK_STATE' permission");
        }
        return null;
    }


    private static final String TAG = "DeviceState";
}

