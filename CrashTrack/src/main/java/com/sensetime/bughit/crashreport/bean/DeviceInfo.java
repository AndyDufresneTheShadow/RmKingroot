package com.sensetime.bughit.crashreport.bean;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.io.File;

/**
 * Created by inx on 2016/8/29.
 */
public class DeviceInfo {

    private final String imei;
    private final String manufacturer;
    private final String brand;
    private final String deviceName;
    private final Integer apiLevel;
    private final String abi;
    private final String osVersion;
    private final String osBuild;
    private final Boolean isRooted;

    private DeviceInfo(Context appContext) {
        imei = getAndroidImei(appContext);
        manufacturer = Build.MANUFACTURER;
        brand = Build.BRAND;
        deviceName = Build.MODEL;
        apiLevel = Build.VERSION.SDK_INT;
        osVersion = Build.VERSION.RELEASE;
        osBuild = Build.DISPLAY;
        isRooted = getIsRooted();
        abi = getABI();
    }

    /**
     * get the cpu_abi by Build.SUPPORTED_ABIS from larger version of Android 5.1 or by Build.CPU_ABI from other versions
     */
    private static String getABI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Build.SUPPORTED_ABIS[0];
        }
        return Build.CPU_ABI;
    }

    private static final transient String[] ROOT_INDICATORS = new String[]{
            // Common binaries
            "/system/xbin/su",
            "/system/bin/su",
            // < Android 5.0
            "/system/app/Superuser.apk",
            "/system/app/SuperSU.apk",
            // >= Android 5.0
            "/system/app/Superuser",
            "/system/app/SuperSU",
            // Fallback
            "/system/xbin/daemonsu"
    };

    /**
     * Check if the current Android device is rooted
     */
    private static Boolean getIsRooted() {
        if (android.os.Build.TAGS != null && android.os.Build.TAGS.contains("test-keys"))
            return true;

        try {
            for (String candidate : ROOT_INDICATORS) {
                if (new File(candidate).exists())
                    return true;
            }
        } catch (Exception ignore) {
            return false;
        }
        return false;
    }

    /**
     * Get the unique device imei for the current Android device
     */
    private static String getAndroidImei(Context appContext) {
        ContentResolver cr = appContext.getContentResolver();
        return Settings.Secure.getString(cr, Settings.Secure.ANDROID_ID);
    }

    private static transient final String TAG = "DeviceInfo";
    private static transient DeviceInfo INSTANCE = null;

    public static void init(Context appContext) {
        if (!(appContext instanceof Application)) {
            Log.w(TAG, "this class should init in Application");
        }
        INSTANCE = new DeviceInfo(appContext);
    }

    public static DeviceInfo getInstance() {
        if (INSTANCE == null) {
            Log.e(TAG, "instance is null");
        }
        return INSTANCE;
    }


    public String getImei() {
        return imei;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getBrand() {
        return brand;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public Integer getApiLevel() {
        return apiLevel;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getOsBuild() {
        return osBuild;
    }

    public Boolean isRooted() {
        return isRooted;
    }

    public String getAbi() {
        return abi;
    }

}
