package moblesafe.ncs.yeyy.moblesafe.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import moblesafe.ncs.yeyy.moblesafe.R;

/**
 * 联系人列表
 */
public class ContactActivity extends Activity {

    private ListView lvList; //获取联系人的listView列表
    private ArrayList<HashMap<String, String>> readContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        lvList = (ListView) findViewById(R.id.lv_contact);
        readContact = readContact();
        lvList.setAdapter(new SimpleAdapter(this, readContact,
                R.layout.contact_listview_item,new String[]{"name","phone"},
                new int[]{R.id.tv_name,R.id.tv_phone}));
//        监听item点击事件
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone=readContact.get(position).get("phone");//读取当前item的电话号码
                Intent intent=new Intent();
                intent.putExtra("phone",phone);
                setResult(Activity.RESULT_OK, intent);//将数据放在intent中，返回给上一个界面
                finish();
            }
        });
    }

    private ArrayList<HashMap<String, String>> readContact() {
//        首先，从raw_contacts中读取联系人的id（"contact_id"）
//        其次，根据contact_id从data表中查询出相应的电话号码和联系人名称
//        然后，根绝mimetype来区分哪个是联系人，哪个是电话号码

//        raw_contacts中读取联系人的id（"contact_id"）
        Uri rawContactsUri = Uri
                .parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

//        getContentResolver().query(url要读取哪张表的uri,projection要查询的内容字段,
//        selection筛选的条件,selectionArgs筛选的参数,sortOrder排序);
        Cursor rawContactsCursor = getContentResolver().query(rawContactsUri,
                new String[]{"contact_id"}, null, null, null);
        if (rawContactsCursor != null) {
            while (rawContactsCursor.moveToNext()) {
                String contactId = rawContactsCursor.getString(0);

                //        其次，根据contact_id从data表中查询出相应的电话号码和联系人名称,实际上查询的是view_data
                Cursor dataCursor = getContentResolver().query(dataUri,
                        new String[]{"data1", "mimetype"}, "contact_id=?",
                        new String[]{contactId}, null);
                if (dataCursor != null) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    while (dataCursor.moveToNext()) {
                        String data1 = dataCursor.getString(0);
                        String mimetype = dataCursor.getString(1);
                        if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                            map.put("phone", data1);
                        } else if ("vnd.android.cursor.item/name".equals(mimetype)) {
                            map.put("name", data1);
                        }
                    }
                    list.add(map);
                    dataCursor.close();
                }
            }
            rawContactsCursor.close();
        }
        return list;
    }
}
