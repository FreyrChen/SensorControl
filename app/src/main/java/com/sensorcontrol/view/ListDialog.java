package com.sensorcontrol.view;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.sensorcontrol.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizhe on 2017/10/9 0009.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class ListDialog extends BaseDialog{

    private List<ScanResult> mList;

    private onItemListener onItemListener;

    public ListDialog(Context context, List<ScanResult> list) {
        super(context);
        if (list != null){
            mList = list;
        }else {
            mList = new ArrayList<>();
        }
        mContext = context;
    }

    public ListDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_list_layout;
    }

    @Override
    protected void init() {
        super.init();
        ListView listView = view.findViewById(R.id.listView);
        listView.setAdapter(new MyAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onItemListener.onItemClick(mList.get(i).SSID);
            }
        });
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(mContext).inflate(R.layout.wifi_list,null);
            TextView textView = view.findViewById(R.id.wifi_name);
            textView.setText(mList.get(i).SSID);
            return view;
        }
    }

    public void setOnItemListener(ListDialog.onItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    public interface onItemListener{
        void onItemClick(String name);
    }

}
