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
}
