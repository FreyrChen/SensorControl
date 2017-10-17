package com.sensorcontrol;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by lizhe on 2017/10/16 0016.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class s {

    public static void main(String[] s){
        try {
            Socket serverSocket = new Socket("192.168.1.102",5029);
            System.out.println("个护短");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
