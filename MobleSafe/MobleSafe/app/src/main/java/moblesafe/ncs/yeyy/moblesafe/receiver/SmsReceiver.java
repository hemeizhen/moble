package moblesafe.ncs.yeyy.moblesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import java.util.Objects;

import moblesafe.ncs.yeyy.moblesafe.R;
import moblesafe.ncs.yeyy.moblesafe.service.LocationService;

/**
 * 拦截短信
 * Created by yeyy on 2016/1/21.
 */
public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] objects = (Objects[]) intent.getExtras().get("pdus");
//        短信最多140字节，超出的话，会分为多条短信发送，所以是一个数组，因为我们的短信指令很短，所以for循环只执行一次
        for (Object object : objects) {
            SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
            String originatingAddress = message.getOriginatingAddress();//短信来源号码
            String messageBody = message.getMessageBody();//短信内容

            if ("#*alarm*#".equals(messageBody)) {
//    播放背景音乐 即使手机调味静音，也能播放音乐，因为使用的是媒体声音的通道，和铃声无关
                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                player.setVolume(1f, 1f);
                player.setLooping(true);
                player.start();
                abortBroadcast();//中断短信的传递，从而系统短信app就收不到内容了
            } else if ("#*location*#".equals(messageBody)) {
                context.startService(new Intent(context, LocationService.class));//开启定位服务
                SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
                String location = sp.getString("location", "");
                abortBroadcast();
            } else if ("#*wipedata*#".equals(messageBody)) {
                abortBroadcast();
            } else if ("#*lockscreen*#".equals(messageBody)) {
                abortBroadcast();
            }
        }

    }
}
