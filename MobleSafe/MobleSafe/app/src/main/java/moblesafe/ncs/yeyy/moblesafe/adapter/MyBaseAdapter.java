package moblesafe.ncs.yeyy.moblesafe.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * listView中adapter的基础适配器
  * @param <T>
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {


    public List<T> lists;

    public Context mContext;

    protected MyBaseAdapter(List<T> lists, Context mContext) {
        this.lists = lists;
        this.mContext = mContext;
    }

    protected MyBaseAdapter() {
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
