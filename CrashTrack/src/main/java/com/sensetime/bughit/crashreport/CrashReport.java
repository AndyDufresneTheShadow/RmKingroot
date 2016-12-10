package com.sensetime.bughit.crashreport;

import android.content.Context;

import com.sensetime.bughit.crashreport.bean.AppInfo;
import com.sensetime.bughit.crashreport.bean.DeviceInfo;
import com.sensetime.bughit.crashreport.utils.CrashCallback;
import com.sensetime.bughit.crashreport.utils.CrashHandler;
import com.sensetime.bughit.crashreport.utils.CrashStorage;
import com.sensetime.bughit.crashreport.utils.HttpClient;
import com.sensetime.bughit.crashreport.utils.Log;

/**
 * Created by inx on 2016/8/31.
 */
public class CrashReport {
    private static CrashHandler handler;
    private static Context mContext;

    public static void init(Context appContext, String urlEndPoint, boolean isUploadEnabled, boolean isDebug, CrashCallback callback) {
        mContext = appContext;
        DeviceInfo.init(appContext);
        AppInfo.init(appContext);
        HttpClient.init(urlEndPoint);
        CrashStorage.init(appContext,isUploadEnabled);
        Log.setDEBUG(isDebug);
        handler = new CrashHandler(appContext, isUploadEnabled, callback);
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    public static void setUploadEnabled(Context context,boolean isUploadEnabled) {
            CrashStorage.setUploadEnabled(context, isUploadEnabled);
    }
}
