package com.aliyun.alink.linksdk.channel.core.persistent.mqtt.send;

import com.aliyun.alink.linksdk.channel.core.base.AError;
import com.aliyun.alink.linksdk.channel.core.base.ARequest;
import com.aliyun.alink.linksdk.channel.core.base.AResponse;
import com.aliyun.alink.linksdk.channel.core.base.ASend;
import com.aliyun.alink.linksdk.channel.core.base.IOnCallListener;
import com.aliyun.alink.linksdk.channel.core.persistent.BadNetworkException;
import com.aliyun.alink.linksdk.channel.core.persistent.IOnSubscribeListener;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.request.MqttPublishRequest;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.request.MqttSubscribeRequest;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.utils.MqttAlinkProtocolHelper;
import com.aliyun.alink.linksdk.tools.ALog;
import com.aliyun.alink.linksdk.tools.ThreadTools;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by huanyu.zhy on 17/11/7.
 *
 * Mqtt 发送类，Publish Subscribe UnSubscribe 等请求的处理
 */
public class MqttSend extends ASend implements IMqttActionListener,IMqttMessageListener{

    private static final String TAG = "MqttSend";

    private IOnSubscribeListener subscribeListener = null;

    /** For Publish Send
     * @param request
     * @param listener
     */
    public MqttSend(ARequest request, IOnCallListener listener) {
        super(request, listener);
        setStatus(MqttSendStatus.waitingToSend);
    }

    /**For Subscribe or UnSubsribe Send
     * @param request
     * @param listener
     */
    public MqttSend(ARequest request, IOnSubscribeListener listener) {
        super(request,null);
        this.subscribeListener = listener;
        setStatus(MqttSendStatus.waitingToSend);
    }

    /* I/F */

    /**设置状态
     * @param state
     */
    public void setStatus(MqttSendStatus state) {
        this.status = state;
    }

    public MqttSendStatus getStatus(){
        return (MqttSendStatus) this.status;
    }

    public IOnSubscribeListener getSubscribeListener() {
        return this.subscribeListener;
    }


    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        if (this.request instanceof MqttSubscribeRequest){
            setStatus(MqttSendStatus.completed);

            boolean isSucc = true;
            try {
                int qos = asyncActionToken.getGrantedQos()[0];
                if (qos == 128)
                    isSucc = false;
            }catch (Exception e) {
                ALog.d(TAG,"onSuccess(),getGrantedQos");
            }
            if (subscribeListener != null){
                if (this.subscribeListener.needUISafety()){
                    ThreadTools.runOnUiThread(new MqttSendResponseRunnable(this,(isSucc? MqttSendResponseRunnable.MSG_SUBSRIBE_SUCCESS:MqttSendResponseRunnable.MSG_SUBSRIBE_FAILED),null));
                } else if (isSucc){
                    this.subscribeListener.onSuccess(((MqttSubscribeRequest)this.request).topic);
                } else {
                    AError error = new AError();
                    error.setCode(AError.AKErrorServerBusinessError);
                    error.setMsg("subACK Failure");
                    this.subscribeListener.onFailed(((MqttSubscribeRequest)this.request).topic,error);
                }
            }
        }
        else if (this.request instanceof MqttPublishRequest) {
            MqttPublishRequest publishRequest = (MqttPublishRequest) this.request;

            //非RPC 请求，直接返回
            if (!publishRequest.isRPC){
                setStatus(MqttSendStatus.completed);

                if (this.listener != null) {
                    if (this.listener.needUISafety()) {
                        ThreadTools.runOnUiThread(new MqttSendResponseRunnable(this,MqttSendResponseRunnable.MSG_PUBLISH_SUCCESS,null));
                    } else {
                        this.listener.onSuccess(this.request,this.response);
                    }

                }
            }
            //RPC 请求，订阅reply成功后需要Publish
            else {
                if (status == MqttSendStatus.waitingToSubReply) {
                    setStatus(MqttSendStatus.subReplyed);
                    MqttSendExecutor sendExecutor = new MqttSendExecutor();
                    sendExecutor.asyncSend(this);
                } else if (status == MqttSendStatus.waitingToPublish) {
                    setStatus(MqttSendStatus.published);
                }
            }

        }
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken,final Throwable exception) {

        final String msg = (null != exception ? exception.getMessage() : "MqttNet send failed: unknown error");

        setStatus(MqttSendStatus.completed);
        if (this.request instanceof MqttSubscribeRequest){

            if (subscribeListener != null){

                byte type = MqttSendResponseRunnable.MSG_SUBSRIBE_FAILED;
                if (exception instanceof BadNetworkException) {
                    type = MqttSendResponseRunnable.MSG_SUBSRIBE_BADNET;
                }

                if (this.subscribeListener.needUISafety()){
                    ThreadTools.runOnUiThread(new MqttSendResponseRunnable(this,type,msg));
                } else if (type == MqttSendResponseRunnable.MSG_SUBSRIBE_BADNET) {
                    AError error = new AError();
                    error.setCode(AError.AKErrorInvokeNetError);
                    this.subscribeListener.onFailed(((MqttSubscribeRequest)this.request).topic, error);
                } else {
                    AError error = new AError();
                    error.setCode(AError.AKErrorUnknownError);
                    error.setMsg(msg);
                    this.subscribeListener.onFailed(((MqttSubscribeRequest)this.request).topic, error);
                }
            }

        }
        else if (this.request instanceof MqttPublishRequest) {
            if (this.listener != null) {

                byte type = MqttSendResponseRunnable.MSG_PUBLISH_FAILED;
                if (exception instanceof BadNetworkException) {
                    type = MqttSendResponseRunnable.MSG_PUBLISH_BADNET;
                }

                if (this.listener.needUISafety()){
                    ThreadTools.runOnUiThread(new MqttSendResponseRunnable(this,type,msg));
                } else if (type == MqttSendResponseRunnable.MSG_PUBLISH_BADNET) {
                    AError error = new AError();
                    error.setCode(AError.AKErrorInvokeNetError);
                    this.listener.onFailed(this.request, error);
                } else {
                    AError error = new AError();
                    error.setCode(AError.AKErrorUnknownError);
                    error.setMsg(msg);
                    this.listener.onFailed(this.request, error);
                }
            }
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        ALog.d(TAG,"messageArrived(), topic ="+topic+" msg ="+message.toString());

        if (this.request instanceof MqttPublishRequest) {

            MqttPublishRequest publishRequest = (MqttPublishRequest) this.request;

            String msgid = MqttAlinkProtocolHelper.parseMsgIdFromPayload(message.toString());

            if (publishRequest.isRPC && (this.status == MqttSendStatus.published || this.status == MqttSendStatus.waitingToPublish)
                &&  topic.equals(publishRequest.replyTopic) && publishRequest.msgId.equals(msgid)) {
                ALog.d(TAG,"messageArrived(), match!");

                setStatus(MqttSendStatus.completed);

                if (null == this.response)
                    this.response = new AResponse();
                this.response.data = message.toString();

                if (this.listener != null) {
                    if (this.listener.needUISafety()) {
                        ThreadTools.runOnUiThread(new MqttSendResponseRunnable(this,MqttSendResponseRunnable.MSG_PUBLISH_SUCCESS,null));
                    } else {
                        this.listener.onSuccess(this.request,this.response);
                    }
                }
            }
        }

    }
}
