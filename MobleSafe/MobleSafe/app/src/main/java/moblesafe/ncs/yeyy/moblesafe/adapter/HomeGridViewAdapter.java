package moblesafe.ncs.yeyy.moblesafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import moblesafe.ncs.yeyy.moblesafe.R;

/**
 * 主页面适配gridView
 * Created by yeyy on 2016/1/11.
 */
public class HomeGridViewAdapter extends BaseAdapter {
    Context context;
    public String[] mItems;
    public int[] mPics;
//    private String[] mItems=new String[]{"手机防盗","通讯卫士","软件管理","进程管理",
//            "流量统计","手机杀毒","缓存清理","高级工具","设置中心"};
//    private int[] mPics=new int[]{R.drawable.home_safe,
//            R.drawable.home_callmsgsafe,R.drawable.home_apps,
//            R.drawable.home_taskmanager,R.drawable.home_netmanager,
//            R.drawable.home_trojan,R.drawable.home_sysoptimize,
//            R.drawable.home_tools,R.drawable.home_settings};


    public HomeGridViewAdapter(Context context, String[] Items, int[] Pics) {
        this.context = context;
        this.mItems = Items;
        this.mPics = Pics;
    }

    @Override
    public int getCount() {
//        系统调用，用来获知集合中有多少元素
        return mItems.length;
    }

    @Override
    public Object getItem(int position) {
        return mItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * @param position    本次getView方法调用所返回的view对象在listview中是处于第几个条目，那么position的值就是多少
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        一般如果只有几个固定的item，就用此方法写，当你需要加载很多数据item的时候，就需要优化了
//        方法一 把布局文件填充成一个View对象
//        View view=View.inflate(context,R.layout.home_gridview_item,null);//把布局文件填充成一个view对象
//        方法二
//        LayoutInflater inflater=LayoutInflater.from(context);//获取布局填充器对象
//        View view1 = inflater.inflate(R.layout.home_gridview_item,null);//使用布局填充器填充布局文件
//      方法三
//        LayoutInflater inflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//        View view2 = inflater.inflate(R.layout.home_gridview_item,null);

//通过资源id查找组件，注意调用的是View对象的findViewById
//        ImageView ivItem = (ImageView) view.findViewById(R.id.iv_item_home);
//        TextView tvItem = (TextView) view.findViewById(R.id.tv_item_home);
//        tvItem.setText(mItems[position]);
//        ivItem.setImageResource(mPics[position]);

        HolderView holderView = null;
        View view = convertView;
        if (view == null) {
            holderView = new HolderView();
            view = LayoutInflater.from(context).inflate(
                    R.layout.home_gridview_item, parent, false);
//
            holderView.ivItem = (ImageView) view.findViewById(R.id.iv_item_home);
            holderView.tvItem = (TextView) view.findViewById(R.id.tv_item_home);
            view.setTag(holderView);
        } else {
            holderView = (HolderView) view.getTag();
        }
        holderView.tvItem.setText(mItems[position]);
        holderView.ivItem.setImageResource(mPics[position]);

        return view;
    }

    class HolderView {
        ImageView ivItem;
        TextView tvItem;
    }
}
