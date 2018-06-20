package com.aliyun.alink.linksdk.channel.core.persistent.mqtt.request;

import com.aliyun.alink.linksdk.channel.core.persistent.PersistentRequest;

/**
 * Created by huanyu.zhy on 17/11/8.
 */
public class MqttPublishRequest extends PersistentRequest {
    /**
     * 请求topic
     */
    public String topic;
    /**
     * 是否是RPC请求，如果是，则需要等待 replyTopic 消息后才Rsp。 默认为 false
     */
    public boolean isRPC = false;
    /**
     * RPC请求中，指定replyTopic，若不指定，则为 topic+"_reply"
     */
    public String replyTopic;

    /**
     * RPC请求中，RPC请求的msgId，对应payload中id字段
     */
    public String msgId = "";
}
