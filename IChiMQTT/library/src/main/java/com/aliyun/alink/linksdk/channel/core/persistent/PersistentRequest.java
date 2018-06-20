package com.aliyun.alink.linksdk.channel.core.persistent;

import com.aliyun.alink.linksdk.channel.core.base.ARequest;

/**长连接请求参数基类
 */
public class PersistentRequest extends ARequest {
    /**
     * 通过payloadObj.toString()获取实际的payload string
     */
    public Object payloadObj;
    public Object context;
}
