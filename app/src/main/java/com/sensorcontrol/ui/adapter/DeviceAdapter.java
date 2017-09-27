package com.sensorcontrol.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.search.SearchResult;
import com.sensorcontrol.R;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lizhe on 2017/9/19 0019.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class DeviceAdapter extends BaseAdapter {

    private List<SearchResult> mList;

    private Context mContext;


    public DeviceAdapter(Context mContext) {
        this.mContext = mContext;
        if (mList == null) {
            mList = new ArrayList<>();
        }
    }

    public void clear() {
        mList.clear();
    }

    public void setmList(List<SearchResult> list) {
        mList.clear();
        list.addAll(list);
        notifyDataSetChanged();
    }

    public void addData(SearchResult data) {
        if (!mList.contains(data)) {
            mList.add(data);
            notifyDataSetChanged();
        }
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public SearchResult getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (view == null){
            view =View.inflate(mContext, R.layout.device_list_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final SearchResult result = (SearchResult) getItem(position);
        viewHolder.name.setText(result.getName());
        viewHolder.mac.setText(result.getAddress());
        viewHolder.rssi.setText(String.format("Rssi: %d", result.rssi));

//        Beacon beacon = new Beacon(result.scanRecord);
//        viewHolder.adv.setText(beacon.toString());


//        view.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(mContext, DeviceDetailActivity.class);
//                intent.putExtra("mac", mList.get(i).getAddress());
//                mContext.startActivity(intent);
//            }
//        });
        return view;
    }

    public List<SearchResult> getmList() {
        return mList;
    }

    static class ViewHolder {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.mac)
        TextView mac;
        @BindView(R.id.rssi)
        TextView rssi;
//        @BindView(R.id.adv)
//        TextView adv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
