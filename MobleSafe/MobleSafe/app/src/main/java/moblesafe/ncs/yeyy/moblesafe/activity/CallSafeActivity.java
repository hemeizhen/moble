package moblesafe.ncs.yeyy.moblesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import moblesafe.ncs.yeyy.moblesafe.R;
import moblesafe.ncs.yeyy.moblesafe.adapter.MyBaseAdapter;
import moblesafe.ncs.yeyy.moblesafe.bean.BlackNumberInfo;
import moblesafe.ncs.yeyy.moblesafe.db.dao.BlackNumberDao;

/**
 * 通讯卫士
 */
public class CallSafeActivity extends Activity {

    private ListView lvCallSafe;
    private List<BlackNumberInfo> blackNumberInfos;

    private LinearLayout llPb;
    //    开始的位置
    private int mStartIndex = 0;
    //    每页加载20条数据
    private int maxCount = 20;
    //    一共有多少页面
    private int totalPage;
    private BlackNumberDao dao;
    private CallSafeAdapter adapter;
    private int totalNumber;
    private Button addBlackNumber;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe2);
        initUi();
        initData();
        onClick();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            llPb.setVisibility(View.INVISIBLE);
            if (adapter == null) {
                adapter = new CallSafeAdapter(blackNumberInfos, CallSafeActivity.this);
                lvCallSafe.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    };

    private void initData() {
        dao = new BlackNumberDao(CallSafeActivity.this);
//        一共有多少条数据
        totalNumber = dao.getTotalNumber();
        new Thread() {
            @Override
            public void run() {
//               分批加载数据
                if (blackNumberInfos == null) {
                    blackNumberInfos = dao.findPar2(mStartIndex, maxCount);
                } else {
//                    把后面的数据嘴角到blackNumberInfos集合里面，防止黑名单被覆盖
                    blackNumberInfos.addAll(dao.findPar2(mStartIndex, maxCount));
                }
                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    private void initUi() {
        llPb = (LinearLayout) findViewById(R.id.ll_pb);
        llPb.setVisibility(View.VISIBLE);//展示加载的圆圈
        lvCallSafe = (ListView) findViewById(R.id.lv_call_safe);
        addBlackNumber = (Button) findViewById(R.id.btn_add_black_number);


    }

    /**
     * listView滑动监听
     */
    private void onClick() {
        lvCallSafe.setOnScrollListener(new AbsListView.OnScrollListener() {
//           状态改变的时候回调的方法

            /**
             *
             * @param view
             * @param scrollState 表示滚动的状态
             * AbsListView.OnScrollListener.SCROLL_STATE_IDLE 闲置状态
             * AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL 手指触摸的时候的状态
             * AbsListView.OnScrollListener.SCROLL_STATE_FLING 惯性
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
//                        获取到最后一条显示的数据
                        int lastVisiblePosition = lvCallSafe.getLastVisiblePosition();
                        if (lastVisiblePosition == blackNumberInfos.size() - 1) {
                            // 加载更多的数据。 更改加载数据的开始位置
                            mStartIndex += maxCount;
                            if (mStartIndex >= totalNumber) {
                                Toast.makeText(getApplicationContext(),
                                        "没有更多的数据了。", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            initData();
                        }
                        break;
                }

            }

            //            listView滚动的时候调用方法
//            时时调用，当我们的手指触摸屏幕的时候就调用
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

//        添加黑名单
        addBlackNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CallSafeActivity.this);
                dialog = builder.create();
                View dialog_view = View.inflate(CallSafeActivity.this, R.layout.dialog_add_black_number, null);
                final EditText etNumber = (EditText) dialog_view.findViewById(R.id.et_black_number);
                Button btnOK = (Button) dialog_view.findViewById(R.id.btn_ok);
                Button btnCancel = (Button) dialog_view.findViewById(R.id.btn_cancel);
                final CheckBox cbPhone = (CheckBox) dialog_view.findViewById(R.id.cb_phone);
                final CheckBox cbSMS = (CheckBox) dialog_view.findViewById(R.id.cb_sms);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str_number = etNumber.getText().toString().trim();
                        if (TextUtils.isEmpty(str_number)) {
                            Toast.makeText(CallSafeActivity.this, "请输入黑名单号码", Toast.LENGTH_LONG).show();
                            return;
                        }
                        String mode = "";
                        if (cbPhone.isChecked() && cbSMS.isChecked()) {
                            mode = "1";
                        } else if (cbPhone.isChecked()) {
                            mode = "2";
                        } else if (cbSMS.isChecked()) {
                            mode = "3";
                        } else {
                            Toast.makeText(CallSafeActivity.this, "请勾选拦截模式", Toast.LENGTH_LONG).show();
                            return;
                        }
                        BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                        blackNumberInfo.setNumber(str_number);
                        blackNumberInfo.setMode(mode);
                        blackNumberInfos.add(0, blackNumberInfo);
//                        把电话号码和拦截模式添加到数据库
                        dao.add(str_number, mode);

                        if (adapter == null) {
                            adapter = new CallSafeAdapter(blackNumberInfos, CallSafeActivity.this);
                            lvCallSafe.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        dialog.dismiss();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setView(dialog_view);
                dialog.show();
                fullScreenDialog();
            }
        });
    }

    private class CallSafeAdapter extends MyBaseAdapter<BlackNumberInfo> {
        private CallSafeAdapter(List lists, Context mContext) {
            super(lists, mContext);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(CallSafeActivity.this, R.layout.call_safe_item, null);
                holder = new ViewHolder();
                holder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
                holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
                holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_number.setText(lists.get(position).getNumber());
            String mode = lists.get(position).getMode();
            if (mode.equals("1")) {
                holder.tv_mode.setText("来电拦截+短信");
            } else if (mode.equals("2")) {
                holder.tv_mode.setText("电话拦截");
            } else if (mode.equals("3")) {
                holder.tv_mode.setText("短信拦截");
            }
            final BlackNumberInfo info = lists.get(position);
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = info.getNumber();
                    boolean result = dao.delete(number);
                    if (result) {
                        Toast.makeText(CallSafeActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        lists.remove(info);
                        //刷新界面
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(CallSafeActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv_number;
        TextView tv_mode;
        ImageView iv_delete;
    }

    /**
     * 让自定义的dialog填充整个屏幕
     */
    private void fullScreenDialog() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }
}
