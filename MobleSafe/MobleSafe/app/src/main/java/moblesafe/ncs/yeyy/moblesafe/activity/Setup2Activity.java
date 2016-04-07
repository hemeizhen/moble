package moblesafe.ncs.yeyy.moblesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import moblesafe.ncs.yeyy.moblesafe.R;
import moblesafe.ncs.yeyy.moblesafe.view.SettingItemView;

/**
 * 第一个设置向导页
 */
public class Setup2Activity extends BaseSetupActivity implements View.OnClickListener {


    private Button btnNext;
    private Button btnPre;
    private SettingItemView sivSim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        btnNext = (Button) findViewById(R.id.btn_next2);
        btnNext.setOnClickListener(Setup2Activity.this);
        btnPre = (Button) findViewById(R.id.btn_previous2);
        btnPre.setOnClickListener(Setup2Activity.this);
        sivSim = (SettingItemView) findViewById(R.id.siv_sim);
        sivSim.setOnClickListener(Setup2Activity.this);

        String sim = mPref.getString("sim", null);
        if (!TextUtils.isEmpty(sim)) {
            sivSim.setChecked(true);
        } else {
            sivSim.setChecked(false);
        }

    }

    @Override
    public void showNextPage() {
//如果sim卡没有绑定，就不允许进入下一个页面
//        String sim=mPref.getString("sim",null);
//        if (TextUtils.isEmpty(sim)){
//            ToastUtils.showToast(this,"必须绑定sim卡");
//            return;
//        }
        startActivity(new Intent(Setup2Activity.this, Setup3Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);

    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(Setup2Activity.this, Setup1Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_previous2:
                showPreviousPage();
                break;

            case R.id.btn_next2:
                showNextPage();
                break;

            case R.id.siv_sim:
                if (sivSim.isChecked()) {
                    sivSim.setChecked(false);
                    mPref.edit().remove("sim").commit();//删除已经绑定的sim卡
                } else {
                    sivSim.setChecked(true);
                    //获取sim卡序列号,TelephonyManager系统给的api接口
//                    TelephonyManager tm = (TelephonyManager) getSystemService(TELECOM_SERVICE);
//                    String simSerialNumber = tm.getSimSerialNumber();//获取sim卡序列号,注意：要给权限

//                        mPref.edit().putString("sim", simSerialNumber).commit();//将sim卡序列号保存在sp中
                }
                break;

            default:
                break;
        }
    }

}
