package com.sensorcontrol.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensorcontrol.R;
import com.sensorcontrol.bean.CmdBean;
import com.sensorcontrol.bean.ListBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lizhe on 2017/9/29 0029.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class BtnListAdapter extends BaseAdapter {

    public static final int BACKDATA = 3;
    private Context mContext;

    private List<CmdBean> mList;
    private Activity mActivity;
    private int type;

    public BtnListAdapter(Context mContext, int type, Activity activity) {
        this.mContext = mContext;
        mList = new ArrayList();
        this.type = type;
        this.mActivity = activity;
    }

    public void setmList(List<CmdBean> mList) {
        this.mList = mList;
        notifyDataSetChanged();
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null){
            view =View.inflate(mContext, R.layout.list_btn_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        CmdBean bean = mList.get(i);
        if (bean.getAT() == null){
            viewHolder.tvAt.setText("预留空位");
        }else {
            viewHolder.tvAt.setText("AT+" + bean.getAT());
        }
        if (bean.getName() == null){
            viewHolder.tvName.setText("预留空位");
        }else {
            viewHolder.tvName.setText(bean.getName());
        }
        if (type == 0) {

            viewHolder.tvTxt.setText("定时: "+bean.getTime()+"ms");
        }else {
            viewHolder.tvTxt.setText("范围: "+bean.getMin()+"  ~  "+bean.getMax());
        }
        viewHolder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mList.remove(i);
                Intent intent = new Intent();
                intent.putExtra("data",new ListBean(mList));
                mActivity.setResult(BACKDATA,intent);
                mActivity.finish();
            }
        });
        return view;
    }

    static class ViewHolder {
        @BindView(R.id.tv_at)
        TextView tvAt;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_txt)
        TextView tvTxt;
        @BindView(R.id.delete_img)
        ImageView deleteImg;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
