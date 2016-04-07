package moblesafe.ncs.yeyy.moblesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import moblesafe.ncs.yeyy.moblesafe.R;

/**
 * 设置中心的自定义控件
 * Created by yeyy on 2016/1/13.
 */
public class SettingClickView extends RelativeLayout {
    private TextView tvTitle;
    private TextView tvDesc;

    public SettingClickView(Context context) {
        super(context);
        initView();
    }
//自定义属性的使用
    public SettingClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        //将自定义好的布局文件设置给当前的SettingClickView this给其一个父类
        View.inflate(getContext(), R.layout.view_setting_click, this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
    }

    public void setTvTitle(String title) {
        tvTitle.setText(title);
    }

    public void setTvDesc(String desc) {
        tvDesc.setText(desc);
    }


}
