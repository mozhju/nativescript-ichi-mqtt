package com.aliyun.alink.linksdk.channel.core.persistent.event;

/**
 * Created by wuzonglu on 17/4/14.
 */

public interface IConnectionStateListener {
    void onConnectFail(String msg);
    void onConnected();
    void onDisconnect();
}
