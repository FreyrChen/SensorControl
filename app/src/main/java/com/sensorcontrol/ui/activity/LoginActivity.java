package com.sensorcontrol.ui.activity;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sensorcontrol.R;
import com.sensorcontrol.base.BaseActivity;
import butterknife.BindView;
import butterknife.OnClick;

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

    @Override
    protected int setLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void setData() {

    }


    @OnClick({R.id.tvForget, R.id.tvRegister, R.id.btnLogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvForget:
                break;
            case R.id.tvRegister:
                Intent intent = new Intent(this, RegisterUserActivity.class);
                startActivity(intent);
                break;
            case R.id.btnLogin:
                break;
        }
    }
}
