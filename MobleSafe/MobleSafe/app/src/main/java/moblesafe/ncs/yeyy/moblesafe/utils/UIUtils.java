package moblesafe.ncs.yeyy.moblesafe.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by yeyy on 2016/3/18.
 * UI工具类
 */
public class UIUtils {
    public static void showToast(final Activity context,final String msg){
        if("main".equals(Thread.currentThread().getName())){
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }else{
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
