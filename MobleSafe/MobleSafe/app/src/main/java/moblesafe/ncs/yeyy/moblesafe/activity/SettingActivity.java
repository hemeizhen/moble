package moblesafe.ncs.yeyy.moblesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import moblesafe.ncs.yeyy.moblesafe.R;
import moblesafe.ncs.yeyy.moblesafe.service.AddressService;
import moblesafe.ncs.yeyy.moblesafe.service.CallSafeService;
import moblesafe.ncs.yeyy.moblesafe.utils.SystemInfoUtils;
import moblesafe.ncs.yeyy.moblesafe.view.SettingClickView;
import moblesafe.ncs.yeyy.moblesafe.view.SettingItemView;

/**
 * 设置中心
 */
public class SettingActivity extends Activity implements View.OnClickListener {

    final String[] items = new String[]{"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};
    private SettingItemView sivUpdate;//设置升级
    private SharedPreferences mPref;
    private SettingItemView sivAddress;
    private SettingClickView scvAddressStyle;
    private SettingClickView scvAddressLocation;
    private SettingItemView sivCallSafe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
//        自动更新
        sivUpdate = (SettingItemView) findViewById(R.id.siv_update);
//        sivUpdate.setTvTitle("自动更新设置");
//        sivUpdate.setTvDesc("自动更新已开启");

        sivUpdate.setOnClickListener(SettingActivity.this);
//        归属地开关
        sivAddress = (SettingItemView) findViewById(R.id.siv_address);
        sivAddress.setOnClickListener(SettingActivity.this);

//        修改提示框显示风格
        scvAddressStyle = (SettingClickView) findViewById(R.id.scv_address_style);
        scvAddressStyle.setOnClickListener(SettingActivity.this);

//        修改归属地位置
        scvAddressLocation = (SettingClickView) findViewById(R.id.scv_address_location);
        scvAddressLocation.setOnClickListener(SettingActivity.this);

//        黑名单更新设置
        sivCallSafe = (SettingItemView) findViewById(R.id.siv_callsafe);
        sivCallSafe.setOnClickListener(SettingActivity.this);
        sharedPreferences();
    }

    /**
     * 用SharedPreferences记录设置的结果，下次进入时保持上次设置的情况
     */
    private void sharedPreferences() {
        mPref = getSharedPreferences("config", MODE_PRIVATE);

//        自动更新
        boolean autoUpdate = mPref.getBoolean("auto_update", true);
        if (autoUpdate) {
//            sivUpdate.setTvDesc("自动更新已开启");
            sivUpdate.setChecked(true);

//            更新sharedPreferences
            mPref.edit().putBoolean("auto_update", false).commit();
        } else {
//            sivUpdate.setTvDesc("自动更新已关闭");
            sivUpdate.setChecked(false);
            //            更新sharedPreferences
            mPref.edit().putBoolean("auto_update", true).commit();

        }

//        修改提示框显示风格
        scvAddressStyle.setTvTitle("归属地提示框风格");
        int style = mPref.getInt("address_style", 0);//读取保存的style

        scvAddressStyle.setTvDesc(items[style]);

//        根据归属地服务是否运行来更新checkBox
        boolean serviceRunning = SystemInfoUtils.isServiceRunning(SettingActivity.this,
                "moblesafe.ncs.yeyy.moblesafe.service.AddressService");
        if (serviceRunning) {
            sivAddress.setChecked(true);
        } else {
            sivAddress.setChecked(false);
        }

//        修改归属地位置
        scvAddressLocation.setTvTitle("归属地提示框显示位置");
        scvAddressLocation.setTvDesc("设置归属地提示框的显示位置");

//        黑名单更新设置
        boolean serviceRunning1 = SystemInfoUtils.isServiceRunning(SettingActivity.this,
                "moblesafe.ncs.yeyy.moblesafe.service.CallSafeService");
        if (serviceRunning){
            sivCallSafe.setChecked(true);
        }else {
            sivCallSafe.setChecked(false);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
//            自动更新
            case R.id.siv_update:
                if (sivUpdate.isChecked()) {
                    //设置不勾选
                    sivUpdate.setChecked(false);
//                    sivUpdate.setTvDesc("自动更新已关闭");
                } else {
                    sivUpdate.setChecked(true);
//                    sivUpdate.setTvDesc("自动更新已开启");
                }
                break;
//            归属地开关
            case R.id.siv_address:

                if (sivAddress.isChecked()) {
                    sivAddress.setChecked(false);
                    stopService(new Intent(SettingActivity.this, AddressService.class));//停止归属地服务
                } else {
                    sivAddress.setChecked(true);
                    startService(new Intent(SettingActivity.this, AddressService.class));//开启归属地服务
                }
                break;
//            修改提示框显示风格
            case R.id.scv_address_style:

                showSingleChooseDialog();
                break;
//        修改归属地位置
            case R.id.scv_address_location:
                startActivity(new Intent(SettingActivity.this, DragViewActivity.class));
                break;
//        黑名单更新设置
            case R.id.siv_callsafe:
                if (sivCallSafe.isChecked()) {
                    sivCallSafe.setChecked(false);
                    stopService(new Intent(SettingActivity.this, CallSafeService.class));//停止归属地服务
                } else {
                    sivCallSafe.setChecked(true);
                    startService(new Intent(SettingActivity.this, CallSafeService.class));//开启归属地服务
                }
                break;
            default:
                break;
        }
    }

    /**
     * 弹出选择风格的单选框
     */
    private void showSingleChooseDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);//弹出框设置log图片
        builder.setTitle("归属地提示框风格");

        int style = mPref.getInt("address_style", 0);//读取保存的style


        builder.setSingleChoiceItems(items, style, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPref.edit().putInt("address_style", which).commit();//保存选择风格
                dialog.dismiss();//让dialog消失
                scvAddressStyle.setTvDesc(items[which]);//更新组合控件的描述信息
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
}
