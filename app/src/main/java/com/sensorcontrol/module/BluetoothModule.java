package com.sensorcontrol.module;

import android.util.Log;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import java.util.UUID;
import static com.inuker.bluetooth.library.Code.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED;

/**
 * Created by lizhe on 2017/9/26 0026.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 * 蓝牙的具体操作在此类中实现
 */

public class BluetoothModule {
    private static BluetoothModule module;
    private BluetoothModuleData bluetoothModuleData;
    private NotifyData notifyData1;
    private final BluetoothClient mClient = ClientManager.getClient();
    private static final BleConnectOptions options = new BleConnectOptions.Builder()
            .setConnectRetry(3)   // 连接如果失败重试3次
            .setConnectTimeout(30000)   // 连接超时30s
            .setServiceDiscoverRetry(3)  // 发现服务如果失败重试3次
            .setServiceDiscoverTimeout(20000)  // 发现服务超时20s
            .build();

    private static final SearchRequest request = new SearchRequest.Builder()
            .searchBluetoothLeDevice(6000, 1)   // 先扫BLE设备3次，每次3s
            .build();

    private BleGattProfile data = null;

    private BluetoothModule() {
    }

    public void setNotifyData(NotifyData notifyData) {
        this.notifyData1 = notifyData;
    }

    public static BluetoothModule getBluetoothModule(){
        if (module == null){
            return module = new BluetoothModule();
        }
        return module;
    }
    private final BleConnectStatusListener mBleConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            if (status == STATUS_CONNECTED) {

            } else if (status == STATUS_DISCONNECTED) {
                connect(mac);
            }
        }
    };

    public void search(){
        if (mClient.isBluetoothOpened()){
            mClient.search(request, new SearchResponse() {
                @Override
                public void onSearchStarted() {
                    bluetoothModuleData.state(SEARCH_STARTED);
                }

                @Override
                public void onDeviceFounded(SearchResult device) {
                    bluetoothModuleData.device(device);
                }

                @Override
                public void onSearchStopped() {
                    bluetoothModuleData.state(SEARCH_STOPPED);
                }

                @Override
                public void onSearchCanceled() {
                    bluetoothModuleData.state(SEARCH_CANCELED);
                }
            });
        }else {
            mClient.openBluetooth();
        }
    }

    public void stopSearch(){
        mClient.stopSearch();
    }

    public void connect(final String mac){
        mClient.connect(mac, options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                if (code == REQUEST_SUCCESS) {
                    mClient.registerConnectStatusListener(mac, mBleConnectStatusListener);
                    bluetoothModuleData.bleGattProfile(data);
                }else {
                    bluetoothModuleData.state(CONN_ERROR);
                }

            }
        });
    }

    public void read(String mac, UUID service,UUID character){
        mClient.read(mac, service, character, new BleReadResponse() {
            @Override
            public void onResponse(int code, byte[] data) {
                if (code == REQUEST_SUCCESS) {
                    bluetoothModuleData.readData(new String(data));
                }else {
                    bluetoothModuleData.state(READ_ERROR);
                }
            }
        });
    }

    //byte[]不能超过20字节
    public void write(String mac, UUID service,UUID character,String s){
        if (s.getBytes().length > 2)
        mClient.write(mac, service, character, s.getBytes(), new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    bluetoothModuleData.state(WRITE_SUCCESS);
                }else {
                    bluetoothModuleData.state(WRITE_ERROR);
                }
            }
        });
    }

    public void readDescriptor(){

    }

    public void writeDescriptor(){

    }

    public void openNotify(String mac, UUID service,UUID character){
        mClient.notify(mac, service, character, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
                notifyData1.notifyData(new String(value));
            }

            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    Log.d("REQUEST_SUCCESS","Notify通知打开成功");
                }else {
                    bluetoothModuleData.state(NOTITY_ERROR);
                }
            }
        });
    }

    public void unnotify(String mac, UUID service,UUID character){
        mClient.unnotify(mac, service, character, new BleUnnotifyResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    Log.d("REQUEST_SUCCESS","Notify通知关闭成功");
                }else {
                    bluetoothModuleData.state(UNNOTITY_ERROR);
                }
            }
        });
    }

    public void openIndicate(){

    }

    public void unindicate(){

    }

    public void readRssi(){

    }

    public void disconnect(String mac){
        mClient.unregisterConnectStatusListener(mac, mBleConnectStatusListener);
        mClient.disconnect(mac);
    }

    public interface BluetoothModuleData{
        void device(SearchResult device);

        void state(int state);

        void bleGattProfile(BleGattProfile data);

        void readData(String data);
    }

    public interface NotifyData{
        void notifyData(String data);
    }

    public void setBluetoothModuleData(BluetoothModuleData bluetoothModuleData) {
        this.bluetoothModuleData = bluetoothModuleData;
    }

    public static final int CONN_ERROR = 1;
    public static final int READ_ERROR = 5;

    public static final int WRITE_ERROR = 6;
    public static final int WRITE_SUCCESS = 7;
    public static final int NOTITY_ERROR = 8;
    public static final int UNNOTITY_ERROR = 10;

    public static final int SEARCH_STOPPED = 2;
    public static final int SEARCH_CANCELED = 3;
    public static final int SEARCH_STARTED = 4;
    public final static int bleGattProfile = 99;
    public final static int readData = 100;
    public final static int notifyData = 101;
    public final static int DEVICE = 102;
}
