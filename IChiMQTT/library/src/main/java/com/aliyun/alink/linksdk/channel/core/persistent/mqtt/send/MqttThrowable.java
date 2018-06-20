package com.aliyun.alink.linksdk.channel.core.persistent.mqtt.send;

/**
 * Created by huanyu.zhy on 17/11/8.
 */
public class MqttThrowable extends Throwable{
    public String message = null;

    public MqttThrowable(String msg) {
        this.message = msg;
    }
}
