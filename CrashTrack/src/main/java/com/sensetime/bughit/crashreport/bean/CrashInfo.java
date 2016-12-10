package com.sensetime.bughit.crashreport.bean;


import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by inx on 2016/8/29.
 */
public class CrashInfo {
    private final String threadName;
    private final String errorMsg;
    public final String line;

    private final StackTraceElement[] frames;
    private final AppInfo appInfo;
    private final DeviceInfo deviceInfo;
    private final DeviceState deviceState;
    private final ThreadState threadState;
    private final Long timestamp;
    private final String crashId;


    public CrashInfo(Context context, String threadName, Throwable exception) {
        appInfo = AppInfo.getInstance();
        deviceInfo = DeviceInfo.getInstance();
        deviceState = new DeviceState(context);
        threadState = new ThreadState();
        this.threadName = threadName;
        Throwable cause = exception.getCause() == null ? exception : exception.getCause();
        errorMsg = cause.getClass().getName();
        frames = cause.getStackTrace();
        timestamp = System.currentTimeMillis() / 1000;
        int index = -1;
        while (++index < frames.length && !frames[index].toString().contains(appInfo.getPkgName()))
            ;//查找包含包名的错误栈帧
        line = index < frames.length ? frames[index].toString() : frames[0].toString();
        crashId = md5(errorMsg + line);//根据错误信息和行号计算crash id
    }


    private static final transient String TAG = "CrashInfo";

    public String getThreadName() {
        return threadName;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public StackTraceElement[] getStackTraces() {
        return frames;
    }

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public DeviceState getDeviceState() {
        return deviceState;
    }

    public ThreadState getThreadState() {
        return threadState;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getCrashId() {
        return crashId;
    }

    public String getLine() {
        return line;
    }

    public static String md5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);

        for (byte b : hash) {
            int i = (b & 0xFF);
            if (i < 0x10) hex.append('0');
            hex.append(Integer.toHexString(i));
        }

        return hex.toString();
    }
}
