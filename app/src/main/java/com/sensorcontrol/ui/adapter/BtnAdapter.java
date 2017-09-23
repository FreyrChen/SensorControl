package com.sensorcontrol.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sensorcontrol.R;
import com.sensorcontrol.bean.CmdBean;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizhe on 2017/9/21 0021.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class BtnAdapter extends RecyclerView.Adapter<BtnAdapter.ViewHolder>{

    private Context mContext;

    private List<CmdBean> mList;

    private OnItemClickListener OnItemClickListener;
    private OnLongClickListener OnLongClickListener;

    public BtnAdapter(Context mContext) {
        this.mContext = mContext;
        mList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.btn_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.update(position);
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setmList(List<CmdBean> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void upData(int position,CmdBean cmdBean){
        mList.add(position,cmdBean);
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_text);
        }

        public void update(final int position){
            textView.setText(mList.get(position).getName());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnItemClickListener.onClick(position,mList.get(position));
                }
            });
            textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    OnLongClickListener.onLongClick(position,mList);
                    return true;
                }
            });
        }
    }

    public void setOnItemClickListener(BtnAdapter.OnItemClickListener onItemClickListener) {
        OnItemClickListener = onItemClickListener;
    }

    public void setOnLongClickListener(BtnAdapter.OnLongClickListener onLongClickListener) {
        OnLongClickListener = onLongClickListener;
    }

    public interface OnItemClickListener{
        void onClick(int position,CmdBean cmdBean);
    }

    public interface OnLongClickListener{
        void onLongClick(int position,List<CmdBean> mList);
    }
}
