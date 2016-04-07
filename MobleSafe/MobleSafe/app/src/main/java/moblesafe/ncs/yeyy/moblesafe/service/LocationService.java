package moblesafe.ncs.yeyy.moblesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * 获取经纬度的service
 * Created by yeyy on 2016/1/22.
 */
public class LocationService extends Service {

    private MyLocationListener listener;
    private LocationManager lm;
    private SharedPreferences mPref;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPref = getSharedPreferences("config", MODE_PRIVATE);
        //        获取系统的定位服务
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
//        List<String> allProviders = lm.getAllProviders();

        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);//是否允许付费，使用3G流量定位
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//精确度
        String bestProvider = lm.getBestProvider(criteria, true);//获取最佳位置提供者
        listener = new MyLocationListener();
//        lm.requestLocationUpdates(provider 位置提供者,minTime 最短更新时间,minDistance最短更新距离,listener);
        lm.requestLocationUpdates(bestProvider, 0, 0, listener);

    }

    class MyLocationListener implements LocationListener {
        //位置发生变化
        @Override
        public void onLocationChanged(Location location) {

//            String j = "经度:" + location.getLongitude();
//            String w = "维度:" + location.getLatitude();
//            String accuracy = "精确度:" + location.getAccuracy();
//            String altitude = "海拔:" + location.getAltitude();
//            将获取的经纬度保存在sp中
            mPref.edit().putString("location", "j:" + location.getLongitude()
                    + ";w:" + location.getLatitude()).commit();
            stopSelf();//停掉service

        }

        //为之提供者状态发生变化
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        //当提供者可用 GPS打开
        @Override
        public void onProviderEnabled(String provider) {

        }

        //用户关闭GPS
        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lm.removeUpdates(listener);//当activity销毁时，停止更新位置，节省电量
    }
}
