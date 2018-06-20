package com.aliyun.alink.linksdk.channel.core.base;

import java.io.Serializable;

/**请求回复类
 */
public class AResponse implements Serializable {
    public Object data; // the payloadObj structure is up to the request-type
}
