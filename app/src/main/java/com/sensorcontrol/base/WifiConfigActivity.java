package com.sensorcontrol.base;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;

/**
 * Created by lizhe on 2017/10/11 0011.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public abstract class WifiConfigActivity extends WifiBaseActivity{

    private GizWifiSDKListener gizWifiSDKListener = new GizWifiSDKListener() {

        @Override
        public void didSetDeviceOnboarding(GizWifiErrorCode result, GizWifiDevice device) {
            WifiConfigActivity.this.didSetDeviceOnboarding(result, device);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // 每次返回activity都要注册一次sdk监听器，保证sdk状态能正确回调
        GizWifiSDK.sharedInstance().setListener(gizWifiSDKListener);
    }

    protected void didSetDeviceOnboarding(GizWifiErrorCode result, GizWifiDevice device) {
    }

}
