package rmkt.inx95.rmkingroot.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;

import java.io.File;

import rmkt.inx95.rmkingroot.R;
import sceenshot.share.moment.WXShareManager;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private TextView hint;
    private ImageView show;
     WXShareManager wxShareManager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
        hint = (TextView) findViewById(R.id.tv_hint);
        show = (ImageView) findViewById(R.id.iv_show);
        wxShareManager = WXShareManager.getInstance(WXEntryActivity.this);
        wxShareManager.getmWXApi().handleIntent(getIntent(), this);
        String fileName = getIntent().getExtras().getString("bitmap");
        File file=null;
        final Bitmap bitmap;
        if (fileName == null ||!(file = new File(getCacheDir(), fileName)).exists()) {
           // hint.setText("bitmap 为空");
            return;
        }
        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        show.setImageBitmap(bitmap);

        findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!WXShareManager.isWXAvailable(WXEntryActivity.this)) {
                    //hint.setText("请先安装微信");
                    return;
                }

                wxShareManager.sharePicture(
                        bitmap,
                        "SenseGame",
                        "这是SenseTime出品的小游戏。。。",
                        SendMessageToWX.Req.WXSceneTimeline
                );
                finish();
               // hint.setText("正在分享。。。");
            }
        });

       //  wxShareManager.handleIntent(getIntent(),this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
      //  wxShareManager.handleIntent(intent,this);
        wxShareManager.getmWXApi().handleIntent(getIntent(), this);
    }


    @Override
    public void onReq(BaseReq baseReq) {
        Toast.makeText(this, "I' onReq", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResp(BaseResp baseResp) {
        finish();
        Toast.makeText(this, "I' onResp", Toast.LENGTH_LONG).show();
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
//分享成功
                hint.setText("分享成功,正在返回");
            case BaseResp.ErrCode.ERR_USER_CANCEL:
//分享取消
                hint.setText("分享取消");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
//分享拒绝
                hint.setText("分享拒绝");
                break;

        }
    }
}
