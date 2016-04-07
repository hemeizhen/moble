package moblesafe.ncs.yeyy.moblesafe.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import moblesafe.ncs.yeyy.moblesafe.R;
import moblesafe.ncs.yeyy.moblesafe.bean.AppInfo;

/**
 * Created by yeyy on 2016/3/14.
 */
public class AppManagerAdapter extends BaseAdapter {
    Context context;
    List<AppInfo> appInfos;
    List<AppInfo> userAppInfos;
    List<AppInfo> systemAppInfos;

    public AppManagerAdapter(Context context, List<AppInfo> appInfo) {
        this.context = context;
        this.appInfos = appInfo;
    }

    @Override
    public int getCount() {
//        获取到adapter的条目
//        return appInfos.size();
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
        return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
    }

    @Override
    public Object getItem(int position) {
//        0的位置是特殊条目，不是一个对象
        if (position == 0) {
            return null;
        } else if (position == userAppInfos.size() + 1) {
            return null;
        }
        AppInfo appInfo = null;
        if (position < userAppInfos.size() + 1) {
//            把多余的特殊条目减掉
            appInfo = userAppInfos.get(position - 1);
        } else {
            int location = userAppInfos.size() + 2;
            appInfo = systemAppInfos.get(position - location);
        }
        return appInfo;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        如果当前的position等于0，表示应用程序
        if (position == 0) {
            TextView textView = new TextView(context);
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundColor(Color.GRAY);
            textView.setTextSize(16);
            textView.setText("用户程序（" + userAppInfos.size() + ")");
            return textView;
//            表示系统程序
        } else if (position == userAppInfos.size() + 1) {
            TextView textView = new TextView(context);
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundColor(Color.GRAY);
            textView.setText("系统程序（" + systemAppInfos.size() + ")");
            return textView;
        }

        final AppInfo appInfo;
        if (position < userAppInfos.size() + 1) {
//            把多余的特殊条目减掉
            appInfo = userAppInfos.get(position - 1);
        } else {
            int location = userAppInfos.size() + 2;
            appInfo = systemAppInfos.get(position - location);
        }

        ViewHolder viewHolder = null;
        View view = null;
        if (convertView != null && convertView instanceof RelativeLayout) {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();

        } else {

            view = View.inflate(context, R.layout.app_manager_item, null);
            viewHolder = new ViewHolder();
            viewHolder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            viewHolder.tvName = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.tvLocation = (TextView) view.findViewById(R.id.tv_location);
            viewHolder.tvApkSize = (TextView) view.findViewById(R.id.tv_apk_size);
            viewHolder.btnUninstall = (Button) view.findViewById(R.id.btn_app_uninstall);

            view.setTag(viewHolder);
        }
//        AppInfo appInfo = appInfos.get(position);
        viewHolder.ivIcon.setBackground(appInfo.getApkIcon());
        viewHolder.tvApkSize.setText(Formatter.formatFileSize(context, appInfo.getApkSize()));
        viewHolder.tvName.setText(appInfo.getApkName());
        if (appInfo.isRom()) {
            viewHolder.tvLocation.setText("手机内存");
        } else {
            viewHolder.tvLocation.setText("外部存储");
        }

//                Button 卸载APP
        viewHolder.btnUninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uninstall_localIntent = new Intent("android.intent.action.DELETE", Uri.parse("package:" + appInfo.getApkPackageName()));
                context.startActivity(uninstall_localIntent);
            }
        });
        return view;
    }

    class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvLocation;
        TextView tvApkSize;
        Button btnUninstall;
    }
}
