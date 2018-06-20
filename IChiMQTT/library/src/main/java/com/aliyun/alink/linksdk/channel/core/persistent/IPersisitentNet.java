package com.aliyun.alink.linksdk.channel.core.persistent;

import com.aliyun.alink.linksdk.channel.core.base.INet;

/**
 * Created by huanyu.zhy on 17/11/8.
 */
public interface IPersisitentNet extends INet {

    /** 获取当前建联状态
     * @return
     */
    PersistentConnectState getConnectState();

    /** 订阅下行消息，订阅后的消息统一在 IOnPushListener 中接收
     * @param topic
     * @param listener
     */
    void subscribe(String topic,IOnSubscribeListener listener);

    /** 取消订阅下行消息
     * @param topic
     * @param listener
     */
    void unSubscribe(String topic,IOnSubscribeListener listener);
}
