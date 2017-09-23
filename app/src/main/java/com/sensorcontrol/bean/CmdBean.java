package com.sensorcontrol.bean;

import java.io.Serializable;

/**
 * Created by lizhe on 2017/9/21 0021.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class CmdBean implements Serializable{

    private String AT;

    private String name;

    private int time;

    public CmdBean(String AT, String name,int time) {
        this.AT = AT;
        this.name = name;
        this.time = time;
    }

    public CmdBean() {
    }

    public String getAT() {
        return AT;
    }

    public void setAT(String AT) {
        this.AT = AT;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
