package sceenshot.share.moment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.platformtools.Util;

import java.io.ByteArrayOutputStream;

/**
 * Created by yalerex on 2016/12/10.
 */

public class WXShareManager {

    public static final String APP_ID = "wxc91888987a108c25";
    private static final int THUMB_SIZE = 150;//设置缩略图大小
    private static WXShareManager mInstance;
    private IWXAPI mWXApi;

    public static WXShareManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new WXShareManager(context);
        }
        return mInstance;
    }

    private WXShareManager(Context context){
        initWXShare(context);
    }

    private void initWXShare(Context context){
        if (mWXApi == null) {
            mWXApi = WXAPIFactory.createWXAPI(context, APP_ID, true);
        }
        mWXApi.registerApp(APP_ID);
    }

    public void sharePicture(Bitmap bitmap,String title,String description, int scene) {
        WXImageObject imgObj = new WXImageObject(bitmap);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        msg.title = title;
        msg.description = description;

        Bitmap thumbBitmap =  Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
        msg.thumbData = Util.bmpToByteArray(thumbBitmap, true);  //设置缩略图

        SendMessageToWX.Req req = new SendMessageToWX.Req();

        req.transaction = buildTransaction("ImgFromSenseGame");
        req.message = msg;
        req.scene = scene;
        mWXApi.sendReq(req);
    }

    private static byte[] bmpToByteArray(final Bitmap bitmap,boolean needRecycle){
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        if (needRecycle) {
            bitmap.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public void handleIntent(Intent intent,Activity activity) {
        mWXApi.handleIntent(intent, (IWXAPIEventHandler) activity);
    }

    public IWXAPI getmWXApi() {
        return mWXApi;
    }

    public static boolean isWXAvailable(Activity activity) {
        try {
            activity.getPackageManager().getPackageInfo("com.tencent.mm", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
