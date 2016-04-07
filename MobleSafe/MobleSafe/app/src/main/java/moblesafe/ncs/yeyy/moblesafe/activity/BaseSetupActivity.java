package moblesafe.ncs.yeyy.moblesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * 设置引导页的基类，不需要再清单文件中注册,不需要界面展示
 * Created by yeyy on 2016/1/19.
 */
public abstract class BaseSetupActivity extends Activity {
    private GestureDetector mDetector;
    public SharedPreferences mPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = getSharedPreferences("config",MODE_PRIVATE);
//        手势识别器
        mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            /**
             * 监听手势滑动事件
             * @param e1 表示滑动的起点
             * @param e2 表示滑动的重点
             * @param velocityX X表示水平速度
             * @param velocityY Y表示垂直速度
             * @return
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                判断纵向滑动幅度是否过大，过大不允许切换界面
                if (Math.abs(e2.getRawY())-e1.getRawY()>100){
                    Toast.makeText(BaseSetupActivity.this,"不能这样滑哦！",Toast.LENGTH_SHORT).show();
                    return true;
                }
//                滑动是否过慢
                if (Math.abs(velocityX)<100){
                    Toast.makeText(BaseSetupActivity.this,"滑动的太慢了！",Toast.LENGTH_SHORT).show();
                    return true;
                }

//               向右滑动，上一页
                if (e2.getRawX()-e1.getRawX()>200){
                    showPreviousPage();
                    return true;
                }
//                像左滑动，下一页
                if (e1.getRawX()-e2.getRawX()>200){
                    showNextPage();
                    return true;
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }
    /**
     * 展示下一页, 子类必须实现
     */
    public abstract void showNextPage();

    /**
     * 展示上一页, 子类必须实现
     */
    public abstract void showPreviousPage();
//    // 点击下一页按钮
//    public void next(View view) {
//        showNextPage();
//    }
//
//    // 点击上一页按钮
//    public void previous(View view) {
//        showPreviousPage();
//    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);//委托手势识别器处理触摸事件
        return super.onTouchEvent(event);
    }
}
