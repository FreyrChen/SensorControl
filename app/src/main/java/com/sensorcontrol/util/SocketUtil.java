package com.sensorcontrol.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class SocketUtil {

    public static final String IP_ADDR = "13.102.25.195";//服务器地址
    public static final int PORT = 8080;//服务器端口号
    public static String replyInfo = null;
    private static Socket socket = null;


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
    private static BufferedOutputStream bos = null;
    private static BufferedInputStream br = null;
    private static InputStream is = null;

    public static byte receive(Socket socket) throws IOException {
        is = socket.getInputStream();
        br = new BufferedInputStream(is);
        int r = -1;
        byte[] b = new byte[1];
        List<Byte> l = new LinkedList<Byte>();
        while (l.size() <= 0){
            byte num = Byte.valueOf((byte)r);
            l.add(num);
        }
        b[0] = l.get(0);

        return b[0];
    }

    public static void closeConn() throws IOException{
        if (os != null){
            os.close();
        }
        if (bos != null){
            bos.close();
        }
        if (br != null) {
            br.close();
        }
        if (is != null){
            is.close();
        }

        if (socket != null){
            socket.close();
        }
    }

    public static void sendPackageData(Socket socket,byte[] data) throws IOException {
        os = socket.getOutputStream();
        bos = new BufferedOutputStream(os);
        bos.write(data);
        bos.flush();
        socket.shutdownOutput();
    }

}
