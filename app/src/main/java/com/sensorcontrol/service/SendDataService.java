package com.sensorcontrol.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by lizhe on 2017/11/25 0025.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class SendDataService extends Service {

    private static final String SERVER_IP = "192.168.0.106";
    private static final int SERVER_PORT = 333;
    private Socket mSocket;
    private BufferedInputStream bufferedInputStream;
    private BufferedOutputStream bufferedOutputStream;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SendDataService: ","服务创建");
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = new Socket(SERVER_IP,SERVER_PORT);
                    Log.d("conn","创建套字成功");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Subscribe
    public void onPostMsgEvent(byte[] event) {
        if (event!=null) {
            //向socket主机提交数据
            byte[] postMsgStr = event;
            try {
                bufferedOutputStream.write(postMsgStr);
                bufferedOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            bufferedInputStream.close();
            bufferedOutputStream.close();
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("error:","Socket结束故障");
        }
    }
}
