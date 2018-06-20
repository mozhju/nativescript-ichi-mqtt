package com.aliyun.alink.linksdk.channel.core.persistent.mqtt.send;

import com.aliyun.alink.linksdk.channel.core.base.AError;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.request.MqttSubscribeRequest;

/**
 * Created by huanyu.zhy on 17/11/9.
 */
public class MqttSendResponseRunnable implements Runnable {

    static public final byte MSG_PUBLISH_SUCCESS = 0X01;
    static public final byte MSG_PUBLISH_FAILED = 0X02;
    static public final byte MSG_PUBLISH_BADNET = 0X03;
    static public final byte MSG_SUBSRIBE_SUCCESS = 0X04;
    static public final byte MSG_SUBSRIBE_FAILED = 0X05;
    static public final byte MSG_SUBSRIBE_BADNET = 0X06;

    private MqttSend sendObj = null;
    private byte msgType = 0;
    private String errorMsg = null;

    public MqttSendResponseRunnable(MqttSend send, byte type, String errorMsg) {
        this.sendObj = send;
        this.msgType = type;
        this.errorMsg = errorMsg;
    }

    @Override
    public void run() {
        if (null == this.sendObj)
            return;

        switch (this.msgType) {

            case MSG_PUBLISH_SUCCESS:
                if (this.sendObj.getListener() == null)
                    return;
                this.sendObj.getListener().onSuccess(this.sendObj.getRequest(),this.sendObj.getResponse());
                break;
            case MSG_PUBLISH_FAILED:
            case MSG_PUBLISH_BADNET:
                if (this.sendObj.getListener() == null)
                    return;

                AError error = new AError();
                if (msgType == MSG_PUBLISH_BADNET) {
                    error.setCode(AError.AKErrorInvokeNetError);
                } else {
                    error.setCode(AError.AKErrorUnknownError);
                }
                error.setMsg(errorMsg);
                this.sendObj.getListener().onFailed(this.sendObj.getRequest(), error);
                break;

            case MSG_SUBSRIBE_SUCCESS:
                if (this.sendObj.getSubscribeListener() == null)
                    return;
                this.sendObj.getSubscribeListener().onSuccess(((MqttSubscribeRequest)this.sendObj.getRequest()).topic);
                break;
            case MSG_SUBSRIBE_FAILED:
            case MSG_SUBSRIBE_BADNET:
                if (this.sendObj.getSubscribeListener() == null)
                    return;

                AError error1 = new AError();
                if (msgType == MSG_PUBLISH_BADNET) {
                    error1.setCode(AError.AKErrorInvokeNetError);
                } else {
                    error1.setCode(AError.AKErrorUnknownError);
                }
                error1.setMsg(errorMsg);
                this.sendObj.getSubscribeListener().onFailed(((MqttSubscribeRequest)this.sendObj.getRequest()).topic, error1);
                break;
        }
    }
}
