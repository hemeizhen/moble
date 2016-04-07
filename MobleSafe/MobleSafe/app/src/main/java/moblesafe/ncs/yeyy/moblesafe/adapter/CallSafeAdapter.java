package moblesafe.ncs.yeyy.moblesafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import moblesafe.ncs.yeyy.moblesafe.R;
import moblesafe.ncs.yeyy.moblesafe.bean.BlackNumberInfo;
import moblesafe.ncs.yeyy.moblesafe.db.dao.BlackNumberDao;

/**
 * 黑名单适配数据
 * Created by yeyy on 2016/2/19.
 */
public class CallSafeAdapter extends BaseAdapter {
    Context context;
    BlackNumberDao dao = new BlackNumberDao(context);
//    dao = new BlackNumberDao(CallSafeActivity.this);
    List<BlackNumberInfo> blackNumberInfos ;

    public CallSafeAdapter(Context context, List<BlackNumberInfo> blackNumberInfos) {
        this.context = context;
        this.blackNumberInfos = blackNumberInfos;
    }

    public CallSafeAdapter() {
    }

    @Override
    public int getCount() {
        return blackNumberInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return blackNumberInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        HolderView 相当于一个listView中的item，convertView复用
        HolderView holderView = null;
        View view = convertView;
        if (view == null) {
            holderView = new HolderView();
            view = LayoutInflater.from(context).inflate(
                    R.layout.call_safe_item, parent, false);
            holderView.tvNumber = (TextView) view.findViewById(R.id.tv_number);
            holderView.tvMode = (TextView) view.findViewById(R.id.tv_mode);
            holderView.ivDelete = (ImageView) view.findViewById(R.id.iv_delete);
            view.setTag(holderView);
        } else {
            holderView = (HolderView) view.getTag();
        }
        holderView.tvNumber.setText(blackNumberInfos.get(position).getNumber());
        String mode=blackNumberInfos.get(position).getMode();
        if (mode.equals("1")){
            holderView.tvMode.setText("来电拦截+短信拦截");
        } else if (mode.equals("2")){
            holderView.tvMode.setText("电话拦截");
        }else if (mode.equals("3")){
            holderView.tvMode.setText("短信拦截");
        }

        final BlackNumberInfo info=blackNumberInfos.get(position);

        holderView.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number=info.getNumber();
                boolean result=dao.delete(number);
                if (result){
                    Toast.makeText(context,"删除成功",Toast.LENGTH_LONG).show();
                    blackNumberInfos.remove(info);
                    notifyDataSetChanged();
                }else {
                    Toast.makeText(context,"删除失败",Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    class HolderView {
        TextView tvNumber;
        TextView tvMode;
        ImageView ivDelete;
    }
}
