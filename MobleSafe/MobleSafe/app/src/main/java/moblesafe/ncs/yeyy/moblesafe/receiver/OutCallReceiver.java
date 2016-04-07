package moblesafe.ncs.yeyy.moblesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import moblesafe.ncs.yeyy.moblesafe.db.dao.AddressDao;

/**
 * 监听去电的广播接受者
 * android.permission.PROCESS_OUTGOING_CALLS
 * Created by yeyy on 2016/1/27.
 */
public class OutCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//获取去电号码
        String number = getResultData();
        String address = AddressDao.getAddress(number);//拿到地址
        Toast.makeText(context, address, Toast.LENGTH_LONG).show();
    }
}
