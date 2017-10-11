package com.sensorcontrol.ui.activity.wifi;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.sensorcontrol.R;
import com.sensorcontrol.base.WifiConnActivity;

import java.util.List;

/**
 * Created by lizhe on 2017/10/11 0011.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class DeviceListActivity extends WifiConnActivity{

    @Override
    protected int setLayout() {
        return R.layout.activity_gos_device_list;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void setData() {

    }

    @Override
    protected void didBindDevice(int error, String errorMessage, String did) {

    }

    @Override
    protected void didChannelIDBind(GizWifiErrorCode result) {

    }

    @Override
    protected void didDiscovered(GizWifiErrorCode result, List<GizWifiDevice> deviceList) {

    }

    @Override
    protected void didSetSubscribe(GizWifiErrorCode result, GizWifiDevice device, boolean isSubscribed) {

    }

    @Override
    protected void didUserLogin(GizWifiErrorCode result, String uid, String token) {

    }
}
