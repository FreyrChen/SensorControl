package com.sensorcontrol.module;

/**
 * Created by lizhe on 2017/11/16 0016.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class Ev {

    int code;

    String data;

    public Ev(int code, String data) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
