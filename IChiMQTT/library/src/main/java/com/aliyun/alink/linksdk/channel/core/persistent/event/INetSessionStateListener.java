package com.aliyun.alink.linksdk.channel.core.persistent.event;

/**通道状态通知接口
 * Created on 17/5/4.
 */

public interface INetSessionStateListener {
    void onNeedLogin();
    void onSessionEffective();
    void onSessionInvalid();
}
