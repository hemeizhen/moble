package moblesafe.ncs.yeyy.moblesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import moblesafe.ncs.yeyy.moblesafe.R;
import moblesafe.ncs.yeyy.moblesafe.utils.StreamUtils;

/**
 * SplashActivity 闪屏界面
 */
public class SplashActivity extends Activity {

    protected static final int CODE_UPDATE_DIALOG = 0;
    protected static final int CODE_URL_ERROR = 1;
    protected static final int CODE_NET_ERROR = 2;
    protected static final int CODE_JSON_ERROR = 3;
    protected static final int CODE_ENTER_HOME = 4;//进入到主界面

    private TextView tvVersion;//显示版本
    private TextView tvProgressBar;//下载进度展示
    //服务器返回信息
    private String mVersionName;//版本名
    private int mVersionCode;//版本号
    private String mDesc;//版本描述信息
    private String mDownloadUrl;//新版本下载地址

    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case CODE_UPDATE_DIALOG://发消息弹出dialog，子线程中不能更新UI
                    showUpdateDailog();
                    break;
                case CODE_URL_ERROR:
                    Toast.makeText(SplashActivity.this, "url错误", Toast.LENGTH_LONG).show();
                    enterHome();
                    break;
                case CODE_NET_ERROR:
                    Toast.makeText(SplashActivity.this, "网络错误", Toast.LENGTH_LONG).show();
                    enterHome();
                    break;
                case CODE_JSON_ERROR:
                    Toast.makeText(SplashActivity.this, "数据解析错误", Toast.LENGTH_LONG).show();
                    enterHome();
                    break;
                case CODE_ENTER_HOME:
                    enterHome();
                    break;
                default:
                    break;
            }
        }
    };
    private SharedPreferences mPref;
    private RelativeLayout rlRoot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvProgressBar = (TextView) findViewById(R.id.tv_progressBar);
        rlRoot = (RelativeLayout) findViewById(R.id.rl_root);
        tvVersion.setText("版本号：" + getVersionName());
        sharedPreferences();
        alphaAnimation();
        createShortcut();
    }

    /**
     * 创建快捷方式
     */
    private void createShortcut() {
        Intent intent=new Intent();
//         告诉系统想创建快捷方式
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //如果设置为true表示可以创建重复的快捷方式
        intent.putExtra("duplicate", false);
        /**
         * 1.干什么事情
         * 2.你叫什么名字
         * 3.你长成什么样子
         */
//        你长成什么样子
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
//         叫什么名字
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"黑马手机卫士");
//        干什么事情
//        这个地方不能使用显示意图，必须使用隐式意图
        Intent shortcut_intent=new Intent();
        shortcut_intent.setAction("aaa.bbb.ccc");
        shortcut_intent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,shortcut_intent);
        sendBroadcast(intent);
    }

    /**
     * 判断版本是否需要更新
     */
    private void sharedPreferences() {
        //        判断是否需要更新
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        copyDB("address.db");//拷贝联系人数据库
        boolean autoUpdate = mPref.getBoolean("auto_update", true);
        if (autoUpdate) {
            checkVersion();
        } else {
//            如果当你设置更改为默认不更新的时候，如果不延时跳转到home界面，那么永远在闪屏界面了
            mHandler.sendEmptyMessageDelayed(CODE_ENTER_HOME, 2000);//发送延时两秒后发送消息
        }
    }

    /**
     * 渐变的动画效果
     */
    private void alphaAnimation() {
        AlphaAnimation anim = new AlphaAnimation(0.3f, 1f);
        anim.setDuration(2000);
        rlRoot.startAnimation(anim);
    }

    /**
     * 动态获取程序的版本名称
     *
     * @return
     */
    private String getVersionName() {
//        获取版本源，自己掉api时的快捷键
        PackageManager packageManager = getPackageManager();
        try {
            //获取包信息：当前应用程序的包名getPackageName()相当于"moblesafe.ncs.yeyy.moblesafe"，flags标记
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            return versionName;
//            Log.i("versionCode",versionCode+"");
//            Log.i("versionName",versionName+"");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取版本号
     *
     * @return
     */
    private int getVersionCode() {
//        获取版本源，自己掉api时的快捷键
        PackageManager packageManager = getPackageManager();
        try {
            //获取包信息：当前应用程序的包名getPackageName()相当于"moblesafe.ncs.yeyy.moblesafe"，flags标记
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;//同上写空会有问题的，给一个-1
    }

    /**
     * 从服务器获取版本信息进行校验
     */
//网络访问不可以放在主线程中，主线程超过五秒就会阻塞
    private void checkVersion() {
        final long startTime = System.currentTimeMillis();//获取当前时间的毫秒数
//启动子线程异步加载数据
        new Thread() {
            @Override
            public void run() {//启动run方法
                Message msg = Message.obtain();//拿到消息
                HttpURLConnection conn = null;
                try {
//        tomcat    请求本机地址localhost，但是如果用模拟器加载本机的地址是，可以用10.0.2.2来替换
                    URL url = new URL("http://10.0.2.2:8080/update.json");
//                    请求建立连接  此处的urlConnection对象实际上是根据URL的请求协议(此处是http)生成的URLConnection类
//                    的子类HttpURLConnection,故此处最好将其转化为HttpURLConnection类型的对象,以便用到HttpURLConnection更多的API
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");//设置请求方法
                    conn.setConnectTimeout(5000);//设置连接超时
                    conn.setReadTimeout(5000);//设置从主机读取数据超时，设置响应超时，连接上了，但是服务器迟迟不给响应
                    conn.connect();//连接服务器

                    int responseCode = conn.getResponseCode();//获取响应码
                    if (responseCode == 200) {
                        InputStream inputStream = conn.getInputStream();
                        String result = StreamUtils.readFromStream(inputStream);

//                        解析json
                        JSONObject jo = new JSONObject(result);
                        mVersionName = jo.getString("versionName");
                        mVersionCode = jo.getInt("versionCode");
                        mDesc = jo.getString("description");
                        mDownloadUrl = jo.getString("downloadUrl");

//                        判断是否有更新
                        if (mVersionCode > getVersionCode()) {
//                            服务器的VersionCode大于本地的VersionCode，说明有更新，弹出升级对话框
                            msg.what = CODE_UPDATE_DIALOG;
//                            showUpdateDailog();
                        } else {//没有版本更新
//                            enterHome();
                            msg.what = CODE_ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {
//            url异常
                    msg.what = CODE_URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {
//            网络异常
                    msg.what = CODE_NET_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
//                    json解析失败
                    msg.what = CODE_JSON_ERROR;
                    e.printStackTrace();
                } finally {
                    {
                        long endTime = System.currentTimeMillis();
                        long timeUsed = endTime - startTime;//访问网络花费的时间
                        if (timeUsed < 2000) {
                            try {
//                                强制休眠一段时间，保证闪屏页展示两秒钟
                                Thread.sleep(2000 - timeUsed);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        mHandler.sendMessage(msg);
                        if (conn != null) {
                            conn.disconnect();//关闭网络连接
                        }
                    }
                }
//                super.run();
            }
        }.start();


    }

    /**
     * 升级对话框
     */
    private void showUpdateDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("最新版本：" + mVersionName);
        builder.setMessage(mDesc);
//        builder.setCancelable(false);//不让用户点返回键取消对话框，尽量不用，用户体验太差
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                download();

            }
        });

        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        //设置取消的监听，用户点击返回键时会触发
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });
        builder.show();

    }

    /**
     * 下载apk文件，用xUtils框架
     */
    private void download() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            tvProgressBar.setVisibility(View.VISIBLE);//下载时显示进度
            //        获取sdk的根目录
            String target = Environment.getExternalStorageState() + "/update.apk";
            //          xUtils
            HttpUtils utils = new HttpUtils();
            utils.download(mDownloadUrl, target, new RequestCallBack<File>() {
                //下载文件的进度，,该方法在主线程中运行。total整个文件的大小，current当前下载进度，isUploading文件是否上传，返回false
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    tvProgressBar.setText("下载进度：" + current * 100 / total + "%");
                }

                //下载成功,该方法在主线程中运行
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
//                    当下载完成时，跳转到系统下载页面
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(responseInfo.result), "application/vnd.android.package-archive");
//                    startActivity(intent);
                    startActivityForResult(intent, 0);//如用户取消安装，就返回结果，就会回到方法onActivityResult

                }

                //下载失败,该方法在主线程中运行
                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(SplashActivity.this, "没找到SDK", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 进入到主界面
     */
    private void enterHome() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    //用户取消安装，回调此方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 联系人数据库的拷贝
     * @param dbName
     */
    private void copyDB(String dbName) {
//        getFilesDir()获取文件路径
        File destFile = new File(getFilesDir(), dbName);
        if (destFile.exists()){
            return;
        }
        InputStream in=null;//输入流
        FileOutputStream out=null;//输出流
        try {
             in = getAssets().open(dbName);
            out = new FileOutputStream(destFile);
            int len=0;
            byte[] buffer=new byte[1024];
            while ((len=in.read(buffer))!=-1){
                out.write(buffer,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
