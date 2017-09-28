package com.sensorcontrol.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sensorcontrol.R;
import com.sensorcontrol.bean.CmdBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizhe on 2017/9/27 0027.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.ViewHolder>{

    private Context mContext;

    private List<CmdBean> mList;
    private boolean flag = true;
    private OnAddItemClickListener OnAddItemClickListener;
    private OnSeekBarChangeListener OnSeekBarChangeListener;
    private OnLongClickListener OnLongClickListener;

    public SliderAdapter(Context mContext) {
        this.mContext = mContext;
        mList = new ArrayList<>();
        mList.add(null);
    }

    @Override
    public SliderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.slider_item, null);
        return new SliderAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(SliderAdapter.ViewHolder holder, int position) {
        holder.update(position);
    }

    public void upItem(List<CmdBean> list){
        if (list != null) {
            mList = list;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setmList(List<CmdBean> list) {
        if (list != null) {
            this.mList = new ArrayList<>();
            mList.addAll(list);
            mList.add(null);
        }

        notifyDataSetChanged();
    }

    public void upData(CmdBean cmdBean){
        mList.add(0,cmdBean);
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        SeekBar seekBar;
        TextView addItem;
        LinearLayout linearLayout;
        TextView range;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_cmd);
            seekBar = itemView.findViewById(R.id.appCompatSeekBar);
            addItem = itemView.findViewById(R.id.add_item);
            linearLayout = itemView.findViewById(R.id.ll_no);
            range = itemView.findViewById(R.id.tv_range);
        }

        public void update(final int position){
            if (mList.get(position) == null) {
                linearLayout.setVisibility(View.GONE);
                addItem.setVisibility(View.VISIBLE);
                addItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OnAddItemClickListener.onAddItem(mList);
                    }
                });
            }else {
                linearLayout.setVisibility(View.VISIBLE);
                if (mList.get(position).getName() == null) {
                    linearLayout.setVisibility(View.GONE);
                }
                seekBar.setMax(mList.get(position).getMax());
                range.setText("范围: "+mList.get(position).getMin()+"  ~  " + mList.get(position).getMax());
                addItem.setVisibility(View.GONE);
                textView.setText(mList.get(position).getName());
                textView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        OnLongClickListener.onLongClick(position,mList);
                        return true;
                    }
                });
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
                        OnSeekBarChangeListener.onProgressChanged(mList.get(position).getAT(),progress);
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        Log.e("------------", "开始滑动！");
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        Log.e("------------", "停止滑动！");
                    }
                });
            }
        }
    }

    public List<CmdBean> getmList() {
        List list = new ArrayList();
        list.addAll(mList);
        list.remove(list.size() -1);
        return list;
    }

    public void setOnLongClickListener(SliderAdapter.OnLongClickListener onLongClickListener) {
        OnLongClickListener = onLongClickListener;
    }

    public void setOnAddItemClickListener(OnAddItemClickListener onAddItemClickListener) {
        OnAddItemClickListener = onAddItemClickListener;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        OnSeekBarChangeListener = onSeekBarChangeListener;
    }

    public interface OnLongClickListener{
        void onLongClick(int position,List<CmdBean> mList);
    }

    public interface OnAddItemClickListener{
        void onAddItem(List<CmdBean> cmdBean);
    }

    public interface OnSeekBarChangeListener{
        void onProgressChanged(String name ,int progress);
    }
}
