package com.sensorcontrol.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizDeviceSharing;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.sensorcontrol.R;
import com.sensorcontrol.app.GosDeploy;
import com.sensorcontrol.base.BaseFragment;
import com.sensorcontrol.bean.WifiBean;
import com.sensorcontrol.ui.activity.DeviceControlActivity;
import com.sensorcontrol.ui.activity.wifi.ConfigActivity;
import com.sensorcontrol.ui.adapter.DeviceListAdapter;
import com.sensorcontrol.util.ErrorHandleUtil;
import com.sensorcontrol.util.SpUtil;
import com.sensorcontrol.view.SildeListView;
import com.sensorcontrol.view.SlideListView2;
import com.sensorcontrol.view.VerticalSwipeRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by lizhe on 2017/10/10 0010.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class DeviceListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.llNoDevice)
    ScrollView llNoDevice;
    @BindView(R.id.empty_a)
    ScrollView emptyA;
    @BindView(R.id.id_swipe_ly1)
    VerticalSwipeRefreshLayout idSwipeLy1;
    @BindView(R.id.id_swipe_ly)
    VerticalSwipeRefreshLayout idSwipeLy;
    @BindView(R.id.add_Devices)
    LinearLayout linearLayout;

    private String softssid, mUid, mToken;
    /**
     * 设备列表分类
     */
    private List<GizWifiDevice> boundDevicesList, foundDevicesList, offlineDevicesList;
    /**
     * 设备热点名称列表
     */
    private ArrayList<String> softNameList;
    /**
     * 与APP绑定的设备的ProductKey
     */
    private List<String> ProductKeyList;

    private LinearLayout llNoBoundDevices;
    private LinearLayout llNoFoundDevices;
    private LinearLayout llNoOfflineDevices;
    private SlideListView2 slvBoundDevices;
    private SlideListView2 slvFoundDevices;
    private SlideListView2 slvOfflineDevices;
    private TextView tvBoundDevicesListTitle;
    private TextView tvFoundDevicesListTitle;
    private TextView tvOfflineDevicesListTitle;
    private View icBoundDevices;
    private View icFoundDevices;
    private View icOfflineDevices;

    protected static List<GizWifiDevice> deviceslist;
    public static List<String> boundMessage;
    private DeviceListAdapter mAdapter;
    /** 设备解绑 */
    protected static final int UNBOUND = 99;
    /** 等待框 */
    public ProgressDialog progressDialog;
    private GizWifiDevice mDevice;
    private GosDeploy gosDeploy;

    private GizWifiDeviceListener mDeviceListener = new GizWifiDeviceListener() {
        @Override
        public void didSetSubscribe(GizWifiErrorCode result, GizWifiDevice device, boolean isSubscribed) {
            progressDialog.cancel();
            if (GizWifiErrorCode.GIZ_SDK_SUCCESS == result) {
                mDevice = device;
                Intent intent = new Intent(getContext(), DeviceControlActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("GizWifiDevice", mDevice);
                intent.putExtras(bundle);
                startActivityForResult(intent,1);

            } else {
                if (device.isBind()) {
                    Toast.makeText(getContext(), ErrorHandleUtil.toastError(result,getContext()), Toast.LENGTH_SHORT).show();
                }
            }
        }

    };

    private GizWifiSDKListener mListener = new GizWifiSDKListener() {
        @Override
        public void didChannelIDBind(GizWifiErrorCode result) {
            Log.i("Apptest", result.toString());
            if (GizWifiErrorCode.GIZ_SDK_SUCCESS != result) {
                Toast.makeText(getContext(), ErrorHandleUtil.toastError(result, getContext()), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void didBindDevice(GizWifiErrorCode result, String did) {
            if (result != GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                Toast.makeText(getContext(), ErrorHandleUtil.toastError(result, getContext()), Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(getContext(), "添加成功", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        public void didUnbindDevice(GizWifiErrorCode result, String did) {
            progressDialog.cancel();
            if (GizWifiErrorCode.GIZ_SDK_SUCCESS != result) {
                // String unBoundFailed = (String) getText(R.string.unbound_failed);
                Toast.makeText(getContext(), ErrorHandleUtil.toastError(result,getContext()), Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void didDiscovered(GizWifiErrorCode result, List<GizWifiDevice> deviceList) {
            deviceslist.clear();
            for (GizWifiDevice gizWifiDevice : deviceList) {
               deviceslist.add(gizWifiDevice);
            }
            updateUI();
        }
    };

    /**
     * 设置ProgressDialog
     */
    public void setProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        String loadingText = "处理中，请稍等";
        progressDialog.setMessage(loadingText);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void initData() {
        boundMessage = new ArrayList<>();
        ProductKeyList = GosDeploy.setProductKeyList();
        mUid = SpUtil.getString(getContext(),"uid");
        mToken = SpUtil.getString(getContext(),"token");
    }

    private void updateUI() {
        if (deviceslist.isEmpty()) {
            idSwipeLy.setVisibility(View.GONE);
            idSwipeLy1.setVisibility(View.VISIBLE);
            return;
        } else {
            idSwipeLy1.setVisibility(View.GONE);
            idSwipeLy.setVisibility(View.VISIBLE);
        }

        boundDevicesList = new ArrayList<>();
        foundDevicesList = new ArrayList<>();
        offlineDevicesList = new ArrayList<>();

        for (GizWifiDevice gizWifiDevice : deviceslist) {
            if (GizWifiDeviceNetStatus.GizDeviceOnline == gizWifiDevice.getNetStatus()
                    || GizWifiDeviceNetStatus.GizDeviceControlled == gizWifiDevice.getNetStatus()) {
                if (gizWifiDevice.isBind()) {
                    boundDevicesList.add(gizWifiDevice);
                } else {
                    foundDevicesList.add(gizWifiDevice);
                }
            } else {
                offlineDevicesList.add(gizWifiDevice);
            }
        }


        if (boundDevicesList.isEmpty()) {
            slvBoundDevices.setVisibility(View.GONE);
            llNoBoundDevices.setVisibility(View.VISIBLE);
        } else {
            mAdapter = new DeviceListAdapter(getContext(), boundDevicesList);
            mAdapter.setHandler(handler);
            slvBoundDevices.setAdapter(mAdapter);
            llNoBoundDevices.setVisibility(View.GONE);
            slvBoundDevices.setVisibility(View.VISIBLE);
        }

        if (foundDevicesList.isEmpty()) {
            slvFoundDevices.setVisibility(View.GONE);
            llNoFoundDevices.setVisibility(View.VISIBLE);
        } else {
            mAdapter = new DeviceListAdapter(getContext(), foundDevicesList);
            slvFoundDevices.setAdapter(mAdapter);
            llNoFoundDevices.setVisibility(View.GONE);
            slvFoundDevices.setVisibility(View.VISIBLE);
        }

        if (offlineDevicesList.isEmpty()) {
            slvOfflineDevices.setVisibility(View.GONE);
            llNoOfflineDevices.setVisibility(View.VISIBLE);
        } else {
            mAdapter = new DeviceListAdapter(getContext(), offlineDevicesList);
            mAdapter.setHandler(handler);
            slvOfflineDevices.setAdapter(mAdapter);
            llNoOfflineDevices.setVisibility(View.GONE);
            slvOfflineDevices.setVisibility(View.VISIBLE);
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UNBOUND:
                    progressDialog.show();
                    GizWifiSDK.sharedInstance().unbindDevice(mUid, mToken, msg.obj.toString());
                    break;
            }
        }
    };



    @Override
    protected int setLayout() {
        return R.layout.fragment_device;
    }

    @Override
    protected void init() {
        gosDeploy = new GosDeploy(getContext());
        setProgressDialog();
        icBoundDevices = mRootView.findViewById(R.id.icBoundDevices);
        icFoundDevices = mRootView.findViewById(R.id.icFoundDevices);
        icOfflineDevices = mRootView.findViewById(R.id.icOfflineDevices);

        slvBoundDevices = icBoundDevices.findViewById(R.id.slideListView1);
        slvFoundDevices = icFoundDevices.findViewById(R.id.slideListView1);
        slvOfflineDevices = icOfflineDevices.findViewById(R.id.slideListView1);

        llNoBoundDevices = icBoundDevices.findViewById(R.id.llHaveNotDevice);
        llNoFoundDevices = icFoundDevices.findViewById(R.id.llHaveNotDevice);
        llNoOfflineDevices = icOfflineDevices.findViewById(R.id.llHaveNotDevice);

        tvBoundDevicesListTitle = icBoundDevices.findViewById(R.id.tvListViewTitle);
        tvFoundDevicesListTitle = icFoundDevices.findViewById(R.id.tvListViewTitle);
        tvOfflineDevicesListTitle = icOfflineDevices.findViewById(R.id.tvListViewTitle);

        String boundDevicesListTitle = (String) getText(R.string.bound_divices);
        tvBoundDevicesListTitle.setText(boundDevicesListTitle);
        String foundDevicesListTitle = (String) getText(R.string.found_devices);
        tvFoundDevicesListTitle.setText(foundDevicesListTitle);
        String offlineDevicesListTitle = (String) getText(R.string.offline_devices);
        tvOfflineDevicesListTitle.setText(offlineDevicesListTitle);

        //下拉刷新
        idSwipeLy.setOnRefreshListener(this);
        idSwipeLy1.setOnRefreshListener(this);
        initData();

        slvBoundDevices.initSlideMode(SlideListView2.MOD_RIGHT);
        slvOfflineDevices.initSlideMode(SlideListView2.MOD_RIGHT);
    }

    @Override
    protected void setData() {
        if (progressDialog.isShowing()) {
            progressDialog.cancel();
        }

        initEvent();

    }

    @Override
    public void onResume() {
        super.onResume();
        GizWifiSDK.sharedInstance().setListener(mListener);
        deviceslist = GizWifiSDK.sharedInstance().getDeviceList();
        updateUI();
        if (deviceslist == null){
            linearLayout.setVisibility(View.GONE);
        }else if (deviceslist.size() == 0){
            linearLayout.setVisibility(View.GONE);
        }
        GizWifiSDK.sharedInstance().getBoundDevices(mUid, mToken, ProductKeyList);
        // TODO GosMessageHandler.getSingleInstance().SetHandler(handler);
        if (boundMessage.size() != 0) {
            progressDialog.show();
            if (boundMessage.size() == 2) {
                GizWifiSDK.sharedInstance().bindDevice(mUid, mToken, boundMessage.get(0), boundMessage.get(1), null);
            } else if (boundMessage.size() == 1) {
                GizWifiSDK.sharedInstance().bindDeviceByQRCode(mUid, mToken, boundMessage.get(0));
            } else if (boundMessage.size() == 3) {

                GizDeviceSharing.checkDeviceSharingInfoByQRCode(SpUtil.getString(getContext(),"Token"), boundMessage.get(2));
            } else {
                Log.i("Apptest", "ListSize:" + boundMessage.size());
            }
        }
    }

    private void initEvent() {
        slvFoundDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                progressDialog.show();
                slvFoundDevices.setEnabled(false);
                slvFoundDevices.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        slvFoundDevices.setEnabled(true);
                    }
                }, 3000);
                GizWifiDevice device = foundDevicesList.get(position);
                device.setListener(mDeviceListener);

                String productKey = device.getProductKey();
                if (productKey.equals("ac102d79bbb346389e1255eafca0cfd2")) {
                    device.setSubscribe("b83feefa750740f6862380043c0f78fb", true);
                } else {
                    device.setSubscribe(true);
                }

            }
        });

        slvBoundDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                progressDialog.show();
                slvBoundDevices.setEnabled(false);
                slvBoundDevices.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        slvBoundDevices.setEnabled(true);
                    }
                }, 3000);
                GizWifiDevice device = boundDevicesList.get(position);
                device.setListener(mDeviceListener);
                String productKey = device.getProductKey();
                if (productKey.equals("ac102d79bbb346389e1255eafca0cfd2")) {
                    device.setSubscribe("b83feefa750740f6862380043c0f78fb", true);
                } else {
                    device.setSubscribe(true);
                }

            }
        });

    }

    //下拉刷新的逻辑
    @Override
    public void onRefresh() {
        idSwipeLy.setRefreshing(false);
        idSwipeLy1.setRefreshing(false);
        if (!mUid.isEmpty() && !mToken.isEmpty()) {
            deviceslist = GizWifiSDK.sharedInstance().getDeviceList();
            updateUI();
        }
    }

    @OnClick({R.id.add_Devices,R.id.iv_add_Devices})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.add_Devices:
                intent.setClass(getContext(), ConfigActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_add_Devices:
                intent.setClass(getContext(), ConfigActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
