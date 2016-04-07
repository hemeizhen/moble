package moblesafe.ncs.yeyy.moblesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import moblesafe.ncs.yeyy.moblesafe.R;

/**
 * 设置中心的自定义控件
 * Created by yeyy on 2016/1/13.
 */
public class SettingItemView extends RelativeLayout {
//自定义属性的使用，命名空间
    private static final String NAMESPACE="http://schemas.android.com/apk/res/moblesafe.ncs.yeyy.moblesafe";
    private TextView tvTitle;
    private TextView tvDesc;
    private CheckBox cbStatus;
    private String mTitle;
    private String mDescOn;
    private String mDescOff;

    public SettingItemView(Context context) {
        super(context);
        initView();
    }
//自定义属性的使用
    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTitle = attrs.getAttributeValue(NAMESPACE, "title");//根据属性名称获取属性的值
        mDescOn = attrs.getAttributeValue(NAMESPACE,"desc_on");
        mDescOff = attrs.getAttributeValue(NAMESPACE,"desc_off");
        initView();
//        int attributeCount=attrs.getAttributeCount();
//        for (int i = 0; i < attributeCount; i++) {
//            String attributeName=attrs.getAttributeName(i);
//            String attributeValue=attrs.getAttributeValue(i);
//        }
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        //将自定义好的布局文件设置给当前的SettingItemView this给其一个父类
        View.inflate(getContext(), R.layout.view_setting_item, this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        cbStatus = (CheckBox) findViewById(R.id.cb_status);
        setTvTitle(mTitle);//设置标题
    }

    public void setTvTitle(String title) {
        tvTitle.setText(title);
    }

    public void setTvDesc(String desc) {
        tvDesc.setText(desc);
    }

    /**
     * 返回勾选状态
     * @return
     */
    public boolean isChecked(){
        return cbStatus.isChecked();
    }
    public void setChecked(boolean check){
        cbStatus.setChecked(check);
//        根据选择的状态，更新文本描述
        if (check){
            setTvDesc(mDescOn);
        }
        else {
            setTvDesc(mDescOff);
        }
    }
}
