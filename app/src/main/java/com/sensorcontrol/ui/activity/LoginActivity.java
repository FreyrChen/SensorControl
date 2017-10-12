package com.sensorcontrol.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizEventType;
import com.gizwits.gizwifisdk.enumration.GizUserAccountType;
import com.gizwits.gizwifisdk.enumration.GizUserGenderType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.sensorcontrol.R;
import com.sensorcontrol.app.App;
import com.sensorcontrol.app.MessageCenter;
import com.sensorcontrol.base.BaseActivity;
import com.sensorcontrol.util.ErrorHandleUtil;
import com.sensorcontrol.util.SpUtil;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

/**
 * Created by lizhe on 2017/10/9 0009.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.imageView2)
    ImageView imageView2;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.linearLayout1)
    LinearLayout linearLayout1;
    @BindView(R.id.imageView3)
    ImageView imageView3;
    @BindView(R.id.etPsw)
    EditText etPsw;
    @BindView(R.id.linearLayout2)
    LinearLayout linearLayout2;

    private ProgressDialog progressDialog;
    private GizWifiSDKListener mListener = new GizWifiSDKListener() {
        
        @Override
        public void didUserLogin(GizWifiErrorCode result, String uid, String token) {
            if (GizWifiErrorCode.GIZ_SDK_SUCCESS != result) {// 登录失败
                progressDialog.cancel();
                Toast.makeText(mActivity, ErrorHandleUtil.toastError(result,getApplicationContext()), Toast.LENGTH_SHORT).show();
                etName.setEnabled(true);
                etPsw.setEnabled(true);
            } else {// 登录成功
                progressDialog.cancel();
                Toast.makeText(mActivity, "登录成功", Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(etName.getText().toString()) && !TextUtils.isEmpty(etPsw.getText().toString())
                        && TextUtils.isEmpty(SpUtil.getString(getApplicationContext(),"thirdUid", ""))) {
                    SpUtil.putString(getApplicationContext(),"UserName", etName.getText().toString());
                    SpUtil.putString(getApplicationContext(),"PassWord", etPsw.getText().toString());
                }
                SpUtil.putString(getApplicationContext(),"uid", uid);
                SpUtil.putString(getApplicationContext(),"token", token);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }

        }
    };

    /**
     * 设置ProgressDialog
     */
    public void setProgressDialog() {
        progressDialog = new ProgressDialog(this);
        String loadingText = getString(R.string.loadingtext);
        progressDialog.setMessage(loadingText);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void init() {
        MessageCenter.getInstance(this);
        setProgressDialog();
        GizWifiSDK.sharedInstance().setListener(mListener);
    }

    @Override
    protected void setData() {
        String name = SpUtil.getString(getApplicationContext(),"UserName");
        String pwd = SpUtil.getString(getApplicationContext(),"PassWord");
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)) {
            etName.setText(name);
            etPsw.setText(pwd);
        }

    }


    @OnClick({R.id.tvRegister, R.id.btnLogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvRegister:
                Intent intent = new Intent(this, RegisterUserActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.btnLogin:
                progressDialog.show();
                GizWifiSDK.sharedInstance().userLogin(etName.getText().toString().trim(), etPsw.getText().toString().trim());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1){
            if (resultCode == 1){
                String name = data.getStringExtra("user");
                String pwd = data.getStringExtra("pwd");
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)) {
                    etName.setText(name);
                    etPsw.setText(pwd);
                    GizWifiSDK.sharedInstance().registerUser(name, pwd);
                }
            }
        }
    }

    private static Boolean isQuit = false;
    private Timer timer = new Timer();

    @Override
    public void onBackPressed() {
        if (isQuit == false) {
            isQuit = true;
            Toast.makeText(mActivity, "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
            TimerTask task = null;
            task = new TimerTask() {
                @Override
                public void run() {
                    isQuit = false;
                }
            };
            timer.schedule(task, 2000);
        } else {
            App.getInstance().exitApp();
        }
    }

}
