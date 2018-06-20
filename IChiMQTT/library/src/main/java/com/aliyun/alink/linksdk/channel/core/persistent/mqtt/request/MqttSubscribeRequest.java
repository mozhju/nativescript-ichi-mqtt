package com.aliyun.alink.linksdk.channel.core.persistent.mqtt.request;

import com.aliyun.alink.linksdk.channel.core.persistent.PersistentRequest;

/**
 * Created by huanyu.zhy on 17/11/8.
 */
public class MqttSubscribeRequest extends PersistentRequest {

    public String topic;

    /**
     * subScribe or UnSubScribe
     */
    public boolean isSubscribe;
}
