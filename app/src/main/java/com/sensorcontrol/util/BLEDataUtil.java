package com.sensorcontrol.util;

import android.os.Handler;
import android.os.Message;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author lizhe
 * @date 2017-10-14
 * 蓝牙数据打包
 * 分包协议为
 * 每个包为20字节
 * 1包， 数据头 + n(总包数)
 * 2包， 数据头 + 1 + data
 * 3包， 数据头 + 2 + data
 * 4包， 数据头 + 3 + data
 * .....
 * n+1包，数据头 + n + data
 */

public class BLEDataUtil {

    /**数据头*/
    private static final byte[] START_BYTE = intToButeArray((short)0xffff);
    /**数据头占位*/
    private static final short HEAD_NUM = (short) START_BYTE.length;
    /**占位*/
    private static final int INT_NUM = 5;
    /**累加和*/
    private static int summation;
    /**蓝牙最大发送20个字节，但是首个字节由标志位占用4字节,累积和占1字节*/
    private static final short MAX_SIZE = 20-INT_NUM;
    public static final int BLE_DATA = 986;
    /**总包数*/
    private static int num;
    /**最后一个包的余数*/
    private static int yu;
    /**发送包*/
    private static byte[][] sendByte;

    /**
     * handler转发回所在类
     * @param b
     * @param handler
     * @param time
     */
    public static void handleByte(byte[] b, final Handler handler, int time){
        if (b.length > 20){
            num = (b.length / MAX_SIZE);
            yu = b.length % MAX_SIZE;
        }
//        sendByte = splitPackage(num,yu,b);
        for (int i = 0; i < sendByte.length; i++) {
            final int index = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = BLE_DATA;
                    msg.obj = sendByte[index];
                    handler.sendMessage(msg);
                }
            }, time);
        }
    }

    /**
     * 分包方法
     * @param m
     * @param yu
     * @param b
     * @return
     */
    private static byte[][] splitPackage(short m,short yu,byte[] b) {
        m = (short) (m + 2);
        byte[][] sendData = new byte[m][];
        byte[] frist = tihuan(intToButeArray(m),START_BYTE,HEAD_NUM,false);
        System.out.println("frist______" + frist.length);
        for (int i = 0; i < m; i++) {
            if (i == m-1){
                sendData[i] = new byte[yu + INT_NUM];
            }else if (i == 0){
                sendData[i] = new byte[10];
            }else {
                sendData[i] = new byte[20];
            }
        }
        //添加头部
        sendData[0] = frist;
        for (int i = 1; i < m; i++) {
            byte[] b1 = intToButeArray(i);
            for (int j = 0; j < sendData[i].length; j++) {
                if (j > 3 && j != sendData[i].length-1){
                    sendData[i][j] = b[j - 4 + (MAX_SIZE * (i-1))];
                }else if (j == sendData[i].length-1){
                    sendData[i][j] = summation(sendData[i]);
                }else {
                    sendData[i][j] = b1[j];
                }
            }
            System.out.print(sendData[i].length);
        }
        for (int i = 0; i < sendData.length ; i++) {
            System.out.println("sendData[i].length_________"+sendData[i].length);
        }
        System.out.println("sendData.length_________"+sendData.length);
        return sendData;
    }

    public static byte[][] splitPackage1(int m,int yu,byte[] b,int num) {
        byte[][] sendData;
        if (yu == 0) {
            sendData = new byte[m][20];
        }else {
            m = m + 1;
            sendData = new byte[m][];
            for (int i = 0; i < m; i++) {
                if (i == m - 1){
                    sendData[i] = new byte[yu];
                } else {
                    sendData[i] = new byte[num];
                }
            }
        }



        for (int i = 0; i < m; i++) {
            for (int j = 0; j < sendData[i].length; j++) {
                sendData[i][j] = b[j + (num * i)];
            }
        }

        return sendData;
    }


    private static byte summation(byte[] bytes){
        byte b = 0;
        for (int i = 0; i < bytes.length; i++) {
            b += bytes[i];
        }

        return (byte) (b%256);
    }

    public static void main(String[] a){

    }

    public static int byteArrayToInt(byte[] byteArray) {
        int n = 0;
        try {
            ByteArrayInputStream byteInput = new ByteArrayInputStream(byteArray);
            DataInputStream dataInput = new DataInputStream(byteInput);
            n = dataInput.readInt();
            System.out.println("整数为： " + n);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return n;
    }

    /**
     * int 转 byte[]占 4 字节
     * @param n
     * @return
     */
    public static byte[] intToButeArray(int n) {
        byte[] byteArray = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(byteOut);
            dataOut.writeInt(n);
            byteArray = byteOut.toByteArray();
            for (byte b : byteArray) {
                System.out.println(" " + b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    /**
     * byte[] 拼接
     * @param newArray
     * @param allArray
     * @param startPos
     * @param override
     * @return
     */
    private static byte[] tihuan(byte[] newArray, byte[] allArray,int startPos,boolean override){

        if(newArray==null||allArray==null){
            return null;
        }

        int nlen = newArray.length;
        int alen = allArray.length;
        int spos = startPos;

        if(spos>alen){//开始位置大于原来的数据长度
            spos = alen;
        }else if(spos<0){
            spos =0;
        }
        int rlen = 0;
        byte resultArray[] =null;
        if(nlen+spos<=alen){
            //如果开始位置，到新数据长度，小于或等于总数据长度的话，
            //新的数组的长度为总数据长度

            if (override) {
                //覆盖则长度为新的数据长度
                rlen = spos+nlen;
                resultArray  = new byte[rlen];
                System.arraycopy(allArray,0,resultArray,0,spos );//之前
                System.arraycopy(newArray, 0, resultArray, spos, nlen);//新数据

            }else{
                rlen = alen;//不覆盖则为原有长度
                resultArray  = new byte[rlen];
                System.arraycopy(allArray,0,resultArray,0,spos );//之前
                System.arraycopy(newArray, 0, resultArray, spos, nlen); //新数据
                System.arraycopy(allArray, spos+nlen, resultArray, spos+nlen, alen-spos-nlen);//之后

            }
        }else{
            //
            //如果开始位置加上新数据长度，大于总数据长度
            //新数据数组的长度应该为， 大出来的那个数据的长度+原来总的数据长度。
            rlen = spos+nlen;
            resultArray = new byte[rlen];
            System.arraycopy(allArray,0,resultArray,0,spos );//之前的数据
            System.arraycopy(newArray, 0, resultArray, spos, nlen); // 新数据
        }
        return resultArray;
    }

}
