package com.sensorcontrol.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lizhe on 2017/9/29 0029.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class ListBean implements Serializable{

    private List list;

    public ListBean(List list) {
        this.list = list;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }
}
