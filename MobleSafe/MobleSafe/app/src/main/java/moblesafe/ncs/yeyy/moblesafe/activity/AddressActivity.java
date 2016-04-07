package moblesafe.ncs.yeyy.moblesafe.activity;

import android.os.Bundle;
import android.app.Activity;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import moblesafe.ncs.yeyy.moblesafe.R;
import moblesafe.ncs.yeyy.moblesafe.db.dao.AddressDao;

/**
 * 归属地查询界面
 */
public class AddressActivity extends Activity implements View.OnClickListener {

    private Button btnQuery;
    private EditText etNumber;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        btnQuery = (Button) findViewById(R.id.btn_query);
        btnQuery.setOnClickListener(this);
        etNumber = (EditText) findViewById(R.id.et_number);
        etChange();
        tvResult = (TextView) findViewById(R.id.tv_result);
    }

    public void etChange() {
//    当编辑框发生改变的时候就监听
        etNumber.addTextChangedListener(new TextWatcher() {
            //        文字变化前的回调
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //       当文字发生变化时回调 CharSequence输入的内容
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String address = AddressDao.getAddress(s.toString());
                tvResult.setText(address);

            }

            //       文字变化结束回调
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_query:
                String number = etNumber.getText().toString().trim();//trim()过滤空格
                if (!TextUtils.isEmpty(number)) {
                    String address = AddressDao.getAddress(number);
                    tvResult.setText(address);
                } else {
                    Animation shake = AnimationUtils.loadAnimation(AddressActivity.this, R.anim.shake);
                    etNumber.startAnimation(shake);
                    vibrate();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 手机震动,需要权限VIBRATE
     */
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//        vibrator.vibrate(2000);//平行震动两秒
//        先等待1秒，再震动两秒，再等待一秒，再震动3秒，参数2等于-1，表示只执行一次，不循环，等于0，表示从头循环
//        参数2表示从第几个位置开始循环
        vibrator.vibrate(new long[]{1000, 2000, 1000, 3000}, -1);
//      vibrator.cancel();//取消震动
    }
}
