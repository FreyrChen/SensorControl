package com.sensorcontrol.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.helper.SocketClientDelegate;
import com.vilyever.socketclient.helper.SocketResponsePacket;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by lizhe on 2017/11/24 0024.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class SendThread extends Thread {

    public static Handler handler;
    private Handler mainHandler;
    private SocketClient clients;
    public static final int head = 0;
    public static final int da = 1;
    public static final int chenggong = 55;
    public static final int rep = 99;
    private int shu = 0;

    public SendThread(SocketClient clients,Handler mainhandler) {
        this.clients = clients;
        this.mainHandler = mainhandler;

    }

    @Override
    public void run() {
        super.run();

        Looper.prepare();
        clients.registerSocketClientDelegate(new SocketClientDelegate() {
            @Override
            public void onConnected(SocketClient client) {
                mainHandler.sendEmptyMessage(chenggong);
            }

            @Override
            public void onDisconnected(SocketClient client) {
                if (shu < 10){
                    client.connect();
                    shu++;
                }else {
                    shu = 0;
                }
            }

            @Override
            public void onResponse(SocketClient client, @NonNull SocketResponsePacket responsePacket) {
                Message msg = new Message();
                msg.what = rep;
                byte b = responsePacket.getData()[0];
                msg.obj = b;
                mainHandler.sendMessage(msg);
            }
        });
        Socket socket = null;
        try {
            socket = new Socket("192.168.1.106",10023);
            socket.setSoTimeout(3000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Socket finalSocket = socket;
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case head:
                        try {
                            SocketUtil.sendPackageData(finalSocket, (byte[]) msg.obj);
                            SocketUtil.receive(finalSocket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        clients.sendData((byte[]) msg.obj);
                        break;
                    case da:
                        clients.sendData((byte[]) msg.obj);
                        break;
                }
            }
        };
        Looper.loop();
    }

    public void sendMsg(int code,byte[] b){
        handler.obtainMessage(code,b).sendToTarget();
    }
}
