package com.sensorcontrol.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import com.sensorcontrol.R;

/**
 * Created by lizhe on 2017/9/29 0029.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class StartupActivity extends AppCompatActivity {

    private Handler handler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.startup_activity);
        final Activity activity = this;
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(activity,LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.open_out, R.anim.open_in);// 淡出淡入动画效果
                activity.finish();
            }
        },2000);

    }

}
