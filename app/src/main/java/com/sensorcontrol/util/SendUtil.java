//package com.sensorcontrol.util;
//
//import android.bluetooth.BluetoothGatt;
//import android.bluetooth.BluetoothGattCharacteristic;
//import android.bluetooth.BluetoothGattService;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.Message;
//import android.os.SystemClock;
//import android.text.TextUtils;
//import android.util.Log;
//
//import java.util.Arrays;
//import java.util.UUID;
//
///**
// * Created by lizhe on 2017/10/13 0013.
// * 目标定在月亮之上，即使失败，也可以落在众星之间。
// */
//
//public class SendUtil{
//
//    /**
//     * 发送消息Handler，线程中处理发送，避免阻塞主线程
//     *
//     */
//    private Handler mMessageHandler = null;
//    private BluetoothGatt mCurrentBluetoothGatt = null;
//
//    public void subPackageOnce(){
//
//    }
//
//
//    /**
//     * 初始化MessageHandler
//     * */
//    private void setupMessageHandler() {
//        if(mMessageHandler == null) {
//            if(mMessageThread == null) {
//                mMessageThread = new HandlerThread("thread-send-message");
//            }
//            mMessageThread.start();
//            mMessageHandler = new Handler(mMessageThread.getLooper()) {
//                @Override
//                public void handleMessage(Message message) {
//                    if(mCurrentBluetoothGatt == null) {
//                        return;
//                    }
//
//                    String msg = (String) message.obj;
//
//                    if(!TextUtils.isEmpty(msg)) {
//                        logd("send message : " + msg);
//                        byte[][] packets = BLEDataUtil.encode(msg);
//                        int tryCount  = 3;//最多重发次数
//                        boolean sendSuccess = true;
//                        while(--tryCount > 0) {
//                            sendSuccess = true;
//                            for(int i = 0; i < packets.length; i++) {
//                                byte[] bytes = packets[i];
//                                int onceTryCount = 3;//单次重发次数
//                                boolean onceSendSuccess = true;
//                                BluetoothGattService service = mCurrentBluetoothGatt.getService(UUID.fromString(sServiceUUID));
//                                if(service != null) {
//                                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(sCharacteristicUUID));
//                                    characteristic.setValue(bytes);
//                                    while(--onceTryCount > 0) {
//
//                                        if(mIsShutdown) {
//                                            break;
//                                        }
//
//                                        if(mCurrentBluetoothGatt.writeCharacteristic(characteristic)){
//                                            logd("Write Success, DATA: " + Arrays.toString(characteristic.getValue()));
//                                            onceSendSuccess = true;
//                                            //避免数据发送太快丢失，需要分包延迟发送
//                                            SystemClock.sleep(200);
//                                            break;
//                                        } else {
//                                            logd("Write failed, DATA: " + Arrays.toString(characteristic.getValue()) + ", and left times = " + onceTryCount);
//                                            onceSendSuccess = false;
//                                            //避免数据发送太快丢失，需要分包延迟发送
//                                            SystemClock.sleep(400);//失败的时候，把时间调大
//                                        }
//
//                                    }
//
//                                }
//
//                                if(!onceSendSuccess) {//一次发送，重试三次都未成功则，跳出重发这个数据
//                                    sendSuccess = false;
//                                    break;
//                                }
//
//                                //避免数据发送太快丢失，需要分包延迟发送
//                                SystemClock.sleep(200);
//                            }
//
//                            if(sendSuccess) {
//                                break;
//                            } else {
//                                logd("send msg failed, and try times = " + tryCount);
//                            }
//
//                            if(mIsShutdown) {
//                                logd("give up for disconnected by user");
//                                break;
//                            }
//
//                            //避免数据发送太快丢失，需要分包延迟发送
//                            SystemClock.sleep(200);
//                        }
//
//                        if(!sendSuccess && !mIsShutdown) {
//                            logd("send failed : " + msg);
//                        }
//
//                    }
//                }
//            };
//        }
//    }
//
//    /**
//     * 日志显示开关，默认开启
//     * */
//    private static boolean DEBUG_ENABLE = true;
//    private static final String TAG = "BLEManager";
//    /**
//     * 是否已经关闭
//     * */
//    private volatile boolean mIsShutdown = false;
//
//    /**
//     * 日志
//     * @param message
//     * */
//    private static void logd(String message) {
//        if(DEBUG_ENABLE) {
//            Log.d(TAG, message);
//        }
//    }
//}
