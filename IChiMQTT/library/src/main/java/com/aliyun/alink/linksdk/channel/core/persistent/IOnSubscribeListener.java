package com.aliyun.alink.linksdk.channel.core.persistent;

import com.aliyun.alink.linksdk.channel.core.base.AError;

/**
 *
 * 对 IPersistentNet 的 subscribe unSubscribe 动作的回调
 */
public interface IOnSubscribeListener {

    /** 订阅、取消订阅成功
     * @param topic
     */
    void onSuccess(String topic);

    /** 订阅或取消订阅失败
     * @param topic
     * @param error
     */
    void onFailed(String topic, AError error);

    /** 回调是否UI线程返回
     * @return
     */
    boolean needUISafety();
}
