package rmkt.inx95.rmkingroot;

import android.app.Application;

import com.sensetime.bughit.crashreport.CrashReport;
import com.sensetime.bughit.crashreport.utils.CrashCallback;

/**
 * Created by yalerex on 2016/12/10.
 */

public class RmKingrootApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.init(getApplicationContext(),null,false,true, new CrashCallback() {
            @Override
            public void runOnCrash(Throwable ex) {
                //custom action
            }
        });
    }
}
