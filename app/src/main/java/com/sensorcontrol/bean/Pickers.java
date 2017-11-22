package com.sensorcontrol.bean;

/**
 * Created by lizhe on 2017/11/21 0021.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class Pickers {

    private static final long serialVersionUID = 1L;

    private String showConetnt;
    private byte showId;

    public String getShowConetnt() {
        return showConetnt;
    }

    public byte getShowId() {
        return showId;
    }

    public Pickers(String showConetnt, byte showId) {
        super();
        this.showConetnt = showConetnt;
        this.showId = showId;
    }

    public Pickers() {
        super();
    }

}
