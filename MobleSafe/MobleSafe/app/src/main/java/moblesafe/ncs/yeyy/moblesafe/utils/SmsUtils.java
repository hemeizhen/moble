package moblesafe.ncs.yeyy.moblesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by yeyy on 2016/3/18.
 * 短信备份的工具类
 */
public class SmsUtils {

    /**
     * 备份短信的接口
     *
     * @author Administrator
     */
    public interface BackUpCallBackSms {

        public void befor(int count);

        public void onBackUpSms(int process);

    }

    public static boolean backUp(Context context, BackUpCallBackSms callBack) {
        /**
         * 目的：备份短         * 1.判断当前用户的手机上面是否有SD卡
         * 2.权限--- 说明我们没有权限读取
         *   使用内容观察这读取
         * 3.写短信（写到sd卡）
         */
//        判断当前的sd卡的状态
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            如果能进来说明用户有sd卡
            ContentResolver resolver = context.getContentResolver();
//            获取短信的路径
            Uri uri = Uri.parse("content://sms/");
//            type=1 接收短信，type=2 发送短信
//            cursor 表示游标的意思
            Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
//            获取当前一共有多少条短信
            int count = cursor.getCount();
//            设置pd的最大值
//            pd.setMax(count);
//            progressBar1.setMax(count);
            callBack.befor(count);
            int process = 0;//进度条默认是0
            try {
//            把短信备份到sd卡
                File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
//            写文件
                FileOutputStream os = new FileOutputStream(file);
//                得到序列化器
//                在Android系统里面所有有关xml的解析都是pull解析
                XmlSerializer serializer = Xml.newSerializer();
//                把短息序列化到sd卡然后设置编码格式
                serializer.setOutput(os, "utf-8");
//               开始 standalone表示当前的xml是否是独立文件，true表示文件独立,yes
                serializer.startDocument("utf-8", true);
//               设置开始节点，第一个参数是命名空间。第二个参数是节点的名字
                serializer.startTag(null, "smss");
//                设置smss节点上面的属性值，第二个参数是名字，第三个参数是值
                serializer.attribute(null, "size", String.valueOf(count));

//                游标往下面进行移动
                while (cursor.moveToNext()) {
/**
 * <smss>
 *     <sms>
 *         <address>2222</address>
 *         <date>12345678</date>
 *         <type>1</type>
 *         <body>aaaaa</body>
 *     </sms>
 * </smss>
 */
                    System.err.println("----------------------------");
                    System.out.println("address = " + cursor.getString(0));
                    System.out.println("date = " + cursor.getString(1));
                    System.out.println("type = " + cursor.getString(2));
                    System.out.println("body = " + cursor.getString(3));

                    serializer.startTag(null, "sms");
                    serializer.startTag(null, "address");
                    serializer.text(cursor.getString(0));
                    serializer.endTag(null, "address");
                    serializer.startTag(null, "date");
                    serializer.text(cursor.getString(1));
                    serializer.endTag(null, "date");
                    serializer.startTag(null, "type");
                    serializer.text(cursor.getString(2));
                    serializer.endTag(null, "type");
                    serializer.startTag(null, "body");
//                    读取短信内容,对短信内容进行加密，123是密钥
//                    第一个参数是加密种子，相当于密钥，第二个参数是短信内容
                    serializer.text(Crypto.encrypt("123", cursor.getString(3)));
                    serializer.endTag(null, "body");
                    serializer.endTag(null, "sms");
//                    序列化一条短信之后就需要++
                    process++;
//                    pd.setProgress(process);
//                    progressBar1.setProgress(process);
                    callBack.onBackUpSms(process);
                    SystemClock.sleep(200);
                }
                cursor.close();//此处不关无所谓
                serializer.endTag(null, "smss");
//                有开始就有结束
                serializer.endDocument();
                os.flush();
                os.close();//流一般需要关，性能会好一些
                return true;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return false;
    }
}
