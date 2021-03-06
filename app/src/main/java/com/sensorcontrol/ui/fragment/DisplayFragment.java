package com.sensorcontrol.ui.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchResult;
import com.sensorcontrol.R;
import com.sensorcontrol.base.BaseFragment;
import com.sensorcontrol.bean.ATBean;
import com.sensorcontrol.bean.CmdBean;
import com.sensorcontrol.bean.EventBean;
import com.sensorcontrol.bean.ListBean;
import com.sensorcontrol.controller.BluetoothController;
import com.sensorcontrol.module.BluetoothModule;
import com.sensorcontrol.ui.activity.BtnListActivity;
import com.sensorcontrol.ui.activity.DeviceControlActivity;
import com.sensorcontrol.ui.activity.MainActivity;
import com.sensorcontrol.ui.adapter.BtnAdapter;
import com.sensorcontrol.ui.adapter.BtnListAdapter;
import com.sensorcontrol.ui.adapter.DataAdapter;
import com.sensorcontrol.ui.adapter.SliderAdapter;
import com.sensorcontrol.util.BLEDataUtil;
import com.sensorcontrol.util.BmpUtils;
import com.sensorcontrol.util.FileUtil;
import com.sensorcontrol.util.HexStrUtils;
import com.sensorcontrol.util.SpUtil;
import com.sensorcontrol.util.StringUtils;
import com.sensorcontrol.view.ATDialog;
import com.sensorcontrol.view.BarView;
import com.sensorcontrol.view.InputDialog;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;

/**
 * Created by lizhe on 2017/9/25 0025.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class DisplayFragment extends BaseFragment implements BluetoothModule.NotifyData, BtnAdapter.OnLongClickListener,
        BtnAdapter.OnItemClickListener, InputDialog.OnCancelListener, InputDialog.OnConfirmListener ,BtnAdapter.OnSelectFileListener,BluetoothController.WriteSuccessListener{

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.displayData)
    ListView displayData;
    @BindView(R.id.histogramView)
    BarView mChart;
    @BindView(R.id.slider)
    TextView slider;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.recyclerView_slider)
    RecyclerView recyclerViewSlider;
    @BindView(R.id.slider_btn)
    ImageView sliderBtn;
    @BindView(R.id.delete_img)
    ImageView delete_img;
    @BindView(R.id.ll_layout)
    LinearLayout llLayout;
    private boolean isConn = false;
    private String mac;
    private DataAdapter mDataAdapter;
    private List<CmdBean> mList;
    private List<CmdBean> mList1;
    private ATBean mATBean;
    private ATDialog aDialog;

    private RecyclerView.LayoutManager layoutManager;
    private BtnAdapter mBtnAdapter;
    private Handler handler;
    private Runnable runnable;
    private InputDialog dialog;
    private int position;
    private SliderAdapter mSliderAdapter;
    private boolean flag = true;
    private ProgressDialog progressDialog1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setLayout() {
        return R.layout.display_fragment;
    }

    @Override
    protected void init() {
        initSp();
        initChart();
        mList = SpUtil.getList(getContext(), "btn");
        if (mList == null){
            mList = new ArrayList<>();
        }
        mList1 = SpUtil.getList(getContext(), "slider");
        if (mList1 == null){
            mList1 = new ArrayList<>();
        }
        handler = new Handler();
        MainActivity activity = (MainActivity) (getActivity());
        aDialog = new ATDialog(getContext());
        mDataAdapter = new DataAdapter(getContext());
        BluetoothModule.getBluetoothModule().setNotifyData(this);
        mController.setWriteSuccessListener(this);
        layoutManager = new GridLayoutManager(getContext(), 3);
        mBtnAdapter = new BtnAdapter(getContext());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager = new GridLayoutManager(getContext(), 1);
        mSliderAdapter = new SliderAdapter(getContext());
        recyclerViewSlider.setLayoutManager(layoutManager);
        mBtnAdapter.setmList(mList);
        mSliderAdapter.setmList(mList1);
        progressDialog1 = new ProgressDialog(getContext());
        progressDialog1.setCanceledOnTouchOutside(false);
        progressDialog1.setCancelable(false);
        progressDialog1.setMessage("正在发送");
        sliderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag) {
                    recyclerViewSlider.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    slider.setText("切换至按钮控制模式");
                    sliderBtn.setImageResource(R.drawable.biao);
                    flag = false;
                } else {
                    recyclerViewSlider.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    slider.setText("切换至滑动条控制模式");
                    sliderBtn.setImageResource(R.drawable.slider);
                    flag = true;
                }
            }
        });
        delete_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag){
                    Intent intent = new Intent();
                    intent.putExtra("data",new ListBean(mBtnAdapter.getmList()));
                    intent.putExtra("type",0);
                    intent.setClass(getContext(),BtnListActivity.class);
                    startActivityForResult(intent,0);
                }else {
                    Intent intent = new Intent();
                    intent.putExtra("data",new ListBean(mSliderAdapter.getmList()));
                    intent.putExtra("type",1);
                    intent.setClass(getContext(),BtnListActivity.class);
                    startActivityForResult(intent,1);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if (resultCode == BtnListAdapter.BACKDATA){
                    ListBean bean = (ListBean) data.getSerializableExtra("data");
                    List<CmdBean> list = bean.getList();
                    mBtnAdapter.setmList(list);
                    SpUtil.putList(getContext(), "btn",mBtnAdapter.getmList());
                }
                break;
            case 1:
                if (resultCode == BtnListAdapter.BACKDATA){
                    ListBean bean = (ListBean) data.getSerializableExtra("data");
                    List<CmdBean> list = bean.getList();
                    mSliderAdapter.setmList(list);
                    SpUtil.putList(getContext(), "slider",mSliderAdapter.getmList());
                }
                break;
        }
    }

    private void showNormalDialog(final Uri uri){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(getContext());
        normalDialog.setTitle("发送文件");
        normalDialog.setMessage("文件路径："+uri.getPath().toString());
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendFileData(uri);
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // 显示
        normalDialog.show();
    }

    private Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BLEDataUtil.BLE_DATA:
                    mController.write(mac, (byte[]) msg.obj);
                    break;
            }
        }
    };

    Runnable run = new Runnable(){
        @Override
        public void run() {
            for (int i = 0; i < sendByte.length; i++) {
                try {
                    Thread.currentThread().sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendByte1 = sendByte[i];
                mController.write(mac,sendByte1);
                progressDialog1.incrementProgressBy((int) ((100f/(float) sendByte.length)*(float) i));
                progressDialog1.setProgressNumberFormat("%1d"+"/"+"%2d");

                if (i == sendByte.length-1){
                    System.out.println(i);
                    Message msg = new Message();
                    msg.what = handler_key.D1.ordinal();
                    msg.obj = i;
                    mHandler.sendMessage(msg);
                }
            }
        }
    };

    @Override
    public void writeSuccess(int state) {
        if (BluetoothModule.WRITE_SUCCESS == state){

        }else if (BluetoothModule.WRITE_ERROR == state){
            Toast.makeText(getContext(), "写入失败", Toast.LENGTH_SHORT).show();
        }
    }

    private enum handler_key {
        D1,
        D2,
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler_key key = handler_key.values()[msg.what];
            switch (key) {
                case D1:
                    progressDialog1.cancel();
                    Toast.makeText(getContext(), "发送成功", Toast.LENGTH_SHORT).show();
                    break;
                case D2:
                    progressDialog1.cancel();
                    Toast.makeText(getContext(), "连接断开,发送到第 "+msg.obj +" 个包", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    Thread t;
    private byte[][] sendByte;
    private byte[] sendByte1;
    private int size;

    private void sendFileData(Uri uri) {
        progressDialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog1.show();
        byte[] s = new byte[0];
//        if (BmpUtils.getRealFilePath(getContext(),uri)){
            try {
                Bitmap sendBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
//                Bitmap sendBitmap = BmpUtils.getBitmapFormUri(getActivity(),uri);
                s = BmpUtils.getPicturePixel(sendBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
//        }else {
////            s = FileUtil.getByteArrayFromUri(uri,getContext());
//        }
        sendByte = BLEDataUtil.splitPackage1(s.length / 20, s.length % 20, s,20);
        t = new Thread(run);
        t.start();
//        byte[] b = new byte[200];
//        mController.write(mac,b);
    }

    private void initSp() {
        if (!SpUtil.getBoolean(getContext(), "first", false)) {
            SpUtil.putBoolean(getContext(), "first", true);

            List<CmdBean> list = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                list.add(new CmdBean(null, null, 0));
            }
            SpUtil.putList(getContext(), "btn", list);
        }
    }

    private void initChart() {
        mATBean = SpUtil.getObject(getContext(), "dataList");
        if (mATBean == null) {
            String[] s = new String[]{"AT+1", "AT+2", "AT+3"};
            int[] i = new int[]{0, 0, 0};
            mATBean = new ATBean(s, i);
        }
        mChart.setData(mATBean);
        mChart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                aDialog.setOnCancelListener(new ATDialog.OnCancelListener() {
                    @Override
                    public void onCancel(View view) {
                        aDialog.dismiss();
                    }
                });
                aDialog.setOnConfirmListener(new ATDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm(String name, String cmd, String time) {
                        if (name == null || name.equals("")) {
                            Toast.makeText(getActivity(), "光柱1为空", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (cmd == null || cmd.equals("")) {
                            Toast.makeText(getActivity(), "光柱2为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (time == null || cmd.equals("")) {
                            Toast.makeText(getActivity(), "光柱3为空", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (name == null && cmd == null && time == null) {
                            return;
                        } else {
                            aDialog.dismiss();
                            String[] s = new String[]{"AT+" + name, "AT+" + cmd, "AT+" + time};
                            int[] i = new int[]{0, 0, 0};
                            mATBean = new ATBean(s, i);
                            SpUtil.putObject(getContext(), "dataList", mATBean);
                            mChart.setData(mATBean);
                        }
                    }
                });
                aDialog.show();
                return true;
            }
        });

    }

    @Override
    protected void setData() {
        displayData.setAdapter(mDataAdapter);
        recyclerView.setAdapter(mBtnAdapter);
        recyclerViewSlider.setAdapter(mSliderAdapter);

        mBtnAdapter.setOnLongClickListener(this);
        mBtnAdapter.setOnItemClickListener(this);
        mBtnAdapter.setOnSelectFileListener(this);
        mBtnAdapter.setOnAddItemListener(new BtnAdapter.OnAddItemListener() {
            @Override
            public void onAddItem(CmdBean cmdBean) {

                dialog = new InputDialog(getContext());
                dialog.show();
                dialog.getRelativeLayout_rl_range().setVisibility(View.GONE);
                dialog.setOnCancelListener(new InputDialog.OnCancelListener() {
                    @Override
                    public void onCancel(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.setOnConfirmListener(new InputDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm(String name, String cmd, String time,String min, String max) {
                        if (name == null || name.equals("")) {
                            Snackbar.make(llLayout, "名字为空", Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        if (cmd == null || cmd.equals("")) {
                            Snackbar.make(llLayout, "AT命令为空", Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        if (name == null && cmd == null) {
                            return;
                        } else {
                            dialog.dismiss();
                            int num;
                            if (time.equals("")){
                                num = 0;
                            }else {
                                num = Integer.valueOf(time);
                            }
                            CmdBean bean = new CmdBean(cmd,name,num);

                            mBtnAdapter.upData(bean);
                            List list = new ArrayList();
                            list.add(0,bean);
                            list.addAll(mList);
                            SpUtil.putList(getContext(), "btn",mBtnAdapter.getmList());
                        }
                    }
                });

            }
        });

        mSliderAdapter.setOnAddItemClickListener(new SliderAdapter.OnAddItemClickListener() {
            @Override
            public void onAddItem(List<CmdBean> cmdBean) {
                final InputDialog aDialog1 = new InputDialog(getContext());
                aDialog1.show();
                aDialog1.getRelativeLayout().setVisibility(View.GONE);
                aDialog1.setOnCancelListener(new InputDialog.OnCancelListener() {
                    @Override
                    public void onCancel(View view) {
                        aDialog1.dismiss();
                    }
                });
                aDialog1.setOnConfirmListener(new InputDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm(String name, String cmd, String time,String min, String max) {
                        if (name == null || name.equals("")) {
                            Snackbar.make(llLayout, "名字为空", Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        if (cmd == null || cmd.equals("")) {
                            Snackbar.make(llLayout, "AT命令为空", Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        if (name == null && cmd == null) {
                            return;
                        } else {
                            int xiao;
                            int da;
                            if(TextUtils.isEmpty(min)){
                               xiao = 0;
                            }else {
                                xiao = Integer.valueOf(min);
                            }
                            if (TextUtils.isEmpty(max)){
                                da = 1024;
                            }else {
                                da = Integer.valueOf(max);
                            }

                            aDialog1.dismiss();
                            CmdBean bean = new CmdBean(cmd,name,0,xiao,da);

                            mSliderAdapter.upData(bean);
                            SpUtil.putList(getContext(), "slider", mSliderAdapter.getmList());
                        }
                    }
                });
            }
        });

        mSliderAdapter.setOnSeekBarChangeListener(new SliderAdapter.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(String name, int progress) {
                if (isConn) {
                    mController.write(mac, "AT+" + name + "=" + progress + "\r\n");
                } else {
                    Snackbar.make(llLayout, "未连接蓝牙", Snackbar.LENGTH_INDEFINITE)
                            .setAction("去接连", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((MainActivity) (getActivity())).setShowFragment(MainActivity.CONFIG);
                                    ((MainActivity) (getActivity())).setBtn(MainActivity.CONFIG);
                                }
                            }).show();
                }
            }
        });

        mSliderAdapter.setOnLongClickListener(new SliderAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(final int position, final List<CmdBean> mList) {
                final InputDialog aDialog1 = new InputDialog(getContext());
                aDialog1.show();
                aDialog1.getRelativeLayout().setVisibility(View.GONE);
                aDialog1.setOnCancelListener(new InputDialog.OnCancelListener() {
                    @Override
                    public void onCancel(View view) {
                        aDialog1.dismiss();
                    }
                });
                aDialog1.setOnConfirmListener(new InputDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm(String name, String cmd, String time,String min, String max) {
                        if (name == null || name.equals("")) {
                            Snackbar.make(llLayout, "名字为空", Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        if (cmd == null || cmd.equals("")) {
                            Snackbar.make(llLayout, "AT命令为空", Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        if (name == null && cmd == null) {
                            return;
                        } else {
                            aDialog1.dismiss();
                            mList1.get(position).setName(name);
                            mList1.get(position).setAT(cmd);
                            if(TextUtils.isEmpty(min)){
                                mList1.get(position).setMin(0);
                            }else {
                                mList1.get(position).setMin(Integer.valueOf(min));
                            }
                            if (TextUtils.isEmpty(max)){
                                mList1.get(position).setMax(1024);
                            }else {
                                mList1.get(position).setMax(Integer.valueOf(Integer.valueOf(max)));
                            }
                            mSliderAdapter.upItem(mList1);
                            SpUtil.putList(getContext(), "slider", mSliderAdapter.getmList());
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void handleMessage1(Message msg) {
        switch (msg.what) {
            case BluetoothModule.READ_ERROR:
                Snackbar.make(llLayout, "读取失败", Snackbar.LENGTH_SHORT).show();
                break;
            case BluetoothModule.WRITE_ERROR:
                Snackbar.make(llLayout, "发送失败", Snackbar.LENGTH_SHORT).show();
                break;
            case BluetoothModule.WRITE_SUCCESS:
                Snackbar.make(llLayout, "发送成功", Snackbar.LENGTH_SHORT).show();
                break;
            case BluetoothModule.readData:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Object event) {

        if (event instanceof EventBean && event != null) {
            EventBean bean = (EventBean) event;
            mac = bean.getMac();
            isConn = true;
        }
        if (event instanceof Uri && event != null) {
            Uri uri = (Uri) event;
            showNormalDialog(uri);
        }
    }

    private int num;
    private String AT;

    @Override
    public void notifyData(String data) {
        mDataAdapter.addItem(data);
        displayData.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                displayData.setSelection(displayData.getCount() - 1);
            }
        });
        String zhi = data;
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
        if (AT != null) {
            setBarData(AT);
        }
    }

    private void setBarData(@Nullable String AT) {
        for (int i = 0; i < 3; i++) {
            if (AT.equals(mATBean.getAT()[i])) {
                mATBean.getAT()[i] = AT;
                mATBean.getNum()[i] = num;
                mChart.setData(mATBean);
                SpUtil.putObject(getContext(), "dataList", mATBean);
            }
        }

    }

    @Override
    public void onClick(int position, CmdBean cmdBean) {
        if (cmdBean.getName() != null && cmdBean.getAT() != null) {
            if (isConn) {
                sendData(cmdBean.getAT(), cmdBean.getTime());
            } else {
                Snackbar.make(llLayout, "未连接蓝牙", Snackbar.LENGTH_INDEFINITE)
                        .setAction("去接连", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ((MainActivity) (getActivity())).setShowFragment(MainActivity.CONFIG);
                            }
                        }).show();
            }
        } else {
            Snackbar.make(llLayout, "请先编辑", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void sendData(final String at, final int time) {
        if (time <= 0) {
            mController.write(mac, "AT+" + at + "\r\n");
        } else {
            runnable = new Runnable() {
                @Override
                public void run() {
                    mController.write(mac, "AT+" + at + "\r\n");
                    handler.postDelayed(this, time);
                }
            };
            handler.postDelayed(runnable, time);
        }
    }

    @Override
    public void onLongClick(int position, List<CmdBean> mList) {
        dialog = new InputDialog(getContext());
        dialog.show();
        dialog.getRelativeLayout_rl_range().setVisibility(View.GONE);
        dialog.setOnCancelListener(this);
        dialog.setOnConfirmListener(this);

        this.position = position;
        this.mList = mList;
    }


    @Override
    public void onCancel(View view) {
        dialog.dismiss();
    }

    @Override
    public void onConfirm(String name, String cmd, String time,String min, String max) {
        if (name == null || name.equals("")) {
            Snackbar.make(llLayout, "名字为空", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (cmd == null || cmd.equals("")) {
            Snackbar.make(llLayout, "AT命令为空", Snackbar.LENGTH_SHORT).show();
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
            mBtnAdapter.upItem(mList);

            SpUtil.putList(getContext(), "btn", mBtnAdapter.getmList());
        }
    }

    @Override
    public void onSelectFile() {
        if (isConn) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            getActivity().startActivityForResult(intent, 77);
        }else {
            Snackbar.make(llLayout, "未连接蓝牙", Snackbar.LENGTH_INDEFINITE)
                    .setAction("去接连", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((MainActivity) (getActivity())).setShowFragment(MainActivity.CONFIG);
                        }
                    }).show();
        }
    }


}
