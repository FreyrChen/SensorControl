package com.sensorcontrol.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lizhe on 2017/11/15 0015.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class SendUtil {
//    传输逻辑 　　
//            1>  收发双方拨号连通后，发送方等待接收方传来 NAK 信号。当第一个 NAK 到达,发送方解释为 开始发送第一个包 　　
//            2>  发送方一旦收到第一个 NAK ，启动了传输，发送方就将数据以每次 128 字节,打包成帧格式传送，再等待接收方的确认信号 　
//            3>  发送方收到接收方传来的 ACK 信号，解释为信息包被正确接收，并有发送下一个包的含义　　
//            4>  发送方收到接收方传来的 NAK 信号，解释为请求重发同一数据包 　　
//            5>  发送方收到接收方传来的 CAN 信号，解释为请求无条件停止传输过程 　　
//            6>  发送方正常传输完全部数据，需要正常结束，发送 EOT 信号通知接收方。接收方用 ACK 进行确认
//            7>  接收方发送 CAN 无条件停止传输过程，发送方收到 CAN 后，不发送 EOT 确认
//            8>  虽然信息包是以 SOH 来标志一个信息包的起始的，但在 SOH 位置上出现的 EOT ,则表示数据传输结束，再也没有数据传过来
//            9>  接收方首先应确认信息包序号的完整性，通过对信息包序号取补，然后和信息包序号的补码异或，结果为 0 表示正确，结果不为 0 则发送 NAK 请求重传
//            10> 接收方确认信息包序号正确后，然后检查是否期望的序号。如果不是期望得到的　　信息包序号，说明发生严重错误，应该发送一个 CAN 来中止传输
//            11> 对于10>情况的唯一例外，是收到的包的信息包序号与前一个信息包序号相同，　　此中情况，接收方简单忽略这个重复的包，向发送方发出 ACK ，准备接收下一个包
//            12> 接收方确认了信息包序号的完整性和是正确期望的后，只对 512 字节的数据区段 　　进行算术和校验，结果与帧中最后一个字节（算术校验和）比较，相同 发送 ACK， 　　不同发送 NAK 　
//
//            .超时处理 　　
//            1> 接收方等待一个信息包的到来所具有的超时时限为 10 秒，每个超时后发送 NAK 　　
//            2> 当收到包时，接收过程中每个字符的超时间隔为 1 秒 　　
//            3> 为保持"接收方驱动"，发送方在等待一个启动字节时不应该采用超时处理
//            4> 一旦传输开始，发送方采用单独的 1 分钟超时时限，给接收方充足的时间做发送ACK ,NAK ,CAN 之前的必须处理　　
//            5> 所有的超时及错误事件至少重试 10 次　　
//            4．控制字符 　　
//               控制字符符合 ASICII 标准定义，长度均为 1 字节 　　SOH   0x01 　　EOT 0x04 　　ACK 0x06 　　NAK 0x15 　　CAN 0x18
//            5. 不足128以^Z表示(CTRL-Z(0x1A))对于标准Xmodem协议来说，如果传送的文件不是128的整数倍，那么最后一个数据包的有效内容肯定小于帧长，不足的部分需要用CTRL-Z(0x1A)来填充。
//

//    下面是我自己写的Xmodem通讯程序，用于验证超级终端的文件传输功能，后面对其扩展用于LCD图片下载：
//          #include <stc89.h>
//          #define uchar unsigned char
//          #define uint unsigned int
//
//          #define XMODEM_SOH 0x01     //这几个都是跟Xmodem协议有关的几个常数,如开始,结束,应答等等
//          #define XMODEM_EOT 0x04
//          #define XMODEM_ACK 0x06
//          #define XMODEM_NAK 0x15
//          #define XMODEM_CAN 0x18
//
//    //连续传输错误10次则PC端超级终端会取消传输
//    uchar PackCnt;                    //每一个数据包的计数值，用于包的拆分
//    uchar PackNum;                    //数据包编号
//    uchar TestVal;                    //每一个包的数据校验码
//    uchar xdata Tab[132];                  //每个数据包为1024字节
/*

数据包格式：
| SOH | 信息包序号 | 信息包序号的补码 | 数据区段 | crc16
说明:
      SOH 帧的开头字节,代表信息包中的第一个字节 　　
      信息包序号:对 256 取模所得到当前包号，第一个信息包的序号为 1(而信息包序号范围 0~255)
      信息包序号的补码： 当前信息包号的补码 　　
      数据区段:数据区段的长度固定为 1024字节，其内容没有任何限制，可以是文本数据或二进制数据　　
      算术校验和:1字节的算术校验和，只对数据区段计算后对 256 取模而得
*/

    public static final byte NAK = 0x15;
    public static final byte EOT = 0x20;
    public static final byte SOH = 0x01;
    public static final byte ACK = 0x06;
    public static final byte CAN = 0x18;
    private static final int len = 1460;
    public static final int SEND_SUCCESS = 90;
    public static final int THREAD_ERROR = 101;
    public static final int THREAD_SERVICE_ERROR = 102;
    public static final int THREAD_SERVICE_CLOSE = 104;
    public static final int EXECUTION_TIMEOUT = 103;
    public static final int IO_ERROR = 105;
    public static final int SEND_ERROR = 91;
    public static final int NAK_UNABSORBED = 92;
    public static final int CONN_ERROR = 93;
    public static final int SEND_START = 201;
    public static final int DEVICESTATE = 202;

    private static final int seizeASeat = 5;
    private static int dataLen = len - seizeASeat;

    private static Handler handler;

    private static byte[] data;
    private byte[] img;
    private static Timer timer;
    private static Timer timer1;
    private final int CHECK_TIME = 12000;
    private static EThread eThread;

    private static int pLength;
    private static int yu;

    public SendUtil(Handler handler, byte[] data) {
        this.handler = handler;
        this.data = data;
    }
    private static BThread thread;

    public void getDeviceState(byte[] data){
        timer1 = new Timer();
        timer1.schedule(new TimerTask(){
            @Override
            public void run() {
                checkThread();
            }
        },CHECK_TIME);
        thread = new BThread(data);
        thread.start();

    }

    public void send(final int num){
        img = chuli(num);

        timer = new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                checkThread();
            }
        },CHECK_TIME);

        eThread = new EThread(img);
        eThread.start();
    }


    public static void closeTimer() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (eThread != null) {
            eThread.stopThread(true);
        }
        if (thread != null){
            thread.stopThread1(true);
        }
        if (timer != null) {
            timer.cancel();// 关闭计时器
        }
        if (timer1 != null){
            timer1.cancel();
        }
    }

    public static byte[] chuli(int i){
        Log.d("asjdhk", i+"");
        byte[] b = subpackage(data,i);
        byte[] head = new byte[3];
        byte[] tail;
        head[0] = SOH;
        head[1] = (byte) i;
        head[2] = (byte) (0xff - i);
        char check = Crc16Utils.Crc16Calc(b,b.length);
        tail = charToByte(check);
        byte[] data = new byte[head.length + b.length];
        System.arraycopy(head, 0, data, 0, head.length);
        System.arraycopy(b, 0, data, head.length, b.length);
        byte[] sendData = new byte[data.length + tail.length];
        System.arraycopy(data, 0, sendData, 0, data.length);
        System.arraycopy(tail, 0, sendData, data.length, tail.length);
        return sendData;
    }


    public static byte[] charToByte(char c) {
        byte[] b = new byte[2];
        b[0] = (byte) ((c & 0xFF00) >> 8);
        b[1] = (byte) (c & 0xFF);
        return b;
    }

    public static byte[] subpackage(byte[] img,int packageNum){
        byte[] resp = new byte[dataLen];
        yu = img.length%dataLen;

        if (yu == 0){
            int position = packageNum * dataLen;
            for (int i = 0; i < dataLen; i++) {
                resp[i] = img[i + position];
            }
        }else {
            pLength = (img.length/dataLen);
            Log.d("pLength",pLength+"");
            int position;
            if (packageNum == pLength){
                position = (packageNum-1) * dataLen;
            }else {
                position = packageNum * dataLen;
            }
            if (packageNum == pLength) {
                resp = new byte[dataLen];
                for (int i = 0; i < yu; i++) {
                    resp[i] = img[i + position];
                }
            }else {
                for (int i = 0; i < dataLen; i++) {
                    resp[i] = img[i + position];
                }
            }
        }
        return resp;
    }

    protected void checkThread() {
        try {
            SocketUtil.closeConn();
        } catch (IOException e) {
            handler.sendEmptyMessage(IO_ERROR);
        }
        handler.sendEmptyMessage(EXECUTION_TIMEOUT);
    }

    static Socket socket;

    class EThread extends Thread {
        private boolean flag = true;
        private byte[] t;

        public EThread(byte[] t) {
            this.t = t;
        }

        public void stopThread(boolean flag) {
            this.flag = !flag;
        }

        @Override
        public void run() {
            try {
                socket = new Socket("13.102.25.195", 8080);
//                socket = new Socket("192.168.1.107", 10023);
                SocketUtil.sendPackageData(socket,t);
                socket.setSoTimeout(5000);//响应阻塞超时
                byte resp = SocketUtil.receive(socket);
                timer.cancel();// 关闭计时器
                if (resp == 0){
                    handler.sendEmptyMessage(NAK_UNABSORBED);
                }
                switch (resp){
                    case ACK:
                        handler.sendEmptyMessage(ACK);
                        break;
                    case NAK:
                        handler.sendEmptyMessage(NAK);
                        break;
                    case EOT:
                        handler.sendEmptyMessage(EOT);
                        break;
                    case CAN:
                        handler.sendEmptyMessage(CAN);
                        break;
                }
            } catch (ConnectTimeoutException e){
                timer.cancel();
                handler.sendEmptyMessage(EXECUTION_TIMEOUT);
            } catch (IOException e) {
                timer.cancel();
                handler.sendEmptyMessage(IO_ERROR);
            }
            if(!flag) {
                return;
            }

        }

    }

    class BThread extends Thread {
        private boolean flag = true;
        private byte[] b;

        public BThread(byte[] b) {
            this.b = b;
        }

        public  void stopThread1(boolean flag) {
            this.flag = !flag;
        }

        @Override
        public void run() {
            try {
                socket = new Socket("13.102.25.195", 8080);
//                socket = new Socket("192.168.1.107", 10023);
                socket.setSoTimeout(5000);//响应阻塞超时
                SocketUtil.sendPackageData(socket,b);
                byte resp = SocketUtil.receive(socket);
                timer1.cancel();// 关闭计时器
                if (resp == 0){
                    handler.sendEmptyMessage(DEVICESTATE);
                }
                switch (resp){
                    case ACK:
                        handler.sendEmptyMessage(ACK);
                        break;
                    case NAK:
                        handler.sendEmptyMessage(SEND_START);
                        break;
                    case EOT:
                        handler.sendEmptyMessage(EOT);
                        break;
                    case CAN:
                        handler.sendEmptyMessage(CAN);
                        break;
                }

            } catch (ConnectTimeoutException e){
                timer1.cancel();
                handler.sendEmptyMessage(EXECUTION_TIMEOUT);
            } catch (IOException e) {
                timer1.cancel();
                handler.sendEmptyMessage(IO_ERROR);
            }
            if(!flag) {
                return;
            }
        }
    }


}
