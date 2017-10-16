package com.sensorcontrol.ui.activity;

import android.content.Intent;

import com.sensorcontrol.R;
import com.sensorcontrol.base.BaseActivity;

/**
 * Created by lizhe on 2017/10/16 0016.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class SelectFileActivity extends BaseActivity{

    @Override
    protected int setLayout() {
        return R.layout.activity_select_file;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void setData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
