package sceenshot.share.moment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by yalerex on 2016/12/10.
 */

public class ScreenShotUtils {

    public static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Log.i("TAG", "" + statusBarHeight);
        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }
    private void openScreenshot(Activity activity,File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        activity.startActivity(intent);
    }
//        void takeScreenshot(Runnable finisher, boolean statusBarVisible, boolean navBarVisible) {
//            // We need to orient the screenshot correctly (and the Surface api seems to take screenshots
//            // only in the natural orientation of the device :!)
//            mDisplay.getRealMetrics(mDisplayMetrics);
//            float[] dims = {mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels};
//            float degrees = getDegreesForRotation(mDisplay.getRotation());
//            boolean requiresRotation = (degrees > 0);
//            if (requiresRotation) {
//                // Get the dimensions of the device in its native orientation
//                mDisplayMatrix.reset();
//                mDisplayMatrix.preRotate(-degrees);
//                mDisplayMatrix.mapPoints(dims);
//                dims[0] = Math.abs(dims[0]);
//                dims[1] = Math.abs(dims[1]);
//            }
//
//            // Take the screenshot
//            mScreenBitmap = Surface.screenshot((int) dims[0], (int) dims[1]);
//            if (mScreenBitmap == null) {
//                notifyScreenshotError(mContext, mNotificationManager);
//                finisher.run();
//                return;
//            }
//
//            if (requiresRotation) {
//                // Rotate the screenshot to the current orientation
//                Bitmap ss = Bitmap.createBitmap(mDisplayMetrics.widthPixels,
//                        mDisplayMetrics.heightPixels, Bitmap.Config.ARGB_8888);
//                Canvas c = new Canvas(ss);
//                c.translate(ss.getWidth() / 2, ss.getHeight() / 2);
//                c.rotate(degrees);
//                c.translate(-dims[0] / 2, -dims[1] / 2);
//                c.drawBitmap(mScreenBitmap, 0, 0, null);
//                c.setBitmap(null);
//                mScreenBitmap = ss;
//            }
//
//            // Optimizations
//            mScreenBitmap.setHasAlpha(false);
//            mScreenBitmap.prepareToDraw();
//
//            // Start the post-screenshot animation
//            startAnimation(finisher, mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels,
//                    statusBarVisible, navBarVisible);
//        }

}
