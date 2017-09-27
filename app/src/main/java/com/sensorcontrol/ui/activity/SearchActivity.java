//package com.sensorcontrol.ui.activity;
//
//import android.content.Intent;
//import android.os.Handler;
//import android.support.v7.widget.Toolbar;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import com.dd.CircularProgressButton;
//import com.inuker.bluetooth.library.BluetoothClient;
//import com.inuker.bluetooth.library.search.SearchRequest;
//import com.inuker.bluetooth.library.search.SearchResult;
//import com.inuker.bluetooth.library.search.response.SearchResponse;
//import com.sensorcontrol.R;
//import com.sensorcontrol.module.ClientManager;
//import com.sensorcontrol.base.BaseActivity;
//import com.sensorcontrol.bean.DetailItem;
//import com.sensorcontrol.ui.adapter.DeviceAdapter;
//import java.util.List;
//import butterknife.BindView;
///**
// * Created by lizhe on 2017/9/21 0021.
// * 目标定在月亮之上，即使失败，也可以落在众星之间。
// */
//
//public class SearchActivity extends BaseActivity {
//
//    @BindView(R.id.toolBar)
//    Toolbar toolBar;
//    @BindView(R.id.listView)
//    ListView listView;
//    @BindView(R.id.circular)
//    CircularProgressButton circular;
//    @BindView(R.id.empty)
//    LinearLayout empty;
//
//    private BluetoothClient mClient;
//    private DeviceAdapter mAdapter;
//    private List<DetailItem> mList;
//
//    private SearchRequest request = new SearchRequest.Builder()
//            .searchBluetoothLeDevice(3000, 2)   // 先扫BLE设备3次，每次3s
//            .build();
//
//    private String mac;
//    private Handler mHandler;
//
//
//
//
//    @Override
//    protected int setLayout() {
//        return R.layout.activity_search;
//    }
//
//    @Override
//    protected void init() {
//        initToolbar(toolBar);
//        mClient = ClientManager.getClient();
//        mAdapter = new DeviceAdapter(this);
//        mHandler = new Handler();
//        circular.setIndeterminateProgressMode(true);
//    }
//
//    @Override
//    protected void setData() {
//
//        listView.setAdapter(mAdapter);
//        circular.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                circular.setProgress(0);
//                if (mClient.isBluetoothOpened()) {
//                    search();
//                }else {
//                    mClient.openBluetooth();
//
//                }
//
//            }
//        });
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                stop();
//                mac = mAdapter.getItem(i).getAddress();
//                Intent intent = new Intent();
//                intent.setClass(getApplicationContext(), ControlActivity.class);
//                intent.putExtra("mac",mac);
//                startActivity(intent);
//
//            }
//        });
//    }
//
//    private void search() {
//        mClient.search(request, new SearchResponse() {
//            @Override
//            public void onSearchStarted() {
//                circular.setProgress(50);
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (mAdapter.getCount() > 0) {
//                            circular.setProgress(100);
//                            circular.setCompleteText("搜索到" + mAdapter.getCount() + "台设配");
//                            listView.setVisibility(View.VISIBLE);
//                            empty.setVisibility(View.GONE);
//                        }else {
//                            circular.setProgress(-1);
//                            circular.setCompleteText("请重试");
//                            search();
//                        }
//
//                    }
//                },6000);
//            }
//
//            @Override
//            public void onDeviceFounded(SearchResult device) {
//                mAdapter.addData(device);
//            }
//
//            @Override
//            public void onSearchStopped() {
//
//            }
//
//            @Override
//            public void onSearchCanceled() {
//
//            }
//        });
//    }
//
//
//
//    private void stop() {
//        mClient.stopSearch();
//    }
//
//
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//}
