package moblesafe.ncs.yeyy.moblesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;
import java.util.Objects;

import moblesafe.ncs.yeyy.moblesafe.db.dao.BlackNumberDao;

/**
 * 通讯卫士黑名单设置的服务
 */
public class CallSafeService extends Service {

    private BlackNumberDao dao;
    private TelephonyManager tm;
    private Uri uri;

    public CallSafeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dao = new BlackNumberDao(this);

//        获取到系统的电话服务
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        MyPhoneStateListener listener = new MyPhoneStateListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

//        初始化短信的广播
        InnerReceiver innerReceiver = new InnerReceiver();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(innerReceiver, intentFilter);
    }

    private class MyPhoneStateListener extends PhoneStateListener {
//        电话状态改变的监听

        /**
         * @param state          电话的状态
         * @param incomingNumber 电话号码
         */
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
//            * @see TelephonyManager#CALL_STATE_IDLE 电话闲置
//            * @see TelephonyManager#CALL_STATE_RINGING 电话铃响
//            * @see TelephonyManager#CALL_STATE_OFFHOOK 电话接通
            switch (state) {
//                电话铃响状态
                case TelephonyManager.CALL_STATE_RINGING:
                    String mode = dao.findNumber(incomingNumber);
                    /**
                     * 黑名单拦截模式
                     * 1.全部拦截，电话拦截+短信拦截
                     * 2.电话拦截
                     * 3.短信拦截
                     */
                    if (mode.equals("1") || mode.equals("2")) {
                        System.out.println("挂断黑名单电话");
                        uri = Uri.parse("content://call_log/calls");
                        getContentResolver().registerContentObserver(uri, true,
                                new MyContentObserver(new Handler(), incomingNumber));
//                        挂断电话需要一定的步骤如下
                        endCall();
                    }
                    break;
            }
        }
    }

    /**
     * 实现隐藏黑名单电话挂断记录
     */
    private class MyContentObserver extends ContentObserver {
        String incomingNumber;

        /**
         * Creates a content observer.
         *
         * @param handler        The handler to run {@link #onChange} on, or null if none.
         * @param incomingNumber
         */
        public MyContentObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        //        当数据改变的时候调用此方法
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            getContentResolver().unregisterContentObserver(this);
            deleteCallLog(incomingNumber);
        }
    }

    /**
     * 实现删除黑名单电话号码的功能
     *
     * @param incomingNumber
     */
    private void deleteCallLog(String incomingNumber) {
        getContentResolver().delete(uri,"number=?",new String[]{incomingNumber});
    }

    /**
     * 挂断电话
     */
    private void endCall() {
        try {
//            通过类加载器加载ServiceManager
            Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
//            通过反射得到当前的方法
            Method method = clazz.getDeclaredMethod("getService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
//            为了拦截到电话号码ITelephony这个类的使用，需要在main目录下面新建aidl文件夹，下包含两个包，
//            package android.telephony;package com.android.internal.telephony;
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
//            然后才能调用endCall方法
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objects = (Objects[]) intent.getExtras().get("pdus");
//        短信最多140字节，超出的话，会分为多条短信发送，所以是一个数组，因为我们的短信指令很短，所以for循环只执行一次
            for (Object object : objects) {
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
                String originatingAddress = message.getOriginatingAddress();//短信来源号码
                String messageBody = message.getMessageBody();//短信内容
//                通过短信的电话号码查询拦截模式
                String mode = dao.findNumber(originatingAddress);
                /**
                 * 黑名单拦截模式
                 * 1.全部拦截，电话拦截+短信拦截
                 * 2.电话拦截
                 * 3.短信拦截
                 */
                if (mode.equals("1")) {
                    abortBroadcast();
                } else if (mode.equals("3")) {
                    abortBroadcast();
                }
//                智能拦截模式
                if (messageBody.contains("fapiao")) {
                    abortBroadcast();
                }

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
