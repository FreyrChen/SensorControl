package com.sensorcontrol.module;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;

/**
 * Created by lizhe on 2017/9/28 0028.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class WifiModule {

    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;

    private WifiManager.WifiLock mWifiLock;

    // 实例化监听器
   private  GizWifiSDKListener mListener = new GizWifiSDKListener() {
        // 实现手机号注册用户回调
        @Override
        public void didRegisterUser(GizWifiErrorCode result, String uid, String token){
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
             // 注册成功，处理注册成功的逻辑
            } else {
                // 注册失败，处理注册失败的逻辑
            }
        }


    };

    private void wifiLisenter(){
        // 注册监听器
        GizWifiSDK.sharedInstance().setListener(mListener);
        // 调用SDK的手机号注册接口
        GizWifiSDK.sharedInstance().userLogin("17688943972", "100122");
    }

    public WifiModule(Context context) {
        //取得WifiManager
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //取得WifiInfo
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    //打开WIFI
    public void openWifi(View view) {
        if (!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(true);
        }else if (mWifiManager.getWifiState() == 2){
            Snackbar.make(view,"亲，Wifi正在开启，不用再开了",Snackbar.LENGTH_SHORT).show();
        }else {
            Snackbar.make(view,"亲，Wifi已经开启,不用再开了",Snackbar.LENGTH_SHORT).show();
        }
    }

    //关闭Wifi
    public void closeWifi(View view){
        if (mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(false);
        }else if (mWifiManager.getWifiState() == 1){
            Snackbar.make(view,"亲，Wifi已经关闭，不用再关了",Snackbar.LENGTH_SHORT).show();
        }else if (mWifiManager.getWifiState() == 0){
            Snackbar.make(view,"亲，Wifi正在关闭，不用再关了",Snackbar.LENGTH_SHORT).show();
        }else {
            Snackbar.make(view,"请重新关闭",Snackbar.LENGTH_SHORT).show();
        }
    }

    //检查当前WIFI状态
    public void checkState(View view){
        if (mWifiManager.getWifiState() == 0){
            Snackbar.make(view,"Wifi正在关闭",Snackbar.LENGTH_SHORT).show();
        }else if (mWifiManager.getWifiState() == 0){
            Snackbar.make(view,"Wifi已经关闭",Snackbar.LENGTH_SHORT).show();
        }else if (mWifiManager.getWifiState() == 2) {
            Snackbar.make(view,"Wifi正在开启",Snackbar.LENGTH_SHORT).show();
        } else if (mWifiManager.getWifiState() == 3) {
            Snackbar.make(view,"Wifi已经开启",Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(view,"没有获取到WiFi状态",Snackbar.LENGTH_SHORT).show();
        }
    }


}
