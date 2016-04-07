package moblesafe.ncs.yeyy.moblesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import moblesafe.ncs.yeyy.moblesafe.R;

/**
 * 修改归属地显示位置
 */
public class DragViewActivity extends Activity  {

    private TextView tvTop;
    private TextView tvBottom;
    private ImageView ivDrag;
    private int startX;
    private int startY;
    private SharedPreferences mPref;
    private int winWidth;
    private int winHeight;

    long[] mHits=new long[2];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view);

//        mPref = getSharedPreferences("config",MODE_PRIVATE);

        tvTop = (TextView) findViewById(R.id.tv_top);
        tvBottom = (TextView) findViewById(R.id.tv_bottom);
        ivDrag = (ImageView) findViewById(R.id.iv_drag);

        dragListener();
//        int lastX=mPref.getInt("lastX",0);
//        int lastY=mPref.getInt("lastY",0);
//        RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)ivDrag
//                .getLayoutParams();//获取布局对象
//        layoutParams.leftMargin=lastX;//设置左边距
//        layoutParams.topMargin=lastY;//设置top边距
//        ivDrag.setLayoutParams(layoutParams);//重新设置位置
        sharedPreferences();

    }

//    设置触摸监听
    private void dragListener(){
        ivDrag.setOnTouchListener(new View.OnTouchListener() {
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
//                       计算偏移量
                        int dx = endX - startX;
                        int dy = endY - startY;
//                       更新左上右下坐标
                        int l = ivDrag.getLeft() + dx;
                        int r = ivDrag.getRight() + dx;

                        int t = ivDrag.getTop() + dy;
                        int b = ivDrag.getBottom() + dy;

//                        判断滑动时停止的位置不能超出屏幕边界，超出屏幕直接返回
                        if (l < 0 || r > winWidth || t < 0 || b > winHeight - 20){
                            break;
                        }

//                        根据提示框的位置决定图片的隐藏
                        if (t>winHeight/2){//上边显示，下边隐藏
                            tvTop.setVisibility(View.VISIBLE);
                            tvBottom.setVisibility(View.INVISIBLE);
                        }else {//上边隐藏，下边显示
                            tvTop.setVisibility(View.INVISIBLE);
                            tvBottom.setVisibility(View.VISIBLE);
                        }

//                       更新界面
                        ivDrag.layout(l, t, r, b);

//                       重新初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
//                       记录上次结束的坐标点
                        Editor edit = mPref.edit();
                        edit.putInt("lastX", ivDrag.getLeft());
                        edit.putInt("lastY", ivDrag.getTop());
                        edit.commit();//记得一定要提交
                        break;
                    default:
                        break;
                }
                return false;//事件要往下传递，让onClick可以响应
            }
        });

//        双击按钮
        ivDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits,1,mHits,0,mHits.length-1);
                mHits[mHits.length-1]= SystemClock.uptimeMillis();
                if (mHits[0]>=(SystemClock.uptimeMillis()-500)){
//                把图片居中
                    ivDrag.layout(winWidth/2-ivDrag.getWidth()/2,
                            ivDrag.getTop(),winWidth/2+ivDrag.getWidth()/2,
                            ivDrag.getBottom());
                }

            }
        });
    }

    //        记录当前结束时的位置
    private void sharedPreferences(){
        mPref = getSharedPreferences("config",MODE_PRIVATE);

        int lastX=mPref.getInt("lastX",0);
        int lastY=mPref.getInt("lastY",0);

//        初始化Android界面
//        onMeasure(测量view),onLayout(安放的位置),onDraw(绘制)
//        不能使用之歌方法，因为还没有测量完成，就不能安放位置
//        ivDrag.layout(lastX,lastY,lastX+ivDrag.getWidth(),lastY+ivDrag.getHeight());

//        获取屏幕宽度
        winWidth = getWindowManager().getDefaultDisplay().getWidth();
        winHeight = getWindowManager().getDefaultDisplay().getHeight();


        if (lastY>winHeight/2){//上边显示，下边隐藏
            tvTop.setVisibility(View.VISIBLE);
            tvBottom.setVisibility(View.INVISIBLE);
        }else {//上边隐藏，下边显示
            tvTop.setVisibility(View.INVISIBLE);
            tvBottom.setVisibility(View.VISIBLE);
        }
        RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)ivDrag
                .getLayoutParams();//获取布局对象
        layoutParams.leftMargin=lastX;//设置左边距
        layoutParams.topMargin=lastY;//设置top边距
        ivDrag.setLayoutParams(layoutParams);//重新设置位置
    }

}
