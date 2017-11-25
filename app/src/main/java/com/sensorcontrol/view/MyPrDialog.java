package com.sensorcontrol.view;

import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sensorcontrol.R;

/**
 * Created by lizhe on 2017/11/23 0023.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class MyPrDialog extends BaseDialog {

    ProgressBar progress;
    TextView progressPercent;
    TextView progressNumber;
    TextView progressMessage;
    TextView tvCancel;

    private OnCancelListener onCancelListener;
    private int maxProgress;
    public MyPrDialog(Context context, int maxProgress) {
        super(context);
        mContext = context;
        this.maxProgress = maxProgress;
    }

    @Override
    protected void init() {
        super.init();
        progressNumber = view.findViewById(R.id.progress_number);
        progress = view.findViewById(R.id.progress);
        progressPercent = view.findViewById(R.id.progress_percent);
        progressMessage = view.findViewById(R.id.progress_message);
//        tvCancel = view.findViewById(R.id.tv_cancel);

        progress.setMax(maxProgress);
        progressNumber.setText("总计："+maxProgress+"");

//        tvCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onCancelListener.onCancel();
//            }
//        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.common_progress_dialog;
    }

    public void setProgress(int num) {
        progress.setProgress(num);
    }

    public void setProgressPercent(String percent) {
        progressPercent.setText(percent);
    }

    public void setProgressMessage(String message) {
        progressMessage.setText(message);
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    interface OnCancelListener{
        void onCancel();
    }
}
