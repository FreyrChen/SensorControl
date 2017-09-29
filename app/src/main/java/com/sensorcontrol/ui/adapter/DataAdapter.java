package com.sensorcontrol.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.inuker.bluetooth.library.utils.ByteUtils;
import com.sensorcontrol.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lizhe on 2017/9/21 0021.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class DataAdapter extends BaseAdapter {

    private List<String> mList;
    private Context mContext;

    public DataAdapter(Context context) {
        this.mContext = context;
        mList = new ArrayList<>();
    }

    public void setmList(List<String> mList) {
        this.mList = mList;
        notifyDataSetChanged();


    }

    public void addItem(String s) {
        if (s != null) {
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");
//            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
//            String str = formatter.format(curDate);
            mList.add(s);
            notifyDataSetChanged();

        }
    }

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

        view = View.inflate(mContext, R.layout.data_item, null);
        TextView textView = view.findViewById(R.id.txt);
        textView.setText(mList.get(i));
        return view;
    }

}
