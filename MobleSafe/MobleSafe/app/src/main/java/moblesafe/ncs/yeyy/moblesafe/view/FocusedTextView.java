package moblesafe.ncs.yeyy.moblesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 获取焦点TextView
 * Created by yeyy on 2016/1/11.
 */
public class FocusedTextView extends TextView {
    //    用代码new时走此方法
    public FocusedTextView(Context context) {
        super(context);
    }

    //有属性的时候
    public FocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //有style样式的时候
    public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 表示有没有获取焦点
     * 跑马灯要运行，首先调用此函数判断是否有焦点，是true的话，跑马灯才会有效果，
     * 所以我们不管实际上textview有没有焦点，都强制返回true，让跑马灯认为有焦点
     *
     * @return
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
