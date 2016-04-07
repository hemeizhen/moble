package moblesafe.ncs.yeyy.moblesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import moblesafe.ncs.yeyy.moblesafe.R;
import moblesafe.ncs.yeyy.moblesafe.adapter.HomeGridViewAdapter;
import moblesafe.ncs.yeyy.moblesafe.utils.MD5Utils;

/**
 * 手机卫士主界面
 */
public class HomeActivity extends Activity implements AdapterView.OnItemClickListener {

    private GridView gvHome;
    private HomeGridViewAdapter homeGridViewAdapter;
    private String[] mItems = {"手机防盗", "通讯卫士", "软件管理", "进程管理",
            "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};
    private int[] mPics = {R.drawable.home_safe,
            R.drawable.home_callmsgsafe, R.drawable.home_apps,
            R.drawable.home_taskmanager, R.drawable.home_netmanager,
            R.drawable.home_trojan, R.drawable.home_sysoptimize,
            R.drawable.home_tools, R.drawable.home_settings};


    //    private EditText etPassword;
    private SharedPreferences mPref;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        initView();

//        gvHome.setAdapter(new HomeGridViewAdapter);
    }

    private void initView() {
        gvHome = (GridView) findViewById(R.id.gv_home);

        homeGridViewAdapter = new HomeGridViewAdapter(HomeActivity.this, mItems, mPics);
        gvHome.setAdapter(homeGridViewAdapter);

        gvHome.setOnItemClickListener(HomeActivity.this);
    }

    //    设置监听
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
//                手机防盗
                showPasswordDialog();
//                启动手机防盗页面
//                startActivity(new Intent(HomeActivity.this,LostFindActivity.class));
                break;
            case 1:
//                通讯卫士
                startActivity(new Intent(HomeActivity.this,CallSafeActivity.class));
                break;
            case 2:
//                软件管理
                startActivity(new Intent(HomeActivity.this,AppManagerActivity.class));
                break;
            case 3:
//                进程管理
                startActivity(new Intent(HomeActivity.this,TaskManagerActivity.class));
                break;
            case 7:
//                启动高级设置页面
                startActivity(new Intent(HomeActivity.this,AToolsActivity.class));
                break;
            case 8:
//                设置中心
                Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    /**
     * 手机防盗显示密码弹窗
     */
    private void showPasswordDialog() {
//        判断是否设置密码
        String savePassword = mPref.getString("password", null);
//        输入密码弹窗
        if (!TextUtils.isEmpty(savePassword)) {
            showPasswordInputDialog();
        } else {
//        如果没有设置过，弹出设置密码的弹窗
            showPasswordSetDialog();
        }
    }


    /**
     * 如果没有设置密码，设置密码的弹窗
     */
    private void showPasswordSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_set_password, null);
//        dialog.setView(view);//将自定义的布局文件设置给dialog
        dialog.setView(view, 0, 0, 0, 0);//设置边距为0，保证在2.X的版本上没问题

        final EditText etPassword = (EditText) view.findViewById(R.id.et_password);
        final EditText etPasswordConfirm = (EditText) view.findViewById(R.id.et_password_confirm);

        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String passwordConfirm = etPasswordConfirm.getText().toString();
//                password!=null&&!password.equals("")
                if (!TextUtils.isEmpty(password) && !passwordConfirm.isEmpty()) {
                    if (password.equals(passwordConfirm)) {
                        Toast.makeText(HomeActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                        mPref.edit().putString("password", MD5Utils.encode(password)).commit();//将密码保存起来
                        dialog.dismiss();
                        //                启动手机防盗页面
                        startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "输入框内容不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();//隐藏dialog
            }
        });

        dialog.show();
        fullScreenDialog();
    }

    /**
     * 让自定义的dialog填充整个屏幕
     */
    private void fullScreenDialog() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }

    /**
     * 如果设置密码了，设置输入密码的弹窗
     */
    private void showPasswordInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_input_password, null);
        dialog.setView(view, 0, 0, 0, 0);//设置边距为0，保证在2.X的版本上没问题

        final EditText etPassword = (EditText) view.findViewById(R.id.et_password);
        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                if (!TextUtils.isEmpty(password)) {
                    String savePassword = mPref.getString("password", null);
                    if (MD5Utils.encode(password).equals(savePassword)) {
                        Toast.makeText(HomeActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
//                启动手机防盗页面
                        startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "输入框内容不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();//隐藏dialog
            }
        });
        dialog.show();
        fullScreenDialog();
    }


//    都写在一个类里面是不好的
//    class HomeGridViewAdapter extends BaseAdapter
//
//    {
////        Context context;
//
//
//        @Override
//        public int getCount() {
//            return mItems.length;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return mItems[position];
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {

//            HolderView holderView = null;
//            View view = convertView;
//            if (view == null) {
//                holderView = new HolderView();
//                view = LayoutInflater.from(HomeActivity.this).inflate(
//                        R.layout.home_gridview_item, parent, false);
//                holderView.ivItem = (ImageView) view.findViewById(R.id.iv_item_home);
//                holderView.tvItem = (TextView) view.findViewById(R.id.tv_item_home);
//                view.setTag(holderView);
//            } else {
//                holderView = (HolderView) view.getTag();
//            }
//            holderView.tvItem.setText(mItems[position]);
//            holderView.ivItem.setImageResource(mPics[position]);
//
//
//            return view;
//        }
//
//        class HolderView {
//            ImageView ivItem;
//            TextView tvItem;
//
//        }
//    }
}
