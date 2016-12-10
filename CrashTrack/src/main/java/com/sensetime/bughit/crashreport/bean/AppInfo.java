package com.sensetime.bughit.crashreport.bean;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by inx on 2016/8/29.
 */
public class AppInfo {

    private String pkgName;
    private Integer versionCode;
    private String versionName;
    private Boolean isMinifyEnabled;

    private AppInfo(Context context) {
        pkgName = context.getPackageName();
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
            versionCode = pInfo.versionCode;
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private static final transient String TAG = "AppInfo";
    public static transient AppInfo INSTANCE = null;

    public static void init(Context appContext) {
        if (!(appContext instanceof Application)) {
            Log.w(TAG, "this class should init in Application");
        }
        INSTANCE = new AppInfo(appContext);
    }

    public static AppInfo getInstance() {
        if (INSTANCE == null) {
            Log.e(TAG, "instance is null");
        }
        return INSTANCE;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public boolean isMinifyEnabled() {
        return isMinifyEnabled;
    }

    public void setMinifyEnabled(boolean minifyEnabled) {
        isMinifyEnabled = minifyEnabled;
    }
}
