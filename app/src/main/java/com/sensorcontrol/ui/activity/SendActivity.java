package com.sensorcontrol.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.sensorcontrol.R;
import com.sensorcontrol.base.BaseActivity;
import com.sensorcontrol.util.BmpUtils;
import com.sensorcontrol.util.ImageHelper;
import com.sensorcontrol.util.SendUtil;
import com.sensorcontrol.view.ClipPictureActivity;

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

    private ProgressDialog progressDialog;
    private int jishu = 0;
    private int num;
    private int pLength;
    private SendUtil sendUtil;
    private Bitmap sendBmp;
    String path;

    private static int MAX_VALUE = 255;
    private static int MID_VALUE = 127;
    private float mHue, mSaturation, mLum;

    @Override
    protected int setLayout() {
        return R.layout.activity_send;
    }

    @Override
    protected void init() {
        initProgress();
        path = getIntent().getStringExtra("path");
        if (!TextUtils.isEmpty(path)){
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

    }

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在发送");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
    }



    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SendUtil.NAK:
                    if (jishu < 3){
                        sendUtil.send(num);
                        jishu++;
                    }else {
                        progressDialog.cancel();
                        Toast.makeText(SendActivity.this, "重发3次失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case SendUtil.ACK:
                    if (num <= pLength) {
                        ++num;
                        sendUtil.send(num);
                    }
                    break;
                case SendUtil.EOT:
                    progressDialog.cancel();
                    Toast.makeText(SendActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                    break;
                case SendUtil.CAN:
                    progressDialog.cancel();
                    Toast.makeText(SendActivity.this, "发送失败，接收到CAN，强制结束", Toast.LENGTH_SHORT).show();
                    break;
                case SendUtil.CONN_ERROR:
                    break;
                case SendUtil.NAK_UNABSORBED:
                    progressDialog.cancel();
                    Toast.makeText(SendActivity.this, "发送失败，未回应", Toast.LENGTH_SHORT).show();
                    break;
                case SendUtil.EXECUTION_TIMEOUT:
                    progressDialog.cancel();
                    SendUtil.closeTimer();
                    Toast.makeText(SendActivity.this, "发送超时", Toast.LENGTH_SHORT).show();
                    break;
                case SendUtil.THREAD_SERVICE_CLOSE:
                    progressDialog.cancel();
                    Toast.makeText(SendActivity.this, "线程服务关闭", Toast.LENGTH_SHORT).show();
                    break;
                case SendUtil.THREAD_ERROR:
                    progressDialog.cancel();
                    Toast.makeText(SendActivity.this, "线程错误", Toast.LENGTH_SHORT).show();
                    break;
                case SendUtil.THREAD_SERVICE_ERROR:
                    progressDialog.cancel();
                    Toast.makeText(SendActivity.this, "线程服务错误", Toast.LENGTH_SHORT).show();
                    break;
                case SendUtil.IO_ERROR:
                    progressDialog.cancel();
                    Toast.makeText(SendActivity.this, "IO异常", Toast.LENGTH_SHORT).show();
                    break;
                case SendUtil.DEVICESTATE:
                    progressDialog.cancel();
                    Toast.makeText(SendActivity.this, "发送失败，未回应", Toast.LENGTH_SHORT).show();
                    break;
                case SendUtil.SEND_START:
                    num = 0;
                    sendUtil.send(num);
                    break;
            }
        }
    };




    @OnClick({R.id.btn_send,R.id.iv_back,R.id.btn_crop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                if (sendBmp != null) {
                    progressDialog.show();
                    sendBmp = BmpUtils.compressImage(sendBmp);
                    final byte[] s = BmpUtils.getPicturePixel(sendBmp);
                    int yu = s.length % 1460;
                    if (yu == 0) {
                        pLength = (s.length / 1460) + 1;
                    } else {
                        pLength = (s.length / 1460);
                    }

                    sendUtil = new SendUtil(handler, s);
                    sendUtil.getDeviceState();
//                    SocketUtil.sendData("13.102.25.195",8080,s);
//                    uploadImgOne(imgPath,device.getDid());
//                    progressDialog.show();
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

    /**
     * 获取本地图片并指定高度和宽度
     */
    public Bitmap getNativeImage(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.outWidth = 376;
        options.outHeight = 160;
        // 获取这个图片的宽和高
        Bitmap myBitmap = BitmapFactory.decodeFile(imagePath, options); //此时返回myBitmap为空
        //计算缩放比
        int be = (int) (options.outHeight / (float) 200);
        int ys = options.outHeight % 200;//求余数
        float fe = ys / (float) 200;
        if (fe >= 0.5)
            be = be + 1;
        if (be <= 0)
            be = 1;
        options.inSampleSize = be;
        //重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false
        options.inJustDecodeBounds = false;
        myBitmap = BitmapFactory.decodeFile(imagePath, options);
        return myBitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
