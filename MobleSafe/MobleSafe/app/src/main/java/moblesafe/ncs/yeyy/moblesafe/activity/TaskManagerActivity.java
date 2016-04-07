package moblesafe.ncs.yeyy.moblesafe.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import moblesafe.ncs.yeyy.moblesafe.R;
import moblesafe.ncs.yeyy.moblesafe.bean.TaskInfo;
import moblesafe.ncs.yeyy.moblesafe.engine.TaskInfoParser;
import moblesafe.ncs.yeyy.moblesafe.utils.SharedPreferencesUtils;
import moblesafe.ncs.yeyy.moblesafe.utils.SystemInfoUtils;
import moblesafe.ncs.yeyy.moblesafe.utils.UIUtils;

/**
 * 进程管理
 */
public class TaskManagerActivity extends Activity implements View.OnClickListener {
    @ViewInject(R.id.tv_task_process_count)
    TextView tvTaskCount;
    @ViewInject(R.id.tv_task_memory)
    TextView tvTaskMemory;
    @ViewInject(R.id.list_view)
    ListView listView;
    @ViewInject(R.id.btn_check_all)
    Button btnCheckAll;
    @ViewInject(R.id.btn_select_oppsite)
    Button btnSelectOppsite;
    @ViewInject(R.id.btn_kill_process)
    Button btnKillProcess;
    @ViewInject(R.id.btn_setting)
    Button btnSetting;
    private List<TaskInfo> taskInfos;
    private List<TaskInfo> userTaskInfos;
    private List<TaskInfo> systemTaskInfos;
    private TaskManagerAdapter adapter;
    private int processCount;
    private long availMem;
    private long totalMem;
    private SharedPreferences sp;

    private String packageName="000000000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        ViewUtils.inject(this);
        sp = getSharedPreferences("config", 0);
        initUI();
        initData();

    }

//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            TaskManagerAdapter adapter = new TaskManagerAdapter();
//
//        }
//    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                taskInfos = TaskInfoParser.getTaskInfos(TaskManagerActivity.this);

                userTaskInfos = new ArrayList<TaskInfo>();
                systemTaskInfos = new ArrayList<TaskInfo>();
                for (TaskInfo taskInfo : taskInfos) {
                    if (taskInfo.isUserApp()) {
                        userTaskInfos.add(taskInfo);
                    } else {
                        systemTaskInfos.add(taskInfo);
                    }
                }

//                handler.sendEmptyMessage(0);同handler发送接收消息是一致的
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        adapter = new TaskManagerAdapter(TaskManagerActivity.this, taskInfos);
                        adapter = new TaskManagerAdapter();
                        listView.setAdapter(adapter);
                    }
                });
            }
        }.start();

    }

    /**
     * ActivityManager
     * 任务管理器
     * packageManager
     * 包管理器
     */
    @SuppressLint("NewApi")
    private void initUI() {

        processCount = SystemInfoUtils.getProcessCount(this);
        tvTaskCount.setText("进程:" + processCount + "个");

        availMem = SystemInfoUtils.getAvailMem(this);

        totalMem = SystemInfoUtils.getTotalMem(this);

        tvTaskMemory.setText("剩余/总内存" + Formatter.formatFileSize(TaskManagerActivity.this, availMem) + "/"
                + Formatter.formatFileSize(TaskManagerActivity.this, totalMem));

        btnCheckAll.setOnClickListener(this);
        btnSelectOppsite.setOnClickListener(this);
        btnKillProcess.setOnClickListener(this);
        btnSetting.setOnClickListener(this);

//        View view = View.inflate(TaskManagerActivity.this, R.layout.task_manager_item, null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                得到当前点击listview的对象
                Object object = listView.getItemAtPosition(position);
                if (object != null && object instanceof TaskInfo) {
                    TaskInfo taskInfo = (TaskInfo) object;
                    TaskManagerAdapter.ViewHolder holder = (TaskManagerAdapter.ViewHolder) view.getTag();

//                    if (taskInfo.getPackageName().equals(getPackageName())){
//                        return;
//                    }

//                    判断当前的item是否被勾选上
                    /**
                     * 如果被勾选上了，那么就改成没有勾选
                     * 如果没有勾选，就改成已经勾选
                     */
                    if (taskInfo.isChecked()) {
                        taskInfo.setChecked(false);
                        holder.cbStatus.setChecked(false);
                    } else {
                        taskInfo.setChecked(true);
                        holder.cbStatus.setChecked(true);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
//            全选
            case R.id.btn_check_all:
                for (TaskInfo taskInfo : userTaskInfos) {

//            判断当前的用户程序是不是自己的程序。如果是自己的程序。那么就把文本框隐藏
//                    getPackageName空
                    if (taskInfo.getPackageName().equals(getPackageName())) {
                        continue;
                    }

                    taskInfo.setChecked(true);
//                    Log.i("taskInfo.getPackageName()-------", "taskInfo.getPackageName()-------" + taskInfo.getPackageName());
//                    Log.i("taskInfo.getPackageName()+++++++++","taskInfo.getPackageName()+++++++++"+ getPackageName());
                }
                for (TaskInfo taskInfo : systemTaskInfos) {
                    taskInfo.setChecked(true);
                }
//                一定要注意，一旦数据发生改变一定要刷新
                adapter.notifyDataSetChanged();
                break;
//            反选
            case R.id.btn_select_oppsite:
                for (TaskInfo taskInfo : userTaskInfos) {
                    // 判断当前的用户程序是不是自己的程序。如果是自己的程序。那么就把文本框隐藏

                    if (taskInfo.getPackageName().equals(getPackageName())) {
                        continue;
                    }
                    taskInfo.setChecked(!taskInfo.isChecked());
                }
                for (TaskInfo taskInfo : systemTaskInfos) {
                    taskInfo.setChecked(!taskInfo.isChecked());
                }
                adapter.notifyDataSetChanged();
                break;
//            清理
            case R.id.btn_kill_process:
//                想杀死进程就必须先得到进程管理器
//                Caused by:java.util.ConcurrentModificationException
//                杀死进程的会报的错误
                ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

//                清理进程的集合
                List<TaskInfo> killLists = new ArrayList<TaskInfo>();
//                清理总共的进程个数
                int totalCount = 0;
//                清理进程的大小
                int killMem = 0;

                for (TaskInfo taskInfo : userTaskInfos) {
                    if (taskInfo.isChecked()) {
                        killLists.add(taskInfo);
//                        userTaskInfos.remove(taskInfo);
                        totalCount++;
                        //                杀死进程,参数表示包名
                        killMem += taskInfo.getMemorySize();

                    }
                }

                for (TaskInfo taskInfo : systemTaskInfos) {
                    if (taskInfo.isChecked()) {
                        killLists.add(taskInfo);
//                        systemTaskInfos.remove(taskInfo);
                        totalCount++;
                        killMem += taskInfo.getMemorySize();
                        //                杀死进程,参数表示包名
                        activityManager.killBackgroundProcesses(taskInfo.getPackageName());
                    }

                }

                /**
                 * 注意
                 * 当集合在迭代的时候，不能修改集合的大小
                 */
                for (TaskInfo taskInfo : killLists) {
                    if (taskInfo.isUserApp()) {
                        userTaskInfos.remove(taskInfo);
                        activityManager.killBackgroundProcesses(taskInfo.getPackageName());
//                        Log.i("清理", "清理" + taskInfo.getPackageName());


                    } else {
                        systemTaskInfos.remove(taskInfo);
                        activityManager.killBackgroundProcesses(taskInfo.getPackageName());

                    }
                }
                UIUtils.showToast(TaskManagerActivity.this, "共清理了" + totalCount + "个进程,释放了" +
                        Formatter.formatFileSize(TaskManagerActivity.this, killMem) + "内存");

//                processCount 表示总共有多少进程
//                totalCount  当前清理了多少个进程
                processCount -= totalCount;
                tvTaskCount.setText("进程" + processCount + "个");

//
                tvTaskMemory.setText("剩余/总内存:"
                        + Formatter.formatFileSize(TaskManagerActivity.this, availMem + killMem)
                        + "/"
                        + Formatter.formatFileSize(TaskManagerActivity.this, totalMem));
                ;


                adapter.notifyDataSetChanged();
                break;
//            设置
            case R.id.btn_setting:
                startActivity(new Intent(TaskManagerActivity.this, TaskManagerSettingActivity.class));
                break;
            default:
                break;
        }
    }

    public class TaskManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
//        return taskInfos.size();
//            //        获取到adapter的条目
//            userTaskInfos = new ArrayList<TaskInfo>();
////                系统程序
//            systemTaskInfos = new ArrayList<TaskInfo>();
//            for (TaskInfo taskInfo : taskInfos) {
////                    用户程序
//                if (taskInfo.isUserApp()) {
//                    userTaskInfos.add(taskInfo);
//                } else {
//                    systemTaskInfos.add(taskInfo);
//                }
//            }

            /**
             * 判断当前用户是否需要展示系统程序
             * 如果需要就全部显示
             * 如果不需要就展示用户进程
             */
//            boolean result=sp.getBoolean("is_show_system",false);
            boolean result = SharedPreferencesUtils.getBoolean(TaskManagerActivity.this, "is_show_system", false);
            if (result) {
                return userTaskInfos.size() + 1 + systemTaskInfos.size() + 1;

            } else {
                return userTaskInfos.size() + 1;
            }
        }

        @Override
        public Object getItem(int position) {
            //        0的位置是特殊条目，不是一个对象
            if (position == 0) {
                return null;
            } else if (position == userTaskInfos.size() + 1) {
                return null;
            }
            TaskInfo taskInfo = null;
            if (position < userTaskInfos.size() + 1) {
//            把多余的特殊条目减掉
                taskInfo = userTaskInfos.get(position - 1);
            } else {
                int location = userTaskInfos.size() + 2;
                taskInfo = systemTaskInfos.get(position - location);
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
                TextView textView = new TextView(TaskManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setTextSize(16);
                textView.setText("用户程序：" + userTaskInfos.size() + "个");
                return textView;
//            表示系统程序
            } else if (position == userTaskInfos.size() + 1) {
                TextView textView = new TextView(TaskManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setTextSize(16);
                textView.setText("系统程序：" + systemTaskInfos.size() + "个");
                return textView;
            }

            ViewHolder holder = null;
            View view = null;
            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();

            } else {
                holder = new ViewHolder();
                view = LayoutInflater.from(TaskManagerActivity.this).inflate(
                        R.layout.task_manager_item, parent, false);
                holder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tvName = (TextView) view.findViewById(R.id.tv_name);
                holder.tvMemorySize = (TextView) view.findViewById(R.id.tv_app_memory_size);
                holder.cbStatus = (CheckBox) view.findViewById(R.id.cb_app_status);
                view.setTag(holder);
            }
            TaskInfo taskInfo = null;
            if (position < (userTaskInfos.size() + 1)) {
//            用户程序
                taskInfo = userTaskInfos.get(position - 1);
                Log.d("用户程序","用户程序"+taskInfo);
            } else {
//            系统程序
                int location = position - 1 - userTaskInfos.size() - 1;
                taskInfo = systemTaskInfos.get(location);
            }
            holder.ivIcon.setImageDrawable(taskInfo.getIcon());
            holder.tvName.setText(taskInfo.getAppName());
            holder.tvMemorySize.setText("内存占用:" + Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.getMemorySize()));

            if (taskInfo.isChecked()) {
                holder.cbStatus.setChecked(true);
            } else {
                holder.cbStatus.setChecked(false);
            }

//            判断当前展示的item是否是自己的程序，如果是，就把程序给隐藏
//            if (taskInfo.getPackageName().equals(getPackageName())){
//                holder.cbStatus.setVisibility(View.INVISIBLE);
//            }else {
//                holder.cbStatus.setVisibility(View.VISIBLE);
//            }
            return view;
        }

        public class ViewHolder {
            ImageView ivIcon;
            TextView tvName;
            TextView tvMemorySize;
            CheckBox cbStatus;
        }
    }

    //    当用户设置完要显示的进程后，返回到上一界面，
// 需要刷新界面，不会走onCreate方法
    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
