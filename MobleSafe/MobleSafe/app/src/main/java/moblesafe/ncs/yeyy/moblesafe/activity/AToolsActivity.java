package moblesafe.ncs.yeyy.moblesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import moblesafe.ncs.yeyy.moblesafe.R;
import moblesafe.ncs.yeyy.moblesafe.utils.SmsUtils;
import moblesafe.ncs.yeyy.moblesafe.utils.UIUtils;

;

/**
 * 高级工具
 */
public class AToolsActivity extends Activity implements View.OnClickListener {

    private TextView tvTools;
    private TextView tvBackUpSms;
    private ProgressDialog pd;
    @ViewInject(R.id.progressBar1)
    private ProgressBar progressBar1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
        ViewUtils.inject(this);
        initUI();
    }

    private void initUI() {
        tvTools = (TextView) findViewById(R.id.tv_tools);
        tvTools.setOnClickListener(this);
//        特性他默认是没有点击事件的，需要设置android:clickable="true";
        tvBackUpSms = (TextView) findViewById(R.id.tv_back_upsms);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            归属地查询
            case R.id.tv_tools:
                startActivity(new Intent(AToolsActivity.this, AddressActivity.class));
//            备份短信
            case R.id.tv_back_upsms:
                pd = new ProgressDialog(AToolsActivity.this);
                pd.setTitle("提示");
                pd.setMessage("稍安勿躁，正在备份……");
//                设置样式
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.show();
                new Thread() {//子线程可以改变UI
                    @Override
                    public void run() {
                        boolean result = SmsUtils.backUp(AToolsActivity.this, new SmsUtils.BackUpCallBackSms() {

                            @Override
                            public void befor(int count) {
                                pd.setMax(count);
                                progressBar1.setMax(count);
                            }

                            @Override
                            public void onBackUpSms(int process) {
                                pd.setProgress(process);
                                progressBar1.setProgress(process);
                            }
                        });
                        if (result) {
//                            安全弹吐司的方法
                            UIUtils.showToast(AToolsActivity.this, "备份成功");
//                            Looper.prepare();//如果不添加子线程改变ui就会挂掉
//                            Toast.makeText(AToolsActivity.this, "备份成功", Toast.LENGTH_SHORT).show();
//                            Looper.loop();
                        } else {
                            UIUtils.showToast(AToolsActivity.this, "备份失败");
//                            Looper.prepare();
//                            Toast.makeText(AToolsActivity.this, "备份失败", Toast.LENGTH_SHORT).show();
//                            Looper.loop();
                        }
                        pd.dismiss();
                    }
                }.start();

                break;
            default:
                break;
        }
    }
}
