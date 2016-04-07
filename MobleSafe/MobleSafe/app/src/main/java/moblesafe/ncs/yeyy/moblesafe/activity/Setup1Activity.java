package moblesafe.ncs.yeyy.moblesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import moblesafe.ncs.yeyy.moblesafe.R;

/**
 * 第二个设置向导页
 */
public class Setup1Activity extends BaseSetupActivity implements View.OnClickListener {

    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
        btnNext = (Button) findViewById(R.id.btn_next1);
        btnNext.setOnClickListener(Setup1Activity.this);
    }

    @Override
    public void showNextPage() {
        startActivity(new Intent(Setup1Activity.this, Setup2Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    @Override
    public void showPreviousPage() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            下一页
            case R.id.btn_next1:
//                Intent intent=new Intent();
//                intent.setClass(Setup1Activity.this,Setup2Activity.class);
//                startActivity(intent);
                showNextPage();
//                两个界面切换动画 ,进入动画，退出动画
//                overridePendingTransition(enterAnim,exitAnim);
                break;
            default:
                break;
        }
    }
}
