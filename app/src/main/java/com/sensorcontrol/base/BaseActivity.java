package com.sensorcontrol.base;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

import com.sensorcontrol.R;
import com.sensorcontrol.app.App;
import com.sensorcontrol.controller.BluetoothController;
import com.sensorcontrol.module.BluetoothModule;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/8/18 0018.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected Activity mActivity;
    private Unbinder mUnbinder;
    protected BluetoothController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(setLayout());
        mUnbinder = ButterKnife.bind(mActivity);
        App.getInstance().addActivity(this);
        initController();
        init();
        setData();
        fastClick();
    }

    private void initController(){
        if (mController != null){
            return;
        }
        mController = new BluetoothController.Builder()
                .setmActivity(this)
                .setmBluetoothModule(BluetoothModule.getBluetoothModule())
                .setmHandler(new MyHandler(this))
                .build();
    }

    @LayoutRes
    protected abstract int setLayout();

    protected abstract void init();

    protected abstract void setData();


    /**
     * 防止快速点击
     *
     * @return
     */
    private boolean fastClick() {
        long lastClick = 0;
        if (System.currentTimeMillis() - lastClick <= 1000) {
            return false;
        }
        lastClick = System.currentTimeMillis();
        return true;

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getInstance().removeActivity(this);
        mUnbinder.unbind();
    }

    protected void initToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 检测view是否显示
     * @param view
     * @return
     */
    protected boolean isShow(View view){
        if (view.getVisibility() == View.VISIBLE){
            return true;
        }else {
            return false;
        }
    }

    private class MyHandler extends Handler {

        private WeakReference<Activity> mActivity;
        public MyHandler(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = mActivity.get();
            if (activity == null) {
                return;
            }
            handleMessage1(msg);
        }
    }

    protected void handleMessage1(Message msg){

    }

    /**
     * 点击动效
     * @param view
     */
    public final void clickAnimation(View view){
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f,1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.2f,1f);

        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
        animatorSet.start();

    }
}
