package com.sensorcontrol.ui.activity.wifi;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.sensorcontrol.R;
import com.sensorcontrol.app.MessageCenter;
import com.sensorcontrol.base.WifiConnActivity;
import com.sensorcontrol.view.SildeListView;
import com.sensorcontrol.view.VerticalSwipeRefreshLayout;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by lizhe on 2017/10/11 0011.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class DeviceListActivity extends WifiConnActivity implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.imgNoDevice)
    ImageView imgNoDevice;
    @BindView(R.id.btnNoDevice)
    Button btnNoDevice;
    @BindView(R.id.llNoDevice)
    ScrollView llNoDevice;
    @BindView(R.id.id_swipe_ly1)
    VerticalSwipeRefreshLayout idSwipeLy1;
    @BindView(R.id.tvListViewTitle)
    TextView tvListViewTitle;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.llHaveNotDevice)
    LinearLayout llHaveNotDevice;
    @BindView(R.id.slideListView1)
    SildeListView slideListView1;
    @BindView(R.id.svListGroup)
    ScrollView svListGroup;
    @BindView(R.id.id_swipe_ly)
    VerticalSwipeRefreshLayout idSwipeLy;

    @Override
    protected int setLayout() {
        return R.layout.activity_gos_device_list;
    }

    @Override
    protected void init() {
        MessageCenter.getInstance(this);
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

    @OnClick({R.id.btnNoDevice})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.btnNoDevice :
                break;
        }
    }


    @Override
    public void onRefresh() {

    }
}
