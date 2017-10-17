package com.sensorcontrol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by lizhe on 2017/10/16 0016.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class s1 {
    public static void main(String[] s){
        try {
            ServerSocket serverSocket = new ServerSocket(5029);
            Socket socket = serverSocket.accept();
            System.out.println(socket.getInetAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
