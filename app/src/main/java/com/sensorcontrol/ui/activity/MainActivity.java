package com.sensorcontrol.ui.activity;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensorcontrol.R;
import com.sensorcontrol.app.App;
import com.sensorcontrol.base.BaseActivity;
import com.sensorcontrol.module.ClientManager;
import com.sensorcontrol.ui.fragment.ConfigFragment;
import com.sensorcontrol.ui.fragment.DisplayFragment;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

/**
 * Created by lizhe on 2017/9/25 0025.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class MainActivity extends BaseActivity {

    @BindView(R.id.fragment_context)
    FrameLayout fragmentContext;
    @BindView(R.id.home_page)
    TextView homePage;
    @BindView(R.id.pair)
    TextView pair;


    private FragmentManager mFragmentManager;
    private Set<Fragment> set;
    private String showFragmentTag;
    public static final String HOMEPAGE = "homepage";
    public static final String CONFIG = "config";

    @Override
    protected int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        homePage.setSelected(true);
        setShowFragment(HOMEPAGE);
    }

    @Override
    protected void setData() {

    }

    @OnClick({R.id.home_page, R.id.pair})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.home_page:
                clickAnimation(homePage);
                setBtn(HOMEPAGE);
                setShowFragment(HOMEPAGE);
                break;
            case R.id.pair:
                clickAnimation(pair);
                setBtn(CONFIG);
                setShowFragment(CONFIG);
                break;
        }
    }

    public void setShowFragment(String tag){
        if (showFragmentTag !=null && showFragmentTag.equals(tag)){
            return;
        }
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        //转场动画
//        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        Fragment hideFragment = mFragmentManager.findFragmentByTag(showFragmentTag);
        if (hideFragment != null){
            fragmentTransaction.hide(hideFragment);
        }

        Fragment showFragment = mFragmentManager.findFragmentByTag(tag);
        if (showFragment == null){
            switch (tag){
                case HOMEPAGE:
                    showFragment = new DisplayFragment();
                    break;
                case CONFIG:
                    showFragment = new ConfigFragment();
                    break;
            }
        }

        if (showFragment == null){

        }else if (showFragment.isAdded()){
            fragmentTransaction.show(showFragment);
        }else {
            fragmentTransaction.add(R.id.fragment_context,showFragment,tag);
            if (set == null){
                set = new HashSet<>();
            }
            set.add(showFragment);
        }
        fragmentTransaction.commit();
        showFragmentTag = tag;

    }

    public void setBtn(String btn) {
        switch (btn){
            case HOMEPAGE:
                homePage.setSelected(true);
                pair.setSelected(false);
                break;
            case CONFIG:
                homePage.setSelected(false);
                pair.setSelected(true);
                break;
        }
    }

    private static Boolean isQuit = false;
    private Timer timer = new Timer();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Fragment f : set) {
            mFragmentManager.beginTransaction().remove(f);
        }
        ClientManager.getClient().closeBluetooth();
    }

    @Override
    public void onBackPressed() {
        if (isQuit == false) {
            isQuit = true;
            Snackbar.make(fragmentContext,"再按一次返回键退出程序",LENGTH_SHORT).show();
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