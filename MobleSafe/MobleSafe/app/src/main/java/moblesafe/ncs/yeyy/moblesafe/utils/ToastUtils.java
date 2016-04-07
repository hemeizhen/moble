package moblesafe.ncs.yeyy.moblesafe.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by yeyy on 2016/1/20.
 * Toast的工具类
 */
public class ToastUtils {
    public static void showToast(Context ctx,String text){
        Toast.makeText(ctx,text,Toast.LENGTH_SHORT).show();
    }
}
