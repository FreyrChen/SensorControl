package com.sensorcontrol.app;

import java.util.UUID;

/**
 * Created by lizhe on 2017/9/21 0021.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class Constants {

    public static volatile boolean isConn = false;

    public static final UUID service = UUID.fromString("0000FFE0-0000-1000-8000-00805f9b34fb");
    public static final UUID Characteristic = UUID.fromString("0000FFE1-0000-1000-8000-00805f9b34fb");
    public static final String MAC = "00:15:83:00:46:43";

    public static final String APPID = "87af64c441d148579474c1c5811fa533";
    public static final String AppSecret = "0d45bd38878e42cfbba31d6605115f48";
    public static final String your_ssid = "4dac426c29fb48f5889c85defd72c949";
    public static final String your_key = "7498a32ccdb84eff81ea84ded2673095";
    public static final String product_secret = "fa3dac009bc04378b5fdd9217f34c16c";
}
