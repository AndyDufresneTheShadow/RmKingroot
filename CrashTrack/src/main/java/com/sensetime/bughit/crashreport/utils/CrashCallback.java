package com.sensetime.bughit.crashreport.utils;

/**
 * Created by inx on 2016/9/7.
 */
public interface CrashCallback {
    void runOnCrash(Throwable ex);
}
