package moblesafe.ncs.yeyy.moblesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * 把获取到的所有应用的集合用list存起来，系统应用的bean
 * Created by yeyy on 2016/3/10.
 */
public  class AppInfo {
    /**
     * 程序的图标
     */
    private Drawable apkIcon;
    /**
     * 程序的名字
     */
    private String apkName;
    /**
     * 程序的大小
     */
    private long apkSize;
    /**
     * 表示到底是用户app还是系统app
     * 如果是true是用户app
     * 如果是false是系统app
     */
    private boolean userApp;
    /**
     *放置的位置
     */
    private boolean isRom;
    /**
     * 包名
     */
    private String apkPackageName;

    @Override
    public String toString() {
        return "AppInfo{" +
                "apkIcon=" + apkIcon +
                ", apkName='" + apkName + '\'' +
                ", apkSize=" + apkSize +
                ", userApp=" + userApp +
                ", isRom=" + isRom +
                ", apkPackageName='" + apkPackageName + '\'' +
                '}';
    }

    public Drawable getApkIcon() {
        return apkIcon;
    }

    public void setApkIcon(Drawable apkIcon) {
        this.apkIcon = apkIcon;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setIsRom(boolean isRom) {
        this.isRom = isRom;
    }

    public String getApkPackageName() {
        return apkPackageName;
    }

    public void setApkPackageName(String apkPackageName) {
        this.apkPackageName = apkPackageName;
    }
}
