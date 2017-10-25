package com.sensorcontrol.controller;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchResult;
import com.sensorcontrol.app.Constants;
import com.sensorcontrol.module.BluetoothModule;
import java.util.UUID;


/**
 * Created by lizhe on 2017/9/26 0026.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class BluetoothController implements BluetoothModule.BluetoothModuleData {

    private static BluetoothModule bluetoothModule;
    private static Handler mHandler;
    private Activity mActivity;
    private UUID service;
    private UUID characteristic;

    private BluetoothController(Builder builder){
        bluetoothModule = builder.mBluetoothModule;
        mHandler = builder.mHandler;
        mActivity = builder.mActivity;
        service = builder.service;
        characteristic = builder.characteristic;
        bluetoothModule.setBluetoothModuleData(this);
    }

    private BluetoothController(){}

    public void conn(String mac){
        bluetoothModule.connect(mac);
    }

    public void search(){
        bluetoothModule.search();
    }
    public void stopSearch(){
        bluetoothModule.stopSearch();
    }

    public void unconn(String mac){
        bluetoothModule.disconnect(mac);
    }

    public void read(String mac){
        bluetoothModule.read(mac,service,characteristic);
    }

    public void write(String mac,String msg){
        bluetoothModule.write(mac,Constants.service,characteristic,msg);
    }

    public void write(String mac,byte[] msg){
        bluetoothModule.write(mac,Constants.service,characteristic,msg);
    }

    public void openNotify(String mac){
        bluetoothModule.openNotify(mac,service,characteristic);
    }

    public void unNotify(String mac){
        bluetoothModule.unnotify(mac,service,characteristic);
    }

    public void disconnect(String mac){
        bluetoothModule.disconnect(mac);
    }

    @Override
    public synchronized void state(int state) {
        Message msg = new Message();
        switch (state){
            case BluetoothModule.SEARCH_STARTED:
                msg.what = BluetoothModule.SEARCH_STARTED;
                mHandler.sendMessage(msg);
                break;
            case BluetoothModule.SEARCH_STOPPED:
                msg.what = BluetoothModule.SEARCH_STOPPED;
                mHandler.sendMessage(msg);
                break;
            case BluetoothModule.SEARCH_CANCELED:
                msg.what = BluetoothModule.SEARCH_CANCELED;
                mHandler.sendMessage(msg);
                break;
            case BluetoothModule.CONN_ERROR:
                msg.what = BluetoothModule.CONN_ERROR;
                mHandler.sendMessage(msg);
                break;
            case BluetoothModule.NOTITY_ERROR:
                msg.what = BluetoothModule.NOTITY_ERROR;
                mHandler.sendMessage(msg);
                break;
            case BluetoothModule.WRITE_SUCCESS:
                writeSuccessListener.writeSuccess(state);
                break;
            case BluetoothModule.READ_ERROR:
                writeSuccessListener.writeSuccess(state);
                break;
            case BluetoothModule.UNNOTITY_ERROR:
                msg.what = BluetoothModule.UNNOTITY_ERROR;
                mHandler.sendMessage(msg);
                break;
            case BluetoothModule.WRITE_ERROR:
                msg.what = BluetoothModule.WRITE_ERROR;
                mHandler.sendMessage(msg);
                break;
        }
    }


    @Override
    public void device(SearchResult device) {
        Message msg = new Message();
        msg.what = BluetoothModule.DEVICE;
        msg.obj = device;
        mHandler.sendMessage(msg);
    }

    @Override
    public void bleGattProfile(BleGattProfile data) {
        Message msg = new Message();
        msg.what = BluetoothModule.bleGattProfile;
        msg.obj = data;
        mHandler.sendMessage(msg);
    }

    @Override
    public void readData(String data) {
        Message msg = new Message();
        msg.what = BluetoothModule.readData;
        msg.obj = data;
        mHandler.sendMessage(msg);
    }

    private WriteSuccessListener writeSuccessListener;

    public void setWriteSuccessListener(WriteSuccessListener writeSuccessListener) {
        this.writeSuccessListener = writeSuccessListener;
    }

    public interface WriteSuccessListener{
        void writeSuccess(int state);
    }

    public static class Builder{
        private BluetoothModule mBluetoothModule;
        private Handler mHandler;
        private Activity mActivity;
        private UUID service = UUID.fromString("0000FFE0-0000-1000-8000-00805f9b34fb");
        private UUID characteristic = UUID.fromString("0000FFE1-0000-1000-8000-00805f9b34fb");

        public Builder() {

        }

        public Builder setmBluetoothModule(BluetoothModule mBluetoothModule) {
            this.mBluetoothModule = mBluetoothModule;
            return this;
        }

        public Builder setmHandler(Handler mHandler) {
            this.mHandler = mHandler;
            return this;
        }

        public Builder setmActivity(Activity mActivity) {
            this.mActivity = mActivity;
            return this;
        }

        public Builder setService(UUID service) {
            this.service = service;
            return this;
        }

        public Builder setCharacteristic(UUID characteristic) {
            this.characteristic = characteristic;
            return this;
        }

        public BluetoothController build(){
            return new BluetoothController(this);
        }
    }
}
