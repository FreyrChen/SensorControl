package com.sensorcontrol.view;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.sensorcontrol.R;

/**
 * Created by lizhe on 2017/9/22 0022.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class InputDialog extends BaseDialog {

    private OnCancelListener onCancelListener;

    private OnConfirmListener onConfirmListener;

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
        final EditText edTime = view.findViewById(R.id.ed_time);
        final TextView positiveButton = view.findViewById(R.id.positiveButton);
        TextView negativeButton = view.findViewById(R.id.negativeButton);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelListener.onCancel(view);
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConfirmListener.onConfirm(edName.getText().toString().trim(),edCmd.getText().toString().trim(),edTime.getText().toString().trim());
            }
        });
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
        void onConfirm(String name,String cmd,String time);
    }
}
