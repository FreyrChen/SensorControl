package com.sensorcontrol.bean;

/**
 * Created by lizhe on 2017/10/11 0011.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class WifiBean {

    String mac;
    String did;
    String productKey;

    public WifiBean(String mac, String did, String productKey) {
        this.mac = mac;
        this.did = did;
        this.productKey = productKey;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }
}
