package com.aliyun.alink.linksdk.channel.core.base;

/**请求结果回调类
 */
public interface IOnCallListener {

    /**请求成功感的回调接口
     * @param request
     * @param response
     */
    void onSuccess(ARequest request, AResponse response);

    /**请求失败的回调接口
     * @param request
     * @param error
     */
    void onFailed(ARequest request, AError error);

    /**
     * @return
     */
    boolean needUISafety();
}
