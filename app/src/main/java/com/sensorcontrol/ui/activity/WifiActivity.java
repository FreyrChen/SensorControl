package com.sensorcontrol.ui.activity;

import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;

import com.sensorcontrol.R;
import com.sensorcontrol.base.BaseActivity;

/**
 * Created by lizhe on 2017/9/28 0028.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class WifiActivity extends BaseActivity{

    public final static String PREF_IP = "PREF_IP_ADDRESS";
    public final static String PREF_PORT = "PREF_PORT_NUMBER";
    private Button buttonPin11,buttonPin12,buttonPin13;
    private EditText editTextIPAddress, editTextPortNumber;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    @Override
    protected int setLayout() {
        return R.layout.activity_wifi;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void setData() {

    }
}
