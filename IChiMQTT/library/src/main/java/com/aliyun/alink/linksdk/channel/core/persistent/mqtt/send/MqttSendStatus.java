package com.aliyun.alink.linksdk.channel.core.persistent.mqtt.send;

import com.aliyun.alink.linksdk.channel.core.base.ISendStatus;

/**
 * Created by huanyu.zhy on 17/11/9.
 */
public enum MqttSendStatus implements ISendStatus{
    waitingToSend,

    /**
     * For RPC Publish
     */
    waitingToSubReply,
    subReplyed,
    waitingToPublish,
    published,


    waitingToComplete,
    completed,
}
