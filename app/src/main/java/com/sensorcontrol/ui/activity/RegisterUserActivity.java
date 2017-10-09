package com.sensorcontrol.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizUserAccountType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.sensorcontrol.R;
import com.sensorcontrol.app.Constants;
import com.sensorcontrol.base.BaseActivity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by lizhe on 2017/10/9 0009.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class RegisterUserActivity extends BaseActivity {

    private static final int TICK_TIME = 1;
    private static final int SENDSUCCESSFUL = 2;
    @BindView(R.id.imageView1)
    ImageView imageView1;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.btnGetCode)
    TextView btnGetCode;
    @BindView(R.id.linearLayout1)
    LinearLayout linearLayout1;
    @BindView(R.id.imageView2)
    ImageView imageView2;
    @BindView(R.id.etCode)
    EditText etCode;
    @BindView(R.id.linearLayout2)
    LinearLayout linearLayout2;
    @BindView(R.id.imageView3)
    ImageView imageView3;
    @BindView(R.id.etPsw)
    EditText etPsw;
    @BindView(R.id.linearLayout3)
    LinearLayout linearLayout3;

    //验证码重发倒计时
    int secondleft = 60;
    //The timer.
    Timer timer;
    //数据变量
    String name, code, psw;

    private GizWifiSDKListener mListener = new GizWifiSDKListener() {
        /** 手机验证码回调 */
        @Override
        public void didRequestSendPhoneSMSCode(GizWifiErrorCode result, String token) {
            Message msg = new Message();
            if (GizWifiErrorCode.GIZ_SDK_SUCCESS != result) {
                handler.sendMessage(msg);
            } else {
                handler.sendEmptyMessage(SENDSUCCESSFUL);

            }
        }

        @Override
        public void didRegisterUser(GizWifiErrorCode result, String uid,
                                       String token) {
            if (GizWifiErrorCode.GIZ_SDK_SUCCESS != result) {
                Toast.makeText(mActivity, "注册失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mActivity, "注册成功", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected int setLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void setData() {
        GizWifiSDK.sharedInstance().setListener(mListener);
    }

    public static boolean isMobileNO(String mobiles){

        Pattern p = Pattern.compile("^((1[0-9][0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    @OnClick({R.id.btnGetCode, R.id.btnRegister ,R.id.etName})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnGetCode:
                if (TextUtils.isEmpty(etName.getText())){
                    Toast.makeText(mActivity, "手机号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isMobileNO(etName.getText().toString().trim())){
                    Toast.makeText(mActivity, "请输入合法手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                String AppSecret = Constants.AppSecret;
                GizWifiSDK.sharedInstance().requestSendPhoneSMSCode(AppSecret,etName.getText().toString().trim());
                break;
            case R.id.btnRegister:
                name = etName.getText().toString();
                code = etCode.getText().toString();
                psw = etPsw.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(this, R.string.toast_name_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (code.length() != 6) {
                    Toast.makeText(this, R.string.no_getcode, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(psw)) {
                    Toast.makeText(this, R.string.toast_psw_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }
                GizWifiSDK.sharedInstance().registerUser(name,psw,code, GizUserAccountType.GizUserPhone);
                break;
            case R.id.etName:
                etName.setEnabled(true);
                break;
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case TICK_TIME:
                    String getCodeAgain = getString(R.string.getcode_again);
                    String timerMessage = getString(R.string.timer_message);
                    secondleft--;
                    if (secondleft <= 0) {
                        timer.cancel();
                        btnGetCode.setEnabled(true);
                        btnGetCode.setText(getCodeAgain);
                    } else {
                        btnGetCode.setText(secondleft + timerMessage);
                    }
                    break;
                case SENDSUCCESSFUL:
                    etName.setEnabled(false);
                    isStartTimer();
                    break;
            }
        }
    };

    /**
     * 倒计时
     */
    public void isStartTimer() {
        btnGetCode.setEnabled(false);
        btnGetCode.setBackgroundResource(R.drawable.btn_getcode_shape_gray);
        secondleft = 60;
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.sendEmptyMessage(TICK_TIME);
            }
        }, 1000, 1000);
    }
}
