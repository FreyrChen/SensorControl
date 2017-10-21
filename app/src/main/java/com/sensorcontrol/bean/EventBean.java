package com.sensorcontrol.bean;

import java.io.Serializable;

/**
 * Created by lizhe on 2017/9/26 0026.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class EventBean implements Serializable {

    private String mac;

    public EventBean(String mac) {
        this.mac = mac;
    }

    public String getMac() {
        return mac;
    }


}
