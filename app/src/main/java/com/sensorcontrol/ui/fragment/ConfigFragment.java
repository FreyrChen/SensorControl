package com.sensorcontrol.ui.fragment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.inuker.bluetooth.library.search.SearchResult;
import com.sensorcontrol.R;
import com.sensorcontrol.base.BaseFragment;
import com.sensorcontrol.bean.EventBean;
import com.sensorcontrol.module.BluetoothModule;
import com.sensorcontrol.ui.activity.MainActivity;
import com.sensorcontrol.ui.adapter.DeviceAdapter;
import org.greenrobot.eventbus.EventBus;
import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by lizhe on 2017/9/25 0025.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class ConfigFragment extends BaseFragment {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.refresh)
    ImageView refresh;
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.rl_layout)
    RelativeLayout mRelativeLayout;
    @BindView(R.id.empty_a)
    LinearLayout mEmpty;

    private ProgressDialog pDialog;
    private DeviceAdapter mAdapter;
    private String mac = "";
    private boolean flag = false;
    private MainActivity activity;
    private boolean isShow = true;

    @Override
    protected int setLayout() {
        return R.layout.config_fragment;
    }

    @Override
    protected void init() {
        activity  = (MainActivity)(getActivity());
        mAdapter = new DeviceAdapter(getContext());
    }

    @Override
    protected void setData() {
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mac.equals("")) {
                    mac = mAdapter.getItem(i).getAddress();
                    mController.conn(mac);
                }else {
                    mController.disconnect(mac);
                    mController.unNotify(mac);
                    mac = mAdapter.getItem(i).getAddress();
                    mController.conn(mac);
                }
            }
        });
    }

    @OnClick(R.id.refresh)
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.refresh:
                if (!flag){
                    mController.search();
                }else {
                    mController.stopSearch();
                }
                break;
        }
    }

    private void showDialog(){
        pDialog = new ProgressDialog(getContext());
        pDialog.setIndeterminate(true);
        pDialog.setMessage("正在进行连接.....");
        pDialog.show();
    }


    @Override
    protected void handleMessage1(Message msg) {
        switch (msg.what){
            case BluetoothModule.SEARCH_STARTED:
                showDialog();
                flag = false;
                break;
            case BluetoothModule.SEARCH_STOPPED:
                flag = true;
                pDialog.dismiss();
                Snackbar.make(mRelativeLayout,"蓝牙搜索中断",Snackbar.LENGTH_SHORT).show();
                break;
            case BluetoothModule.SEARCH_CANCELED:
                pDialog.dismiss();
                break;
            case BluetoothModule.CONN_ERROR:
                final Snackbar snackbar1 =  Snackbar.make(mRelativeLayout,"蓝牙连接失败",Snackbar.LENGTH_INDEFINITE);
                snackbar1.setAction("重试", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mController.conn(mac);
                        snackbar1.dismiss();
                    }
                }).show();
                break;
            case BluetoothModule.NOTITY_ERROR:
                final Snackbar snackbar =  Snackbar.make(mRelativeLayout,"打开通知失败",Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("重试", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mController.openNotify(mac);
                        snackbar.dismiss();
                    }
                }).show();
                break;
            case BluetoothModule.bleGattProfile:
                //TODO:
                if (isShow) {
                    Snackbar.make(mRelativeLayout, "蓝牙连接成功", Snackbar.LENGTH_SHORT).show();
                    isShow = false;
                }
                activity.setShowFragment(MainActivity.HOMEPAGE);
                EventBus.getDefault().post(new EventBean(mac));
                activity.setBtn(MainActivity.HOMEPAGE);
                mController.openNotify(mac);
                break;
            case BluetoothModule.DEVICE:
                SearchResult searchResult = (SearchResult) msg.obj;
                mAdapter.addData(searchResult);
                mac = searchResult.getAddress();
                mEmpty.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mController.stopSearch();
        mController.unNotify(mac);
        mController.disconnect(mac);
    }

}
