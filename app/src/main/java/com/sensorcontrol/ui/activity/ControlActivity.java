package com.sensorcontrol.ui.fragment;

import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.jonas.jgraph.graph.NChart;
import com.jonas.jgraph.models.NExcel;
import com.sensorcontrol.R;
import com.sensorcontrol.app.ClientManager;
import com.sensorcontrol.app.Constants;
import com.sensorcontrol.base.BaseActivity;
import com.sensorcontrol.bean.ATBean;
import com.sensorcontrol.bean.CmdBean;
import com.sensorcontrol.ui.adapter.BtnAdapter;
import com.sensorcontrol.ui.adapter.DataAdapter;
import com.sensorcontrol.util.SpUtil;
import com.sensorcontrol.view.ATDialog;
import com.sensorcontrol.view.BarView;
import com.sensorcontrol.view.InputDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.R.attr.data;
import static android.R.id.list;
import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DEVICE_DISCONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED;

/**
 * Created by lizhe on 2017/9/21 0021.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class ControlActivity extends BaseActivity implements BtnAdapter.OnItemClickListener, BtnAdapter.OnLongClickListener,
        InputDialog.OnCancelListener, InputDialog.OnConfirmListener{

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.displayData)
    ListView displayData;
    @BindView(R.id.layout)
    LinearLayout layout;
    @BindView(R.id.histogramView)
    BarView mChart;


    private BtnAdapter mBtnAdapter;

    private RecyclerView.LayoutManager layoutManager;

    private List<CmdBean> mList;

    private InputDialog dialog;

    private int position;

    private DataAdapter mDataAdapter;
    private String mac;
    private boolean mConnected;
    private SweetAlertDialog pDialog;

    private Handler handler;
    private Runnable runnable;
    private ATBean mATBean;
    private ATDialog aDialog;

    private BleConnectOptions options = new BleConnectOptions.Builder()
            .setConnectRetry(3)
            .setConnectTimeout(20000)
            .setServiceDiscoverRetry(3)
            .setServiceDiscoverTimeout(10000)
            .build();

    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {

            if (status == STATUS_CONNECTED) {
                mConnected = (status == STATUS_CONNECTED);
            } else if (status == STATUS_DISCONNECTED) {
                mConnected = (status == STATUS_DISCONNECTED);
                conn();
            }
        }
    };

    @Override
    protected int setLayout() {
        return R.layout.activity_control;
    }

    @Override
    protected void init() {
        initSp();

        mList = SpUtil.getList(this, "btn");
        layoutManager = new GridLayoutManager(this, 2);
        mBtnAdapter = new BtnAdapter(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mBtnAdapter);
        dialog = new InputDialog(this);
        aDialog = new ATDialog(this);
        mDataAdapter = new DataAdapter(this);
        displayData.setAdapter(mDataAdapter);
        initChart();
        handler = new Handler();
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("连接中");
        pDialog.setCancelable(false);
        pDialog.show();

    }

    private void initChart() {
        mATBean = SpUtil.getObject(this,"dataList");
        if (mATBean == null){
            String[] s = new String[]{"AT+1","AT+2","AT+3"};
            int[] i = new int[]{0,0,0};
            mATBean = new ATBean(s,i);
        }
        mChart.setData(mATBean);
        mChart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                aDialog.show();
                return true;
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        connectDeviceIfNeeded();
    }

    private void setBarData(@Nullable String AT){
        for (int i = 0; i < 3;i++){
            if (AT.equals(mATBean.getAT()[i])){
                mATBean.getAT()[i] = AT;
                mATBean.getNum()[i] = num;
                mChart.setData(mATBean);
                SpUtil.putObject(this,"dataList",mATBean);
            }
        }

    }

    private void initSp() {
        if (!SpUtil.getBoolean(this, "first", false)) {
            SpUtil.putBoolean(this, "first", true);

            List<CmdBean> list = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                list.add(new CmdBean(null, null, 0));
            }
            SpUtil.putList(this, "btn", list);
        }
    }

    @Override
    protected void setData() {
        mBtnAdapter.setmList(mList);
        mBtnAdapter.setOnItemClickListener(this);
        mBtnAdapter.setOnLongClickListener(this);
        dialog.setOnCancelListener(this);
        dialog.setOnConfirmListener(this);
        aDialog.setOnConfirmListener(new ATDialog.OnConfirmListener() {
            @Override
            public void onConfirm(String name, String cmd, String time) {
                if (name == null || name.equals("")) {
                    Toast.makeText(mActivity, "光柱1为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cmd == null || cmd.equals("")) {
                    Toast.makeText(mActivity, "光柱2为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (time == null || cmd.equals("")) {
                    Toast.makeText(mActivity, "光柱3为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (name == null && cmd == null && time == null) {
                    return;
                } else {
                    aDialog.dismiss();
                    String[] s = new String[]{"AT+"+name,"AT+"+cmd,"AT+"+time};
                    int[] i = new int[]{0,0,0};
                    mATBean = new ATBean(s,i);
                    SpUtil.putObject(getApplicationContext(),"dataList",mATBean);
                    mChart.setData(mATBean);
                }
            }
        });
        aDialog.setOnCancelListener(new ATDialog.OnCancelListener() {
            @Override
            public void onCancel(View view) {
                aDialog.dismiss();
            }
        });
        mac = getIntent().getStringExtra("mac");
        if (mac != null) {
            connectDeviceIfNeeded();
            ClientManager.getClient().registerConnectStatusListener(mac, mConnectStatusListener);
        }
    }

    @Override
    public void onClick(int position, CmdBean cmdBean) {
        if (cmdBean.getName() != null && cmdBean.getAT() != null) {
            sendData(cmdBean.getAT(), cmdBean.getTime());
        } else {
            Toast.makeText(this, "请先编辑", Toast.LENGTH_SHORT).show();
        }

    }

    private int num;
    private String AT;

    private void openNotify(String mac, UUID service, UUID characteristic) {
        ClientManager.getClient().notify(mac, service, characteristic, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
                mDataAdapter.addItem(value);
                String zhi = new String(value);
                if (!zhi.equals("")) {
                    String[] s = zhi.trim().split("=");
                    switch (s[0]) {
                        case "AT+1":
                            AT = "AT+1";
                            break;
                        case "AT+2":
                            AT = "AT+2";
                            break;
                        case "AT+3":
                            AT = "AT+3";
                            break;
                        case "AT+4":
                            AT = "AT+4";
                            break;
                        case "AT+5":
                            AT = "AT+5";
                            break;
                        case "AT+6":
                            AT = "AT+6";
                            break;
                        case "AT+7":
                            AT = "AT+7";
                            break;
                    }
                    num = Integer.valueOf(s[1]);

                }
                if (AT != null){
                    setBarData(AT);
                }
            }

            @Override
            public void onResponse(int code) {

            }
        });
    }



    private void unnotify(String mac, UUID service, UUID characteristic) {
        ClientManager.getClient().unnotify(mac, service, characteristic, new BleUnnotifyResponse() {
            @Override
            public void onResponse(int code) {
            }
        });
    }

    private void sendData(final String at, final int time) {
        if (time <= 0) {
            werte("AT+" + at + "\r\n");
        } else {
            runnable = new Runnable() {
                @Override
                public void run() {
                    werte("AT+" + at + "\r\n");
                    handler.postDelayed(this, time);
                }
            };
            handler.postDelayed(runnable, time);
        }
    }

    private void werte(String cmd) {
        ClientManager.getClient().write(mac, Constants.service, Constants.Characteristic, cmd.getBytes(), new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    Toast.makeText(getApplicationContext(), "输出成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "输出失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onLongClick(int position, List<CmdBean> mList) {
        dialog.show();
        this.position = position;
        this.mList = mList;
    }

    @Override
    public void onCancel(View view) {
        dialog.dismiss();
    }

    @Override
    public void onConfirm(String name, String cmd, String time) {

        if (name == null || name.equals("")) {
            Toast.makeText(mActivity, "名字为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cmd == null || cmd.equals("")) {
            Toast.makeText(mActivity, "AT命令为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name == null && cmd == null) {
            return;
        } else {
            dialog.dismiss();
            mList.get(position).setName(name);
            mList.get(position).setAT(cmd);
            if (time == null || time.equals("")) {
                mList.get(position).setTime(0);
            } else {
                mList.get(position).setTime(Integer.valueOf(time));
            }
            SpUtil.putList(this, "btn", mList);
            mBtnAdapter.setmList(mList);
        }
    }

    private void connectDeviceIfNeeded() {
        if (!mConnected) {
            conn();
        }
    }

    private void conn() {
        ClientManager.getClient().connect(mac, options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                if (code == REQUEST_SUCCESS) {
                    pDialog.dismiss();
                    layout.setVisibility(View.VISIBLE);
                    openNotify(mac, Constants.service, Constants.Characteristic);
                } else {
                    pDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "连接失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mac != null) {
            unnotify(mac, Constants.service, Constants.Characteristic);
            ClientManager.getClient().unregisterConnectStatusListener(mac, mConnectStatusListener);
            ClientManager.getClient().disconnect(mac);
            handler.removeCallbacks(runnable);// 关闭定时器处理
        }
    }
}
