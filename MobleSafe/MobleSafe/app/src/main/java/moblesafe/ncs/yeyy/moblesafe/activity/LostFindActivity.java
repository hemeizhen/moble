package moblesafe.ncs.yeyy.moblesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import moblesafe.ncs.yeyy.moblesafe.R;

/**
 * 手机防盗页面
 */
public class LostFindActivity extends Activity implements View.OnClickListener {

    private SharedPreferences mPrefs;
    private TextView tvReset;
    private TextView tvSafePhone;
    private ImageView ivProtect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getSharedPreferences("config", MODE_PRIVATE);
        boolean configed = mPrefs.getBoolean("configed", false);
        if (configed) {
            setContentView(R.layout.activity_lost_find);
            tvReset = (TextView) findViewById(R.id.tv_reset_setting);
            tvReset.setOnClickListener(LostFindActivity.this);

//            根据sp更新安全号码
            tvSafePhone = (TextView) findViewById(R.id.tv_safe_phone);
            String phone = mPrefs.getString("safe_phone", "");
            tvSafePhone.setText(phone);

//           根据sp更新保护锁
            ivProtect = (ImageView) findViewById(R.id.iv_protect);
            boolean protect=mPrefs.getBoolean("protect",false);
            if (protect){
                ivProtect.setImageResource(R.drawable.lock);
            }else {
                ivProtect.setImageResource(R.drawable.unlock);
            }
        } else {
//            设置向导页
            startActivity(new Intent(LostFindActivity.this, Setup1Activity.class));
            finish();

        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            重新进入设置向导
            case R.id.tv_reset_setting:
                startActivity(new Intent(LostFindActivity.this, Setup1Activity.class));
                finish();

                break;
            default:
                break;
        }
    }
}
