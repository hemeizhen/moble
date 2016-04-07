package moblesafe.ncs.yeyy.moblesafe.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 自动清理进程
 * Created by yeyy on 2016/3/28.
 */
public class KillProcessService extends Service {

    private LockScreenReceiver receiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class LockScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            得到进程管理器
            ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);

//            获取到手机上吗正在运行的进程
            List<RunningAppProcessInfo> appProcess=activityManager.getRunningAppProcesses();
            for (RunningAppProcessInfo runningAppProcessInfo : appProcess) {
                activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
            }
            activityManager.killBackgroundProcesses(context.getPackageName());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        锁频广播
        receiver = new LockScreenReceiver();
//        锁频的过滤器
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);

//        计时器
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
//                写我们的业务逻辑
//                System.out.println("我被调用了" );

            }
        };

//        进行定时调度
        /**
         * 第一个参数表示用那个类进行调度
         * 第二个参数表示时间
         */
        timer.schedule(task, 1000,1000);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        当应用程序退出的时候需要把广播反注册掉
        unregisterReceiver(receiver);
//        手动回收
        receiver=null;

    }
}
