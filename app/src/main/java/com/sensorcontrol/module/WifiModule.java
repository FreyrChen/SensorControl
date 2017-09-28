package com.sensorcontrol.module;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by lizhe on 2017/9/28 0028.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class WifiModule {

    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;

    private WifiManager.WifiLock mWifiLock;

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

    //锁定WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    //解锁WifiLock
    public void releaseWifiLock() {
        //判断时候锁定
        if (mWifiLock.isHeld()){
            mWifiLock.acquire();
        }
    }

    //创建一个WifiLock
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }



}
