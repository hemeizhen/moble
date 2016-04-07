package moblesafe.ncs.yeyy.moblesafe.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import moblesafe.ncs.yeyy.moblesafe.R;
import moblesafe.ncs.yeyy.moblesafe.bean.TaskInfo;

/**
 * Created by yeyy on 2016/3/23.
 */
public class TaskManagerAdapter extends BaseAdapter {
    Context context;
    List<TaskInfo> taskInfos;
    List<TaskInfo> userAppInfos;
    List<TaskInfo> systemAppInfos;

    public TaskManagerAdapter(Context context, List<TaskInfo> taskInfo) {
        this.context = context;
        this.taskInfos = taskInfo;
    }

    public TaskManagerAdapter() {

    }

    @Override
    public int getCount() {
//        return taskInfos.size();
        //        获取到adapter的条目
        userAppInfos = new ArrayList<TaskInfo>();
//                系统程序
        systemAppInfos = new ArrayList<TaskInfo>();
        for (TaskInfo taskInfo : taskInfos) {
//                    用户程序
            if (taskInfo.isUserApp()) {
                userAppInfos.add(taskInfo);
            } else {
                systemAppInfos.add(taskInfo);
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
        TaskInfo taskInfo = null;
        if (position < userAppInfos.size() + 1) {
//            把多余的特殊条目减掉
            taskInfo = userAppInfos.get(position - 1);
        } else {
            int location = userAppInfos.size() + 2;
            taskInfo = systemAppInfos.get(position - location);
        }
        return taskInfo;
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
            textView.setText("用户程序：" + userAppInfos.size() + "个");
            return textView;
//            表示系统程序
        } else if (position == userAppInfos.size() + 1) {
            TextView textView = new TextView(context);
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundColor(Color.GRAY);
            textView.setTextSize(16);
            textView.setText("系统程序：" + systemAppInfos.size() + "个");
            return textView;
        }
//        if (position < userAppInfos.size() + 1) {
////            把多余的特殊条目减掉
//            taskInfo = userAppInfos.get(position - 1);
//        } else {
//            int location = userAppInfos.size() + 2;
//            taskInfo = systemAppInfos.get(position - location);
//        }
        ViewHolder holder = null;
        View view = null;
        if (convertView != null && convertView instanceof RelativeLayout) {
            view = convertView;
            holder = (ViewHolder) view.getTag();

        } else {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(
                    R.layout.task_manager_item, parent, false);
            holder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            holder.tvName = (TextView) view.findViewById(R.id.tv_name);
            holder.tvMemorySize = (TextView) view.findViewById(R.id.tv_app_memory_size);
            holder.cbStatus = (CheckBox) view.findViewById(R.id.cb_app_status);
            view.setTag(holder);
        }
        TaskInfo taskInfo = null;
        if (position<(userAppInfos.size()+1)){
//            用户程序
            taskInfo=userAppInfos.get(position-1);
        }else {
//            系统程序
            int location=position-1-userAppInfos.size()-1;
            taskInfo=systemAppInfos.get(location);
        }
        holder.ivIcon.setImageDrawable(taskInfo.getIcon());
        holder.tvName.setText(taskInfo.getAppName());
        holder.tvMemorySize.setText("内存占用:"+Formatter.formatFileSize(context, taskInfo.getMemorySize()));

        if (taskInfo.isChecked()){
            holder.cbStatus.setChecked(true);
        }else {
            holder.cbStatus.setChecked(false);
        }
        return view;
    }

   public class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvMemorySize;
        CheckBox cbStatus;
    }
}
