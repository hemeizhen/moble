package moblesafe.ncs.yeyy.moblesafe.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import moblesafe.ncs.yeyy.moblesafe.R;
import moblesafe.ncs.yeyy.moblesafe.adapter.AppManagerAdapter;
import moblesafe.ncs.yeyy.moblesafe.bean.AppInfo;
import moblesafe.ncs.yeyy.moblesafe.engine.AppInfos;

/**
 * 软件管理
 */
public class AppManagerActivity extends Activity implements View.OnClickListener {
    @ViewInject(R.id.list_view)
    private ListView list_view;
    @ViewInject(R.id.tv_rom)
    private TextView tv_rom;
    @ViewInject(R.id.tv_sd)
    private TextView tv_sd;
    @ViewInject(R.id.tv_app)
    private TextView tvApp;


    private List<AppInfo> appInfos;
    private List<AppInfo> userAppInfos;
    private List<AppInfo> systemAppInfos;
    private PopupWindow popupWindow;
    private AppInfo clickAppInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
        initData();
        onClick();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AppManagerAdapter adapter = new AppManagerAdapter(AppManagerActivity.this, appInfos);
//            AppManagerAdapter adapter = new AppManagerAdapter();
            list_view.setAdapter(adapter);
        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {
//                获取到所有安装到手机上面的应用程序
                appInfos = AppInfos.getAppInfos(AppManagerActivity.this);
//                appInfos拆开成用户程序的集合+系统程序的集合
//                用户程序
                userAppInfos = new ArrayList<AppInfo>();
//                系统程序
                systemAppInfos = new ArrayList<AppInfo>();
                for (AppInfo appInfo : appInfos) {
//                    用户程序
                    if (appInfo.isUserApp()) {
                        userAppInfos.add(appInfo);
                    } else {
                        systemAppInfos.add(appInfo);
                    }
                }
//                要发消息接收消息，一般情况下发送一个空消息
//                Message obtain=Message.obtain();
//                handler.sendMessage(obtain);
//                空消息
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        setContentView(R.layout.activity_app_manager);
        ViewUtils.inject(this);
//        获取到肉麻内存的运行的剩余空间
        long rom_freeSpace = Environment.getDataDirectory().getFreeSpace();
//        获取到SD卡的剩余空间
        long sd_freeSpace = Environment.getExternalStorageDirectory().getFreeSpace();

        tv_rom.setText("内存可用:" + Formatter.formatFileSize(this, rom_freeSpace));
        tv_sd.setText("sd卡可用:" + Formatter.formatFileSize(this, sd_freeSpace));

//        卸载要发送广播
        UninstallReceiver receiver = new UninstallReceiver();
//        过滤器删除一个包名
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");//设置数据类型
        registerReceiver(receiver, intentFilter);//注册号广播了
    }

    private void onClick() {
//        设置listView的滚动监听
        list_view.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             *
             * @param view
             * @param firstVisibleItem 第一个课件的条目的位置
             * @param visibleItemCount 一页可以展示多少个条目
             * @param totalItemCount 总共的item的个数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                popupWindowDismiss();
                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem > (userAppInfos.size()) + 1) {
//                    系统应用程序
                        tvApp.setText("系统程序(" + systemAppInfos.size() + ")个");
                    } else {
//                    用户应用程序
                        tvApp.setText("用户程序(" + userAppInfos.size() + ")个");
                    }
                }

            }
        });

//        点击listview item 实现功能,item整体点击事件和item中条目的Button相互冲突，
// 解决方案在Item布局的根布局加上android:descendantFocusability=”blocksDescendants”的属性就好了
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取到当前点击的item对象
                Object obj = list_view.getItemAtPosition(position);

                if (obj != null && obj instanceof AppInfo) {

//                    强转成AppInfo对象
                    clickAppInfo = (AppInfo) obj;

                    View contentView = View.inflate(AppManagerActivity.this, R.layout.item_popup, null);

                    LinearLayout ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
                    LinearLayout ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);
                    LinearLayout ll_start = (LinearLayout) contentView.findViewById(R.id.ll_start);
                    LinearLayout ll_detail = (LinearLayout) contentView.findViewById(R.id.ll_detail);
                    ll_uninstall.setOnClickListener(AppManagerActivity.this);
                    ll_share.setOnClickListener(AppManagerActivity.this);
                    ll_start.setOnClickListener(AppManagerActivity.this);
                    ll_detail.setOnClickListener(AppManagerActivity.this);

                    popupWindowDismiss();
                    // -2表示包裹内容
                    popupWindow = new PopupWindow(contentView, -2, -2);
                    //需要注意：使用PopupWindow 必须设置背景。不然没有动画
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    //占用x，y
                    int[] location = new int[2];
                    //获取view展示到窗体上面的位置
                    view.getLocationInWindow(location);

                    popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 70, location[1]);

                    //从小变大的动画
                    ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

                    sa.setDuration(3000);

                    contentView.startAnimation(sa);


                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //分享
            case R.id.ll_share:

                Intent share_localIntent = new Intent("android.intent.action.SEND");
                share_localIntent.setType("text/plain");
                share_localIntent.putExtra("android.intent.extra.SUBJECT", "f分享");
                share_localIntent.putExtra("android.intent.extra.TEXT",
                        "Hi！推荐您使用软件：" + clickAppInfo.getApkName() + "下载地址:" +
                                "https://play.google.com/store/apps/details?id=" + clickAppInfo.getApkPackageName());
                this.startActivity(Intent.createChooser(share_localIntent, "分享"));
                popupWindowDismiss();

                break;

            //运行
            case R.id.ll_start:
                Intent start_localIntent = this.getPackageManager()
                        .getLaunchIntentForPackage(clickAppInfo.getApkPackageName());
                this.startActivity(start_localIntent);
                popupWindowDismiss();
                break;
            //卸载
            case R.id.ll_uninstall:
                Intent uninstall_localIntent = new Intent("android.intent.action.DELETE", Uri.parse("package:" + clickAppInfo.getApkPackageName()));
//                Intent uninstall_localIntent = new Intent();
//                uninstall_localIntent.setAction("android.intent.action.VIEW");
//                uninstall_localIntent.addCategory("android.intent.category.DEFAULT");
//                uninstall_localIntent.setData(Uri.parse("package:" + clickAppInfo.getApkPackageName()));
                startActivity(uninstall_localIntent);
                popupWindowDismiss();
                break;
//          详细信息
            case R.id.ll_detail:
                Intent detail_intent = new Intent();
                detail_intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
                detail_intent.setData(Uri.parse("package:" + clickAppInfo.getApkPackageName()));
                startActivity(detail_intent);
                break;

            default:
                break;
        }
    }

    private class UninstallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("接收到卸载的广播");
        }
    }

    private void popupWindowDismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }


    @Override
    protected void onDestroy() {
        popupWindowDismiss();

        super.onDestroy();
    }
//    private class AppManagerAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return appInfos.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return appInfos.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder viewHolder = null;
//            View view = convertView;
//            if (view == null) {
//                viewHolder = new ViewHolder();
//                view = View.inflate(AppManagerActivity.this, R.layout.app_manager_item, null);
//                viewHolder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
//                viewHolder.tvName = (TextView) view.findViewById(R.id.tv_name);
//                viewHolder.tvLocation = (TextView) view.findViewById(R.id.tv_location);
//                viewHolder.tvApkSize = (TextView) view.findViewById(R.id.tv_apk_size);
//                view.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) view.getTag();
//            }
//            AppInfo appInfo = appInfos.get(position);
//            viewHolder.ivIcon.setBackground(appInfo.getApkIcon());
//            viewHolder.tvApkSize.setText(Formatter.formatFileSize(AppManagerActivity.this, appInfo.getApkSize()));
//            viewHolder.tvName.setText(appInfo.getApkName());
//            if (appInfo.isRom()) {
//                viewHolder.tvLocation.setText("手机内存");
//            } else {
//
//
//            }
//            return view;
//        }
//
//        class ViewHolder {
//            ImageView ivIcon;
//            TextView tvName;
//            TextView tvLocation;
//            TextView tvApkSize;
//        }
//    }

}
