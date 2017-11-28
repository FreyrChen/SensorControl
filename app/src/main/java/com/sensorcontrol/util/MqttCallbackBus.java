package com.sensorcontrol.util;

import com.lichfaker.log.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by lizhe on 2017/11/28 0028.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class MqttCallbackBus implements MqttCallback {

    @Override
    public void connectionLost(Throwable cause) {
        Logger.e(cause.getMessage());
    }
    @Override
    public void messageArrived(String topic, MqttMessage message) {
        Logger.d(topic + "====" + message.toString());
        EventBus.getDefault().post(message);
    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

}
