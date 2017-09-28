package com.sensorcontrol.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

import com.sensorcontrol.R;
import com.sensorcontrol.base.BaseFragment;
import com.sensorcontrol.bean.ATBean;
import com.sensorcontrol.bean.CmdBean;
import com.sensorcontrol.bean.EventBean;
import com.sensorcontrol.module.BluetoothModule;
import com.sensorcontrol.ui.activity.MainActivity;
import com.sensorcontrol.ui.adapter.BtnAdapter;
import com.sensorcontrol.ui.adapter.DataAdapter;
import com.sensorcontrol.ui.adapter.SliderAdapter;
import com.sensorcontrol.util.SpUtil;
import com.sensorcontrol.view.ATDialog;
import com.sensorcontrol.view.BarView;
import com.sensorcontrol.view.InputDialog;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import butterknife.BindView;

/**
 * Created by lizhe on 2017/9/25 0025.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class DisplayFragment extends BaseFragment implements BluetoothModule.NotifyData, BtnAdapter.OnLongClickListener,
        BtnAdapter.OnItemClickListener, InputDialog.OnCancelListener, InputDialog.OnConfirmListener {

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
    LinearLayout sliderBtn;
    @BindView(R.id.ll_layout)
    LinearLayout llLayout;
    @BindView(R.id.slider_img)
    ImageView sliderImg;

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
        layoutManager = new GridLayoutManager(getContext(), 3);
        mBtnAdapter = new BtnAdapter(getContext());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager = new GridLayoutManager(getContext(), 1);
        mSliderAdapter = new SliderAdapter(getContext());
        recyclerViewSlider.setLayoutManager(layoutManager);
        mBtnAdapter.setmList(mList);
        mSliderAdapter.setmList(mList1);
        sliderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag) {
                    recyclerViewSlider.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    slider.setText("切换至按钮控制模式");
                    sliderImg.setImageResource(R.drawable.biao);
                    flag = false;
                } else {
                    recyclerViewSlider.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    slider.setText("切换至滑动条控制模式");
                    sliderImg.setImageResource(R.drawable.slider);
                    flag = true;
                }
            }
        });
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
                                xiao = Integer.valueOf(max);
                            }
                            if (TextUtils.isEmpty(max)){
                                da = 1024;
                            }else {
                                da = Integer.valueOf(max);
                            }

                            aDialog1.dismiss();
                            CmdBean bean = new CmdBean(cmd,name,0,xiao,da);

                            mSliderAdapter.upData(bean);
                            mList1.add(0,bean);
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
                                mList1.get(position).setMin(Integer.valueOf(Integer.valueOf(max)));
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
    public void onEvent(EventBean event) {
        if (event != null) {
            mac = event.getMac();
            isConn = true;
        }
    }

    private int num;
    private String AT;

    @Override
    public void notifyData(String data) {
        mDataAdapter.addItem(data);
        String zhi = new String(data);
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

}
