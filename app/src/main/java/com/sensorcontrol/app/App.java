package com.sensorcontrol.app;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.Nullable;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.BluetoothContext;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lizhe on 2017/9/18 0018.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class App extends Application{

    private static App application;
    private Set<Activity> mActivityList;

    public static synchronized App getInstance() {
        return application;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        BluetoothContext.set(this);
    }

    public void addActivity(@Nullable Activity activity) {
        if (mActivityList == null){
            mActivityList = new HashSet<>();
        }
        mActivityList.add(activity);
    }

    public void removeActivity(@Nullable Activity activity) {
        if (mActivityList != null){
            mActivityList.remove(activity);
        }
    }

    public void exitApp(){
        if (mActivityList != null){
            for (Activity a : mActivityList){
                mActivityList.remove(a);
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
