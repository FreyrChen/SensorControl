package com.sensorcontrol.app;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizEventType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.BluetoothContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lizhe on 2017/9/18 0018.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class App extends Application{

    private static App application;
    private Set<Activity> mActivityList;

    private GizWifiSDKListener mListener = new GizWifiSDKListener() {

        @Override
        public void didNotifyEvent(GizEventType eventType, Object eventSource, GizWifiErrorCode eventID, String eventMessage) {
            if (eventType == GizEventType.GizEventSDK) {
                // SDK的事件通知
                Log.i("GizWifiSDK", "SDK event happened: " + eventID + ", " + eventMessage);
            } else if (eventType == GizEventType.GizEventDevice) {
                // 设备连接断开时可能产生的通知
                GizWifiDevice mDevice = (GizWifiDevice) eventSource;
                Log.i("GizWifiSDK", "device mac: " + mDevice.getMacAddress() + " disconnect caused by eventID: " + eventID + ", eventMessage: " + eventMessage);
            } else if (eventType == GizEventType.GizEventM2MService) {
                // M2M服务返回的异常通知
                Log.i("GizWifiSDK", "M2M domain " + (String) eventSource + " exception happened, eventID: " + eventID + ", eventMessage: " + eventMessage);
            } else if (eventType == GizEventType.GizEventToken) {
                // token失效通知
                Log.i("GizWifiSDK", "token " + (String) eventSource + " expired: " + eventMessage);
            }
        }
    };

    public static synchronized App getInstance() {
        return application;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        BluetoothContext.set(this);
        GizWifiSDK.sharedInstance().setListener(mListener);
        GizWifiSDK.sharedInstance().startWithAppID(getApplicationContext(), Constants.APPID);
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
