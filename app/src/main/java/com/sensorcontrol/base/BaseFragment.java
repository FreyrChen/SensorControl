package com.sensorcontrol.base;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;


import com.sensorcontrol.controller.BluetoothController;
import com.sensorcontrol.module.BluetoothModule;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * author：lizhe
 * time： 2017-08-23
 * 不积跬步,无以至千里.不积小流,无以成江河
 * 类介绍：fragment基类
 */

public abstract class BaseFragment extends Fragment{

    // 标识fragment视图已经初始化完毕
    private boolean isViewPrepared;
    //标识已经触发过懒加载数据
    private boolean hasFetchData;

    protected View mRootView;

    private Unbinder mUnbinder;

    protected Bundle savedInstanceState;

    protected BluetoothController mController;

    @LayoutRes
    protected abstract int setLayout();

    protected abstract void init();

    protected abstract void setData();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(setLayout(),container,false);
        mUnbinder = ButterKnife.bind(this,mRootView);
        this.savedInstanceState = savedInstanceState;
        initController();
        init();
        return mRootView;

    }

    private void initController(){
        mController = new BluetoothController.Builder()
                .setmActivity(getActivity())
                .setmBluetoothModule(BluetoothModule.getBluetoothModule())
                .setmHandler(new MyHandler(this))
                .build();
    }

    private void lazyFetchDataIfPrepared() {
        if (getUserVisibleHint() && !hasFetchData && isViewPrepared) {
            hasFetchData = true;
            setData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            lazyFetchDataIfPrepared();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewPrepared = true;
        lazyFetchDataIfPrepared();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hasFetchData = false;
        isViewPrepared = false;
        mUnbinder.unbind();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class MyHandler extends Handler {

        private WeakReference<Fragment> mFragment;
        public MyHandler(Fragment fragment) {
            mFragment = new WeakReference<Fragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            Fragment fragment = mFragment.get();
            if (fragment == null) {
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
