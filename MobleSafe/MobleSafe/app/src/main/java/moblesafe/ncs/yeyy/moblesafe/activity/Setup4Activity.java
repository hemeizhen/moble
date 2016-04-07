package moblesafe.ncs.yeyy.moblesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import moblesafe.ncs.yeyy.moblesafe.R;

/**
 * 第四个设置向导页
 */
public class Setup4Activity extends BaseSetupActivity implements View.OnClickListener {

    private Button btnNext;
    private Button btnPre;
    private CheckBox cbProtect;
    //    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);

        btnNext = (Button) findViewById(R.id.btn_next4);
        btnNext.setOnClickListener(Setup4Activity.this);
        btnPre = (Button) findViewById(R.id.btn_previous4);
        btnPre.setOnClickListener(Setup4Activity.this);
        cbProtect = (CheckBox) findViewById(R.id.cb_protect);
//        根据sp的保存状态，更新CheckBox
        boolean protect=mPref.getBoolean("protect",false);
        if (protect){
            cbProtect.setText("防盗保护已经开启");
            cbProtect.setChecked(true);
        }else {
            cbProtect.setText("防盗保护没有开启");
            cbProtect.setChecked(false);
        }
//        监听CheckBox的状态改变
        cbProtect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cbProtect.setText("防盗保护已开启");
                    mPref.edit().putBoolean("protect",true).commit();
                }else {
                    cbProtect.setText("防盗保护没有开启");
                    mPref.edit().putBoolean("protect",false).commit();
                }
            }
        });
//        mPref = getSharedPreferences("config",MODE_PRIVATE);
    }

    @Override
    public void showNextPage() {
        startActivity(new Intent(Setup4Activity.this, LostFindActivity.class));
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
        mPref.edit().putBoolean("configed",true).commit();//更新sp，表示已经展示过设置向导了，下次进来就不展示了
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(Setup4Activity.this, Setup3Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_previous4:
                showPreviousPage();
                break;

            case R.id.btn_next4:
                showNextPage();
                break;

            default:
                break;
        }
    }
}
