package com.sensetime.bughit.crashreport.utils;


import com.sensetime.bughit.BuildConfig;

/**
 * Created by inx on 2016/9/7.
 */
public class Log {
    public static boolean DEBUG = BuildConfig.DEBUG;

    public static void d(String TAG, String msg) {
        if (DEBUG) {
            android.util.Log.d(TAG, msg);
        }
    }
    public static void i(String TAG, String msg) {
        if (DEBUG) {
            android.util.Log.i(TAG, msg);
        }
    }
    public static void w(String TAG, String msg) {
        if (DEBUG) {
            android.util.Log.w(TAG, msg);
        }
    }
    public static void v(String TAG, String msg) {
        if (DEBUG) {
            android.util.Log.v(TAG, msg);
        }
    }
    public static void e(String TAG, String msg) {
        if (DEBUG) {
            android.util.Log.e(TAG, msg);
        }
    }

    public static void setDEBUG(boolean isDebug) {
        DEBUG = isDebug;
    }
}
