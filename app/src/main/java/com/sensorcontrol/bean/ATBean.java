package com.sensorcontrol.bean;


import java.io.Serializable;

/**
 * Created by lizhe on 2017/9/23 0023.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class ATBean implements Serializable{


    private String[] AT;
    private int[] num;

    public ATBean(String[] AT, int[] num) {
        this.AT = AT;
        this.num = num;
    }

    public String[] getAT() {
        return AT;
    }

    public void setAT(String[] AT) {
        this.AT = AT;
    }

    public int[] getNum() {
        return num;
    }

    public void setNum(int[] num) {
        this.num = num;
    }
}
