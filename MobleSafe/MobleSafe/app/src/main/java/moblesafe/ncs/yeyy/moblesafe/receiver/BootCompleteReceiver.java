package moblesafe.ncs.yeyy.moblesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 监听手机开机启动的广播
 * Created by yeyy on 2016/1/19.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

//        只有在防盗保护开启的情况下才进行sim卡的判断
        boolean protect = sp.getBoolean("protect", false);
        if (protect) {
            String sim = sp.getString("sim", null);//获取绑定的sim卡
            if (!TextUtils.isEmpty(sim)) {
//            获取当前手机的sim卡
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String currentSim = tm.getSimSerialNumber();//拿到当前手机sim卡
                if (sim.equals(currentSim)) {

                } else {
                    String phone=sp.getString("safe_phone","");//读取安全号码
//                    发送短信逻辑
                    SmsManager smsManager=SmsManager.getDefault();
                    smsManager.sendTextMessage(phone,null,"sim卡发生改变",null,null);
                }
            }
        }


    }
}
