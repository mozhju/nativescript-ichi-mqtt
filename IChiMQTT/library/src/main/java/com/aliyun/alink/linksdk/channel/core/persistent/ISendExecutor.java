package com.aliyun.alink.linksdk.channel.core.persistent;

import com.aliyun.alink.linksdk.channel.core.base.ASend;

/**
 * 发送处理接口
 */
public interface ISendExecutor {
    void asyncSend(ASend send);
}
