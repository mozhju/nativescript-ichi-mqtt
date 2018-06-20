package com.aliyun.alink.linksdk.channel.core.base;

/**
 *
 * 一次发送请求，封装Request、Response、Listener 及发送状态
 */
public abstract class ASend {
    protected final ARequest request;
    protected AResponse response = null;
    protected ISendStatus status = null;
    protected final IOnCallListener listener;

    /* methods: I/F */

    public ASend(final ARequest request, IOnCallListener listener) {
        this.request = request;
        this.listener = listener;
        this.status = ASendStatus.waitingToSend;
        this.response = new AResponse();
    }

    public ARequest getRequest() {
        return this.request;
    }

    public AResponse getResponse() {
        return this.response;
    }

    public IOnCallListener getListener() {
        return this.listener;
    }

    public ISendStatus getStatus() {
        return this.status;
    }
}
