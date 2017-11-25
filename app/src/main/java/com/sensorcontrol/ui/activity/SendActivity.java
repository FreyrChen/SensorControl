package com.sensorcontrol.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.sensorcontrol.R;
import com.sensorcontrol.base.BaseActivity;
import com.sensorcontrol.bean.Pickers;
import com.sensorcontrol.util.BmpUtils;
import com.sensorcontrol.util.ClientUtil;
import com.sensorcontrol.util.ImageHelper;
import com.sensorcontrol.util.SendUtil;
import com.sensorcontrol.util.SocketUtil;
import com.sensorcontrol.view.ClipPictureActivity;
import com.sensorcontrol.view.MyPrDialog;
import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.helper.SocketClientDelegate;
import com.vilyever.socketclient.helper.SocketPacket;
import com.vilyever.socketclient.helper.SocketPacketHelper;
import com.vilyever.socketclient.helper.SocketResponsePacket;
import com.vilyever.socketclient.util.CharsetUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by lizhe on 2017/11/15 0015.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class SendActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {

    private static final int RESULT_LOAD_IMAGE = 99;
    private static final int REQUEST_CLIP_IMAGE = 56;
    @BindView(R.id.iv_sendImg)
    ImageView ivSendImg;
    @BindView(R.id.sb_tone)
    SeekBar sbTone;
    @BindView(R.id.sb_saturation)
    SeekBar sbSaturation;
    @BindView(R.id.sb_brightness)
    SeekBar sbBrightness;
    @BindView(R.id.iv_bmp_original)
    ImageView ivBmpOriginal;
    @BindView(R.id.sb_wv)
    SeekBar sbWv;
    @BindView(R.id.tv_text)
    TextView tvText;

    @BindView(R.id.progress_message)
    TextView progressMessage;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.progress_percent)
    TextView progressPercent;
    @BindView(R.id.progress_number)
    TextView progressNumber;
    @BindView(R.id.ll_progress)
    LinearLayout llProgress;
    @BindView(R.id.relativeLayout)
    RelativeLayout rl;

    private MyPrDialog progressDialog;
    private int jishu = 0;
    private int num;
    private int pLength;
    private SendUtil sendUtil;
    private Bitmap sendBmp;
    String path;
    private List<Pickers> list;
    Pickers pickers;

    byte[] id = new byte[]{0x00, 0x01, 0x02, 0x04, 0x08};
    String[] name = new String[]{"滚动级别1", "滚动级别2", "滚动级别3", "滚动级别4", "滚动级别5"};


    private static int MAX_VALUE = 255;
    private static int MID_VALUE = 127;
    private float mHue, mSaturation, mLum;

    @Override
    protected int setLayout() {
        return R.layout.activity_send;
    }

    @Override
    protected void init() {
        path = getIntent().getStringExtra("path");
        if (!TextUtils.isEmpty(path)) {
            Glide.with(this).load(path).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                    sendBmp = BmpUtils.compressImage(resource);
                    sendBmp = resource;
                    ivSendImg.setImageBitmap(resource);
                    ivBmpOriginal.setImageBitmap(resource);

                    sbTone.setMax(MAX_VALUE);
                    sbSaturation.setMax(MAX_VALUE);
                    sbBrightness.setMax(MAX_VALUE);

                    sbTone.setProgress(MID_VALUE);
                    sbSaturation.setProgress(MID_VALUE);
                    sbBrightness.setProgress(MID_VALUE);
                }
            });
        }

        sbWv.setMax(MAX_VALUE);
        sbWv.setProgress(128);
        tvText.setText(128 + "");
    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        switch (seekBar.getId()) {
            case R.id.sb_tone:
                //色调
                mHue = (progress - MID_VALUE) * 1.0F / MID_VALUE * 180;
                break;
            case R.id.sb_saturation:
                //饱和度
                mSaturation = progress * 1.0F / MID_VALUE;
                break;
            case R.id.sb_brightness:
                //亮度
                mLum = progress * 1.0F / MID_VALUE;
                break;
            case R.id.sb_wv:
                tvText.setText(progress + "");
                break;
        }

        ivSendImg.setImageBitmap(ImageHelper.handleImageEffect(sendBmp, mHue, mSaturation, mLum));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    protected void setData() {
        sbTone.setOnSeekBarChangeListener(this);
        sbSaturation.setOnSeekBarChangeListener(this);
        sbBrightness.setOnSeekBarChangeListener(this);
        sbWv.setOnSeekBarChangeListener(this);
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (flag) {
                switch (msg.what) {
                    case SendUtil.NAK:
                        if (jishu < 3) {
                            progressMessage.setText("正在重发第" + num + "包");
                            sendUtil.send(num);
                            jishu++;
                        } else {
                            progressDialog.cancel();
                            chongzhi();
                            Toast.makeText(SendActivity.this, "重发3次失败", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case SendUtil.ACK:
                        if (num <= pLength) {
                            ++num;
                            progress.setProgress(num);
                            progressPercent.setText(num + "");
                            sendUtil.send(num);
                        }
                        break;
                    case SendUtil.EOT:
//                    progressDialog.cancel();
                        llProgress.setVisibility(View.GONE);
                        rl.setVisibility(View.VISIBLE);
                        SendUtil.closeTimer();
                        chongzhi();
                        Toast.makeText(SendActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                        break;
                    case SendUtil.CAN:
//                    progressDialog.cancel();
                        llProgress.setVisibility(View.GONE);
                        rl.setVisibility(View.VISIBLE);
                        chongzhi();
                        SendUtil.closeTimer();
                        Toast.makeText(SendActivity.this, "发送失败，接收到CAN，强制结束", Toast.LENGTH_SHORT).show();
                        break;
                    case SendUtil.CONN_ERROR:
                        break;
                    case SendUtil.NAK_UNABSORBED:
//                    progressDialog.cancel();
                        llProgress.setVisibility(View.GONE);
                        rl.setVisibility(View.VISIBLE);
                        Toast.makeText(SendActivity.this, "发送失败，未回应", Toast.LENGTH_SHORT).show();
                        break;
                    case SendUtil.EXECUTION_TIMEOUT:
//                    progressDialog.setProgressPercent(num + "");
                        llProgress.setVisibility(View.GONE);
                        rl.setVisibility(View.VISIBLE);
                        chongzhi();
                        SendUtil.closeTimer();
                        Toast.makeText(SendActivity.this, "发送超时", Toast.LENGTH_SHORT).show();
                        break;
                    case SendUtil.THREAD_SERVICE_CLOSE:
//                    progressDialog.cancel();
                        llProgress.setVisibility(View.GONE);
                        rl.setVisibility(View.VISIBLE);
                        chongzhi();
                        Toast.makeText(SendActivity.this, "线程服务关闭", Toast.LENGTH_SHORT).show();
                        break;
                    case SendUtil.THREAD_ERROR:
                        progressDialog.cancel();
                        SendUtil.closeTimer();
                        Toast.makeText(SendActivity.this, "线程错误", Toast.LENGTH_SHORT).show();
                        break;
                    case SendUtil.THREAD_SERVICE_ERROR:
//                    progressDialog.cancel();
                        llProgress.setVisibility(View.GONE);
                        rl.setVisibility(View.VISIBLE);
                        chongzhi();
                        SendUtil.closeTimer();
                        Toast.makeText(SendActivity.this, "线程服务错误", Toast.LENGTH_SHORT).show();
                        break;
                    case SendUtil.IO_ERROR:
//                    progressDialog.cancel();
                        llProgress.setVisibility(View.GONE);
                        rl.setVisibility(View.VISIBLE);
                        chongzhi();
                        SendUtil.closeTimer();
                        Toast.makeText(SendActivity.this, "IO异常", Toast.LENGTH_SHORT).show();
                        break;
                    case SendUtil.DEVICESTATE:
//                    progressDialog.cancel();
                        chongzhi();
                        llProgress.setVisibility(View.GONE);
                        rl.setVisibility(View.VISIBLE);
                        SendUtil.closeTimer();
                        Toast.makeText(SendActivity.this, "发送失败，未回应", Toast.LENGTH_SHORT).show();
                        break;
                    case SendUtil.SEND_START:
                        num = 0;
                        sendUtil.send(num);
                        break;
                }

            }
        }
    };

    private static final int DELIMITER = '\n';

    public void chongzhi(){
        progress.setProgress(0);
        progressPercent.setText(0+"");
        try {
            SocketUtil.closeConn();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        llProgress.setVisibility(View.GONE);
        rl.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.btn_send, R.id.iv_back, R.id.btn_crop, R.id.send_wv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                if (sendBmp != null) {
                    llProgress.setVisibility(View.VISIBLE);
                    rl.setVisibility(View.GONE);
                    sendBmp = ImageHelper.handleImageEffect(sendBmp, mHue, mSaturation, mLum);
                    sendBmp = BmpUtils.compressImage(sendBmp);
                    final byte[] s = BmpUtils.getPicturePixel(sendBmp);
                    int yu = s.length % 1460;
                    if (yu == 0) {
                        pLength = (s.length / 1460) + 1;
                    } else {
                        pLength = (s.length / 1460);
                    }
                    int num = pLength+1;
                    sendUtil = new SendUtil(handler, s);
                    sendUtil.getDeviceState(new byte[]{0x04});
                    progress.setMax(num);
                    progressNumber.setText("总计"+num+"");
                }

                break;
            case R.id.iv_back:
                this.finish();
                break;
            case R.id.btn_crop:
                Intent intent = new Intent(this, ClipPictureActivity.class);
                intent.putExtra("path", path);
                startActivityForResult(intent, REQUEST_CLIP_IMAGE);
                break;
            case R.id.send_wv:
                byte[] data = new byte[2];
                data[0] = 0x02;
                data[1] = (byte) Integer.parseInt(tvText.getText().toString());
                SendUtil sendUtil = new SendUtil(handler, data);
                sendUtil.getDeviceState(data);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CLIP_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                byte[] bis = data.getByteArrayExtra("bitmap");
                Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
                sendBmp = bitmap;
                ivSendImg.setImageBitmap(sendBmp);
                ivBmpOriginal.setImageBitmap(sendBmp);
            }
        }
    }

    private boolean flag = true;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            flag = false;
            SocketUtil.closeConn();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
