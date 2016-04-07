package moblesafe.ncs.yeyy.moblesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import moblesafe.ncs.yeyy.moblesafe.R;
import moblesafe.ncs.yeyy.moblesafe.service.KillProcessService;
import moblesafe.ncs.yeyy.moblesafe.utils.SharedPreferencesUtils;
import moblesafe.ncs.yeyy.moblesafe.utils.SystemInfoUtils;

/**
 * 进程管理设置界面
 */
public class TaskManagerSettingActivity extends Activity {

    private CheckBox cbStatus;
    private SharedPreferences sp;
    private CheckBox cbKillProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager_setting);
        initUI();
        onClick();
    }

    private void onClick() {
//        0表示私有的模式(MODE_PRIVATE)
//        sp = getSharedPreferences("config",MODE_PRIVATE);
//        设置是否选中
//        cbStatus.setChecked(sp.getBoolean("is_show_system",false));
        cbStatus.setChecked(SharedPreferencesUtils.getBoolean(TaskManagerSettingActivity.this, "is_show_system", false));
        cbStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    SharedPreferencesUtils.saveBoolean(TaskManagerSettingActivity.this, "is_show_system", true);
//                    Editor edit=sp.edit();
//                    edit.putBoolean("is_show_system",true);
//                    edit.commit();
                } else {
//                    Editor edit=sp.edit();
//                    edit.putBoolean("is_show_system",false);
//                    edit.commit();
                    SharedPreferencesUtils.saveBoolean(TaskManagerSettingActivity.this, "is_show_system", false);
                }
            }
        });


//        传进来定时清理的服务
        final Intent intent=new Intent(this, KillProcessService.class);
        cbKillProcess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    startService(intent);
                }else {
                    stopService(intent);
                }
            }
        });
    }

    private void initUI() {
        cbStatus = (CheckBox) findViewById(R.id.cb_status);
        cbKillProcess = (CheckBox) findViewById(R.id.cb_status_kill_process);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (SystemInfoUtils.isServiceRunning(TaskManagerSettingActivity.this,"moblesafe.ncs.yeyy.moblesafe.service")){
        cbKillProcess.setChecked(true);
        }else {
            cbKillProcess.setChecked(false);
        }
    }
}
