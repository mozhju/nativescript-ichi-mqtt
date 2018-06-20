package com.aliyun.alink.linksdk.channel.core.base;

/**长连接和短连接的数据发送接口
 */
public interface INet {
    /**
     * @param request 发送数据对象基类，需要根据不同的INet实现类传入不同的ARequest子类对象
     *                长连接传入：PersistentRequest对象
     *                短连接传：TransitoryRequest对象
     * @param listener 发送结果回调接口
     * @return AConnect对象，可以用于重试
     */
    ASend asyncSend(ARequest request, IOnCallListener listener);

    /**重新发送请求
     * @param connect  上次调用asyncSend返回值
     */
    void retry(ASend connect);
}
