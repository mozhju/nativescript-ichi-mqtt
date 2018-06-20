package com.aliyun.alink.linksdk.channel.core.persistent;

/**
 * Created by huanyu.zhy on 17/12/14.
 *
 * 长连接状态
 */
public enum PersistentConnectState {
    /**
     * 已连接
     */
    CONNECTED,
    /**
     * 已断连
     */
    DISCONNECTED,
    /**
     * 建联中
     */
    CONNECTING,
    /**
     * 建联失败
     */
    CONNECTFAIL
}
