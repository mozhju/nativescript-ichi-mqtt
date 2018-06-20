package com.aliyun.alink.linksdk.channel.core.persistent.event;

/**下推通道接口类
 */

public interface IOnPushListener {

    /**下推消息回调接口
     * @param method
     * @param data 下推的消息内容
     */
    void onCommand(String method,String data);

    /**根据method过滤消息
     * @param method : 本次下推消息的命令名称
     * @return : 若返回true，则onCommand被调用，返回false，则onCommand不会被调用
     */
    boolean shouldHandle(String method);
}
