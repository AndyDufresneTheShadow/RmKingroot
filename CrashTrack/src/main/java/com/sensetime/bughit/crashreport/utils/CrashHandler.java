package com.sensetime.bughit.crashreport.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sensetime.bughit.crashreport.bean.CrashInfo;
import com.sensetime.bughit.crashreport.bean.Sprout;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

public class CrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private final UncaughtExceptionHandler originalHandler;
    private final Context context;
    private final CrashCallback callback;

    public CrashHandler(Context appContext, boolean isUpload, CrashCallback callback) {
        this.context = appContext;
        this.originalHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.callback = callback;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        final CrashInfo crashInfo = new CrashInfo(context, thread.getName(), ex);
        Sprout sprout = Sprout.transfer(crashInfo);
        final Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.STATIC)
                .create();

        saveSimple(
                gson,
                sprout
        );

        saveFullAndUpload(
                gson,
                crashInfo
        );

        saveToSDCard(sprout);

        if (callback != null) callback.runOnCrash(ex); // custom callback run

        try {
            Async.getExecutor().awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (originalHandler != null) {
            originalHandler.uncaughtException(thread, ex);
        } else {
            System.err.printf("Exception in thread \"%s\" ", thread.getName());
            ex.printStackTrace(System.err);
        }
        System.exit(0);
    }


    /**
     * 序列化简单数据，上传json
     */
    private void saveSimple(Gson gson, Sprout sprout) {
        final String json = gson.toJson(sprout);
        final String fileName = sprout.getTimestamp().toString();
        try {
            CrashStorage.dump2SimpleCache(json, fileName);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * 序列化完全数据，并上传gzip压缩后文件
     */
    private void saveFullAndUpload(final Gson gson, final CrashInfo crashInfo) {
        Async.run(new Runnable() {
            @Override
            public void run() {
                String full = gson.toJson(crashInfo);
                try {
                    CrashStorage.dump2FullCache(full, crashInfo.getTimestamp().toString() + ".gz");
                    CrashStorage.detectRemainAndUpdate(context);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 可视化保存简单数据到外部存储，以便后续查看
     */
    private void saveToSDCard(final Sprout sprout) {
        Async.run(new Runnable() {
            @Override
            public void run() {
                try {
                    CrashStorage.dump2SDCard(sprout);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

    }



}
