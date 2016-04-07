package moblesafe.ncs.yeyy.moblesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import moblesafe.ncs.yeyy.moblesafe.R;
import moblesafe.ncs.yeyy.moblesafe.utils.ToastUtils;

/**
 * 第三个设置向导页
 */
public class Setup3Activity extends BaseSetupActivity implements View.OnClickListener {

    private Button btnNext;
    private Button btnPre;
    private Button btnContact;
    private EditText etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        btnNext = (Button) findViewById(R.id.btn_next3);
        btnNext.setOnClickListener(Setup3Activity.this);
        btnPre = (Button) findViewById(R.id.btn_previous3);
        btnPre.setOnClickListener(Setup3Activity.this);
        btnContact = (Button) findViewById(R.id.btn_contact);
        btnContact.setOnClickListener(Setup3Activity.this);
        etPhone = (EditText) findViewById(R.id.et_phone);
        String phone=mPref.getString("safe_phone","");
        etPhone.setText(phone);
    }

    @Override
    public void showNextPage() {
        startActivity(new Intent(Setup3Activity.this, Setup4Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(Setup3Activity.this, Setup2Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_previous3:
                showPreviousPage();
                break;

            case R.id.btn_next3:

                String phone=etPhone.getText().toString().trim();//trim过滤空格，如果用户输入的是空格，也不可以的
                if (TextUtils.isEmpty(phone)) {
//                Toast.makeText(Setup3Activity.this,"安全号码不能为空",Toast.LENGTH_SHORT).show();
                    ToastUtils.showToast(Setup3Activity.this, "安全号码不能为空");
                    return;
                }
                mPref.edit().putString("safe_phone",phone).commit();//保存安全号码
                showNextPage();
                break;
            case R.id.btn_contact:
//                选择联系人
                Intent intent=new Intent();
                intent.setClass(Setup3Activity.this,ContactActivity.class);
                startActivityForResult(intent,0);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode== Activity.RESULT_OK){
            String phone=data.getStringExtra("phone");//从后一个界面中拿到电话号码
//        phone=phone.replaceAll("-","").replaceAll(" ","");//替换字符中的符号，转变成自己想要的格式

            etPhone.setText(phone);//把电话号码设置给输入框
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
