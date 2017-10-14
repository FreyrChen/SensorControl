package com.sensorcontrol.ui.activity.wifi;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiConfigureMode;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.enumration.GizWifiGAgentType;
import com.sensorcontrol.R;
import com.sensorcontrol.base.WifiConfigActivity;
import com.sensorcontrol.util.ErrorHandleUtil;
import com.sensorcontrol.view.ListDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by lizhe on 2017/10/12 0012.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class ConfigActivity extends WifiConfigActivity {

    public static final Object UPDATEUI = 1;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.wifi_logo)
    ImageView wifiLogo;
    @BindView(R.id.ed_wifi_name)
    EditText edWifiName;
    @BindView(R.id.iv_goto)
    ImageView ivGoto;
    @BindView(R.id.iv_pass)
    ImageView ivPass;
    @BindView(R.id.ed_wifi_pass)
    EditText edWifiPass;
    @BindView(R.id.goto_btn)
    TextView gotoBtn;

    private List<GizWifiGAgentType> types;
    private String wifiName;
    private String wifiKey;
    private GizWifiDevice mDevice;
    private List<GizWifiDevice> mDevices;
    private ListDialog mListDialog;
    private String wifiSSID;
    private List<ScanResult> mScanResults;

    @Override
    protected int setLayout() {
        return R.layout.activity_wifi_config;
    }

    @Override
    protected void init() {
        setProgressDialog();
        mListDialog = new ListDialog(this,mScanResults);
    }

    @Override
    protected void setData() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        wifiSSID = wifiInfo.getSSID();
        Log.d("wifi mac: ", wifiSSID);
        if (wifiInfo.getSSID().equals("<unknown ssid>")){
            Toast.makeText(this, "请先连接wifi", Toast.LENGTH_SHORT).show();
        }else {
            edWifiName.setText(wifiSSID.replaceAll("\\\"",""));
        }
        wifiManager.startScan();
        mScanResults = wifiManager.getScanResults();

        mListDialog.setOnItemListener(new ListDialog.onItemListener() {
            @Override
            public void onItemClick(String name) {
                edWifiName.setText(name);
                mListDialog.dismiss();
            }
        });
    }

    public void setAirLink(String wifiName,String wifiKey) {
        progressDialog.show();
        if (types == null) {
            types = new ArrayList<GizWifiGAgentType>();
        }
        // 让手机连上目标Wifi
        // MCU发出开启AirLink串口指令，通知模组开启AirLink模式。
        //配置设备入网，发送要配置的wifi名称、密码
        types.add(GizWifiGAgentType.GizGAgentESP);
        GizWifiSDK.sharedInstance().setDeviceOnboarding(wifiName, wifiKey, GizWifiConfigureMode.GizWifiAirLink, null, 60, types);
    }

    @Override
    protected void didSetDeviceOnboarding(GizWifiErrorCode result, String mac, String did, String productKey) {
        progressDialog.cancel();
        if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS){
            Toast.makeText(this, R.string.configuration_successful, Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(UPDATEUI);
            finish();
        }else {
            Toast.makeText(mActivity, ErrorHandleUtil.toastError(result,getApplicationContext()), Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick({R.id.iv_goto, R.id.goto_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_goto:
                mListDialog.show();
                break;
            case R.id.goto_btn:
                if (TextUtils.isEmpty(edWifiName.getText())){
                    Toast.makeText(this, "wifi名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edWifiPass.getText())){
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                setAirLink(edWifiName.getText().toString(),edWifiPass.getText().toString());
                break;
        }
    }

}
