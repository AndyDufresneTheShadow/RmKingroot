package com.sensetime.bughit.crashreport.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;

import com.sensetime.bughit.crashreport.bean.Sprout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

/**
 * 1个simple文件大约是500byte
 * 1个full文件大约是1.5KB
 */
public class CrashStorage {
    private static final String TAG = "CrashStorage";
    private static final String CRASH_HIT = "bughit";
    public static final String SIMPLE = "simple";
    public static final String FULL = "full";
    private static final int MAX_SIZE = 100;
    public static final float KEEP_FACTOR = 0.75f;
    private static String sParentPath;
    private static String sSimplePath;
    private static String sSimpleSDPath; //在sd卡中存储的简略信息
    private static String sFullPath;
    private static boolean isUpload;


    public static void init(Context appContext, boolean isUploadEnabled) {
        initCacheDir(appContext);
        initSDDir(appContext);
        guaranteedCapacity();
        isUpload = isUploadEnabled;
        detectRemainAndUpdate(appContext);
    }

    /**
     * 当到达MAX_SIZE时，会保存后面新建的MAX_SIZE*KEEP_FACTOR个文件，删除前面的那些
     */
    private static void guaranteedCapacity() {
        if (sParentPath == null) return;
        String[] dirs = new String[]{
                sSimplePath,
                sSimpleSDPath,
                sFullPath
        };
        for (String dirString : dirs) {
            File dir = new File(dirString);
            String[] list = dir.list();
            if (list.length >= MAX_SIZE) {
                int maxIndex = (int) (MAX_SIZE * (1.0 - KEEP_FACTOR)) - 1;
                int i = -1;
                while (++i <= maxIndex) deleteFile(new File(list[i]));
            }
        }
    }

    private static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null)
                return networkInfo.isAvailable();
        }
        return false;
    }

    public static void detectRemainAndUpdate(Context appContext) {
        if (!isUpload || !isNetworkConnected(appContext)) return;
        uploadSimpleAndDelete(HttpClient.getInstance().getSimpleUrl());
        uploadFullAndDelete(HttpClient.getInstance().getFullUrl(), getAndroidImei(appContext));
    }

    private static String getAndroidImei(Context appContext) {
        ContentResolver cr = appContext.getContentResolver();
        return Settings.Secure.getString(cr, Settings.Secure.ANDROID_ID);
    }

    /**
     * 遗留的数据，上传
     */
    private static void uploadSimpleAndDelete(final String url) {
        Async.run(new Runnable() {
            @Override
            public void run() {
                Map<String, String> map = readFromSimpleCache();
                if (map == null || map.keySet().size() == 0) return;
                Set<String> keySet = map.keySet();
                for (String key : keySet) {
                    String value = map.get(key);
                    if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
                        continue;
                    }
                    try {
                        HttpClient.getInstance().send(
                                url,
                                HttpClient.Method.POST,
                                value
                        );
                        deleteSimpleCache(key);
                    } catch (HttpClient.BadResponseException e) {
                        e.printStackTrace();
                        deleteSimpleCache(key);
                    } catch (HttpClient.NetworkException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static void uploadFullAndDelete(final String url, final String dir) {
        Async.run(new Runnable() {
            @Override
            public void run() {
                String[] fileNames = getFullCacheFileNames();
                if (fileNames == null || fileNames.length == 0) {
                    return;
                }
                for (String fileName : fileNames) {
                    File file = new File(sFullPath, fileName);
                    if (!file.exists() || !file.isFile()) continue;
                    try {
                        HttpClient.getInstance().uploadFile(
                                url,
                                file,
                                dir
                        );
                        deleteFullCache(fileName);
                    } catch (HttpClient.BadResponseException e) {
                        e.printStackTrace();
                        deleteFullCache(fileName);
                    } catch (HttpClient.NetworkException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static void initCacheDir(Context appContext) {
        File parentDir = new File(appContext.getCacheDir().getAbsolutePath(), CRASH_HIT);
        if ((parentDir.exists() || parentDir.mkdirs())) {
            sParentPath = parentDir.getAbsolutePath();
            File simpleDir = new File(sParentPath, SIMPLE);
            if (simpleDir.exists() || simpleDir.mkdir())
                sSimplePath = simpleDir.getAbsolutePath();
            File fullDir = new File(sParentPath, FULL);
            if (fullDir.exists() || fullDir.mkdir())
                sFullPath = fullDir.getAbsolutePath();
        } else {
            Log.w(TAG, "can't create directory");
            sParentPath = null;
        }
    }

    private static void initSDDir(Context context) {
        String rootDir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (context.getExternalFilesDir(null) != null) {
                rootDir = context.getExternalFilesDir(null).getPath();
            } else {
                rootDir = Environment.getExternalStorageDirectory().getPath();
            }
        } else {
            rootDir = context.getFilesDir().getPath();
        }
        sSimpleSDPath = rootDir + File.separator + CRASH_HIT;
        File sdDir = new File(sSimpleSDPath);
        sdDir.mkdirs();
    }

    public static boolean dump2SDCard(Sprout sprout) throws IllegalAccessException {
        File file = new File(sSimpleSDPath, sprout.getTimestamp().toString());
        if (file.exists()) return false;
        FileOutputStream fos = null;
        try {
            file.createNewFile();
            fos = new FileOutputStream(file);
            fos.write(sprout.toString().getBytes());
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalAccessException("unable to write file in sdcard directory");
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean dump2SimpleCache(String content, String fileName) throws IllegalAccessException {

        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(fileName)) return false;
        if (sSimplePath == null) {
            throw new IllegalAccessException("unable to write file in cache directory");
        }
        File file = new File(sSimplePath, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Map<String, String> readFromSimpleCache() {
        if (sSimplePath == null) {
            return null;
        }
        File dir = new File(sSimplePath);

        String[] children = dir.list();
        Map<String, String> map = new HashMap<String, String>();
        String content;
        for (String child : children) {
            if (!TextUtils.isEmpty(child) &&
                    !TextUtils.isEmpty(content = readFile(child)))
                map.put(child, content);
        }
        return map;
    }

    public static File dump2FullCache(String content, String fileName) throws IllegalAccessException {
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(fileName)) {
            return null;
        }
        if (sFullPath == null) {
            throw new IllegalAccessException("unable to write file in cache directory");
        }
        File file = new File(sFullPath, fileName);
        try {
            file.createNewFile();
            GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(file));
            gos.write(content.getBytes());
            gos.flush();
            gos.finish();
            gos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    public static String[] getFullCacheFileNames() {
        File fullDir = null;
        if (sFullPath == null || !(fullDir = new File(sFullPath)).exists()) {
            return null;
        }
        return fullDir.list();
    }

    private static String readFile(String fileName) {
        File file = new File(sSimplePath, fileName);
        String content = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            fis.close();
            content = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void deleteSimpleCache(String fileName) {
        deleteFile(new File(sSimplePath, fileName));
    }

    public static void deleteFullCache(String fileName) {
        deleteFile(new File(sFullPath, fileName));
    }

    private static void deleteFile(File file) {
        if (file.exists() && file.isFile())
            file.delete();
    }

    public static void setUploadEnabled(Context context, boolean isUploadEnabled) {
        isUpload = isUploadEnabled;
        detectRemainAndUpdate(context);
    }
}
