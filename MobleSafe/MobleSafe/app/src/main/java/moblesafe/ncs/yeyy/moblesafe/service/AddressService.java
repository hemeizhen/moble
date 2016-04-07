package moblesafe.ncs.yeyy.moblesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import moblesafe.ncs.yeyy.moblesafe.R;
import moblesafe.ncs.yeyy.moblesafe.db.dao.AddressDao;

/**
 * 来电提醒服务
 * Created by yeyy on 2016/1/27.
 */
public class AddressService extends Service {

    private TelephonyManager tm;
    private MyListener listener;
    private OutCallReceiver receiver;
    private WindowManager mWM;

    private TextView tvNumber;
    private View view;
    private SharedPreferences mPref;

    private int startX;
    private int startY;
    private int winWidth;
    private int winHeight;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPref = getSharedPreferences("config", MODE_PRIVATE);
//        监听来电
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);//监听来电的状态

        receiver = new OutCallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(receiver, filter);//动态注册广播
    }

    class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING://电话铃响状态
                    String address = AddressDao.getAddress(incomingNumber);//根据来电号码查询归属地
                    Toast.makeText(AddressService.this, address, Toast.LENGTH_LONG).show();

                    showToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE://电话闲置状态
                    if (mWM != null && view != null) {
                        mWM.removeView(view);//从window中移除view
                        view = null;
                    }
                default:
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    /**
     * 监听去电的广播接受者
     * android.permission.PROCESS_OUTGOING_CALLS
     * Created by yeyy on 2016/1/27.
     */
    class OutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取去电号码
            String number = getResultData();
            String address = AddressDao.getAddress(number);//拿到地址
            Toast.makeText(context, address, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);//停止来电监听

        unregisterReceiver(receiver);//注销广播
    }

    /**
     * 自定义归属地浮窗
     */
    private void showToast(String text) {

        mWM = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
//获取屏幕宽高
        winWidth = mWM.getDefaultDisplay().getWidth();
        winHeight = mWM.getDefaultDisplay().getHeight();

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE 不能够被触摸
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
//        电话窗口，用于电话交互（特别是呼入）他的位于所有应用程序之上，状态栏之下
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
//          将重心位置设置为左上方，有就是（0，0）从左上方开始而不是默认的重心位置
        params.gravity = Gravity.LEFT + Gravity.TOP;

        params.setTitle("Toast");

        int lastX = mPref.getInt("lastX", 0);
        int lastY = mPref.getInt("lastY", 0);

//        设置浮窗的位置,基于左上方的偏移量
        params.x = lastX;
        params.y = lastY;
//        view = new TextView(this);
//        view.setText(text);
//        view.setTextColor(Color.RED);
        view = View.inflate(this, R.layout.toast_address, null);

        int[] bgs = new int[]{R.drawable.call_locate_white, R.drawable.call_locate_orange,
                R.drawable.call_locate_blue, R.drawable.call_locate_gray, R.drawable.call_locate_green};
        int style = mPref.getInt("address_style", 0);//读取保存的style

        view.setBackgroundResource(bgs[style]);//根据存储样式更新背景
        tvNumber = (TextView) view.findViewById(R.id.tv_number);
        tvNumber.setText(text);
        mWM.addView(view, params);//将view添加在屏幕上（window）

//        实现按钮的拖拽，需要权限android.permission.SYSTEM_ALERT_WINDOW
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                      初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();
//                        计算移动偏移量
                        int dx = endX - startX;
                        int dy = endY - startY;
//                        更新浮窗位置
                        params.x += dx;
                        params.y += dy;

//                        防止坐标偏移屏幕
                        if (params.x<0){
                            params.x=0;
                        }
                        if (params.y<0){
                            params.y=0;
                        }
                        if (params.x>winWidth-view.getWidth()){
                            params.x=winWidth=view.getWidth();
                        }
                        if (params.y>winHeight-view.getHeight()){
                            params.y=winHeight=view.getHeight();
                        }
                        mWM.updateViewLayout(view, params);

//                       重新初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        //                       记录上次结束的坐标点
                        SharedPreferences.Editor edit = mPref.edit();
                        edit.putInt("lastX", params.x);
                        edit.putInt("lastY", params.y);
                        edit.commit();//记得一定要提交
                        break;
                    default:
                        break;
                }

                return false;
            }
        });
    }
}
