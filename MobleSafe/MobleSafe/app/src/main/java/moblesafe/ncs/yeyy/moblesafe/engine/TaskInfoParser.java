package moblesafe.ncs.yeyy.moblesafe.engine;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

import java.util.ArrayList;
import java.util.List;

import moblesafe.ncs.yeyy.moblesafe.R;
import moblesafe.ncs.yeyy.moblesafe.bean.TaskInfo;

/**
 * 进程管理
 * Created by yeyy on 2016/3/23.
 */
public class TaskInfoParser {
    public static List<TaskInfo> getTaskInfos(Context context) {

        PackageManager packageManager = context.getPackageManager();
        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
//        获取到进程管理器
        ActivityManager activityManage = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
//        获取到手机上吗所有运行的进程
        List<RunningAppProcessInfo> appProcess = activityManage.getRunningAppProcesses();
        for (RunningAppProcessInfo runningAppProcessInfo : appProcess) {

            TaskInfo taskInfo = new TaskInfo();
//            获取到进程的名字，实际上就是包名
            String processName = runningAppProcessInfo.processName;
            try {
//                获取到内存基本信息
                /**
                 * 这里面一共只有一个数据
                 */
                MemoryInfo[] memoryInfo = activityManage.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
//                dirty弄脏,获取到总共弄脏多少内存，就是暂用多少内存的意思
                int totalPrivateDirty = memoryInfo[0].getTotalPrivateDirty() * 1024;
                taskInfo.setMemorySize(totalPrivateDirty);

                PackageInfo packageInfo = packageManager.getPackageInfo(processName, 0);
//                获取到图片
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                taskInfo.setIcon(icon);
//                获取到名字
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                taskInfo.setAppName(appName);

//                获取到当前应用程序的标记
                int flags=packageInfo.applicationInfo.flags;
//                ApplicationInfo.FLAG_SYSTEM表示系统应用程序
                if ((flags& ApplicationInfo.FLAG_SYSTEM )!=0){
//                    系统应用
                    taskInfo.setUserApp(false);
                }else {
//                    用户应用
                    taskInfo.setUserApp(true);
                }

            } catch (Exception e) {
                e.printStackTrace();
//                系统核心库里面有些系统没有图标，必须给一个默认的图标，不然会报错
                taskInfo.setIcon(context.getResources().getDrawable(R.mipmap.ic_launcher));
                taskInfo.setAppName("");
            }
            taskInfos.add(taskInfo);

        }
        return taskInfos;
    }
}
