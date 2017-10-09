package com.sensorcontrol.view;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sensorcontrol.R;


/**
 * Created by lizhe on 2017/9/22 0022.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class InputDialog extends BaseDialog {

    private OnCancelListener onCancelListener;

    private OnConfirmListener onConfirmListener;
    private EditText edTime;
    private RelativeLayout relativeLayout;
    private RelativeLayout relativeLayout_rl_range;

    public InputDialog(Context context) {
        super(context);
        mContext = context;
    }

    public InputDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_normal_layout;
    }

    //也回调了父类的init，利用getLayoutId传入了布局的id
    @Override
    protected void init() {
        super.init();
        final EditText edName = view.findViewById(R.id.ed_name);
        final EditText edCmd = view.findViewById(R.id.ed_cmd);
        edTime = view.findViewById(R.id.ed_time);
        final EditText edMix = view.findViewById(R.id.ed_mix);
        final EditText edMin = view.findViewById(R.id.ed_min);
        final TextView positiveButton = view.findViewById(R.id.positiveButton);
        TextView negativeButton = view.findViewById(R.id.negativeButton);
        relativeLayout = view.findViewById(R.id.rl_time);
        relativeLayout_rl_range = view.findViewById(R.id.rl_range);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelListener.onCancel(view);
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConfirmListener.onConfirm(edName.getText().toString().trim(), edCmd.getText().toString().trim(), edTime.getText().toString().trim()+"000",edMin.getText().toString().trim(),edMix.getText().toString().trim());
            }
        });
    }

    public RelativeLayout getRelativeLayout() {
        return relativeLayout;
    }

    public RelativeLayout getRelativeLayout_rl_range() {
        return relativeLayout_rl_range;
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    public interface OnCancelListener {
        void onCancel(View view);
    }

    public interface OnConfirmListener {
        void onConfirm(String name, String cmd, String time, String min, String max);
    }
}
