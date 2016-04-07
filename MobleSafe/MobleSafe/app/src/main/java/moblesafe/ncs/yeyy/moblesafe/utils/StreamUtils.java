package moblesafe.ncs.yeyy.moblesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 读取流的工具
 * Created by yeyy on 2016/1/8.
 */

public class StreamUtils {
    /**
     * 将输入流读取成String后返回
     * @param in
     * @return
     */
    public static String readFromStream(InputStream in) throws IOException {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        int len=0;
        byte[] buffer=new byte[1024];
        while ((len=in.read(buffer))!=-1){
            out.write(buffer,0,len);
        }
        String result=out.toString();
        in.close();//输入流关闭
        out.close();//输出流关闭
        return result;
    }
}
