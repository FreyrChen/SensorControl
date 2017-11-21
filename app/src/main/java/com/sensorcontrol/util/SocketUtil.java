package com.sensorcontrol.util;


import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SocketUtil {

    public static final String IP_ADDR = "13.102.25.195";//服务器地址
    public static final int PORT = 8080;//服务器端口号
    public static String replyInfo = null;

    private static Socket socket = null;

    public static Socket getSocket() {
        openConn();
        return socket;
    }

    public static Socket openConn(){
        new Thread(){
            @Override
            public void run() {
                try {
                    socket = new Socket();
                    socket.setReuseAddress(true);
                    SocketAddress sa = new InetSocketAddress(IP_ADDR,PORT);
                    socket.bind(sa);
                    socket.connect(sa, 2000);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (ConnectException e){
                    e.printStackTrace();
                } catch (SocketException e){
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        new Handler().postAtTime(new Runnable() {
            @Override
            public void run() {

            }
        },3000);
        return socket;
    }


    public static boolean close(Socket socket){
        if (socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (socket.isClosed()){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    private static OutputStream os = null;

    public static byte receive(Socket socket) throws IOException {
        InputStream is = null;
        BufferedReader br = null;
        byte[] b = new byte[1];
        while (true) {
            is = socket.getInputStream();
                is.read(b);
                if (b[0] != 0){
                    break;
                }
            }
        if (br != null) {
            br.close();
        }
        if (is != null){
            is.close();
        }
        if (os != null){
            os.close();
        }
        if (socket != null){
            socket.close();
        }
        return b[0];
    }

    public static void sendPackageData(Socket socket,byte[] data) throws IOException {
        os = socket.getOutputStream();
        os.write(data);
        os.flush();

    }




}
