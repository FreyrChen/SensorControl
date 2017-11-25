package com.sensorcontrol.util;

import android.support.annotation.NonNull;
import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.helper.SocketClientDelegate;
import com.vilyever.socketclient.helper.SocketClientSendingDelegate;
import com.vilyever.socketclient.helper.SocketPacket;
import com.vilyever.socketclient.helper.SocketResponsePacket;
import com.vilyever.socketclient.util.CharsetUtil;

/**
 * Created by lizhe on 2017/11/24 0024.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class ClientUtil {

    public static final byte NAK = 0x15;
    public static final byte EOT = 0x20;
    public static final byte SOH = 0x01;
    public static final byte ACK = 0x06;
    public static final byte CAN = 0x18;

    private SocketClient socketClient;
    private int num;
    private NetSendListener netSendListener;



    public ClientUtil() {
        initClient();
        socketClient.registerSocketClientDelegate(new SocketClientDelegate() {
            /**
             * 连接上远程端时的回调
             */
            @Override
            public void onConnected(SocketClient client) {
                netSendListener.onConnected(client);
                client.sendData(new byte[]{0x04});
            }

            /**
             * 与远程端断开连接时的回调
             */
            @Override
            public void onDisconnected(SocketClient client) {
                // 可在此实现自动重连
                if (num < 10){
                    client.connect();
                    num++;
                }else {
                    num = 0;
                    netSendListener.onConnError(reconnection);
                }
            }

            /**
             * 接收到数据包时的回调
             */
            @Override
            public void onResponse(final SocketClient client, @NonNull SocketResponsePacket responsePacket) {
                byte[] data = responsePacket.getData(); // 获取接收的byte数组，不为null
                switch (data[0]){
                    case NAK:
                        netSendListener.sendData(client);
                        break;
                    case EOT:
                        break;
                    case ACK:
                        netSendListener.sendData(client);
                        break;
                    case CAN:
                        break;
                }
            }
        });

        socketClient.registerSocketClientSendingDelegate(new SocketClientSendingDelegate() {
            /**
             * 数据包开始发送时的回调
             */
            @Override
            public void onSendPacketBegin(SocketClient client, SocketPacket packet) {
                netSendListener.sendStart(client,packet);
            }

            /**
             * 数据包取消发送时的回调
             * 取消发送回调有以下情况：
             * 1. 手动cancel仍在排队，还未发送过的packet
             * 2. 断开连接时，正在发送的packet和所有在排队的packet都会被取消
             */
            @Override
            public void onSendPacketCancel(SocketClient client, SocketPacket packet) {
                netSendListener.sendCancel(client,packet);
            }

            /**
             * 数据包发送的进度回调
             * progress值为[0.0f, 1.0f]
             * 通常配合分段发送使用
             * 可用于显示文件等大数据的发送进度
             */
            @Override
            public void onSendingPacketInProgress(SocketClient client, SocketPacket packet, float progress, int sendedLength) {
                netSendListener.sendProgress(client,packet,progress,sendedLength);
            }

            /**
             * 数据包完成发送时的回调
             */
            @Override
            public void onSendPacketEnd(SocketClient client, SocketPacket packet) {
                netSendListener.onSendPacketEnd(client,packet);
            }
        });
    }

    private void initClient() {
        socketClient = new SocketClient();
        socketClient.getAddress().setRemoteIP("192.168.1.106");
        socketClient.getAddress().setRemotePort("10023");
        socketClient.getAddress().setConnectionTimeout(15*1000);// 连接超时时长，单位毫秒
        socketClient.setCharsetName(CharsetUtil.UTF_8); // 设置编码为UTF-8
        socketClient.getSocketPacketHelper().setReceivePacketLengthDataLength(1);
        socketClient.getSocketPacketHelper().setSendTrailerData(new byte[]{'\n'});

    }


    public void connect(){
        socketClient.connect();
    }

    public void sendSms(byte[] b){
        socketClient.sendData(b);
    }

    public void setNetSendListener(NetSendListener netSendListener) {
        this.netSendListener = netSendListener;
    }

    public interface NetSendListener{
        void onConnected(SocketClient client);

        void onConnError(int code);

        void sendCancel(SocketClient client, SocketPacket packet);

        void sendProgress(SocketClient client, SocketPacket packet, float progress, int sendedLength);

        void sendStart(SocketClient client, SocketPacket packet);

        void sendData(SocketClient client);

        void onSendPacketEnd(SocketClient client, SocketPacket packet);
    }

    int reconnection;


}
