package com.aliyun.alink.linksdk.channel.core.persistent.mqtt.send;

import android.content.Context;
import android.text.TextUtils;
import com.aliyun.alink.linksdk.channel.core.base.ASend;
import com.aliyun.alink.linksdk.channel.core.persistent.BadNetworkException;
import com.aliyun.alink.linksdk.channel.core.persistent.ISendExecutor;
import com.aliyun.alink.linksdk.channel.core.persistent.PersistentConnectState;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.MqttNet;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.request.MqttPublishRequest;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.request.MqttSubscribeRequest;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.utils.MqttAlinkProtocolHelper;
import com.aliyun.alink.linksdk.tools.ALog;
import com.aliyun.alink.linksdk.tools.NetTools;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by huanyu.zhy on 17/11/7.
 *
 * Mqtt 发送执行类，执行Publish Subcribe UnSubsribe 等请求
 */
public class MqttSendExecutor implements ISendExecutor {

    private static final String TAG = "MqttSendExecutor";
    
    @Override
    public void asyncSend(ASend send) {
        if (null == send || null == send.getRequest()) {
            ALog.e(TAG, "asyncSend(): bad parameters: NULL");
            return;
        }
        IMqttAsyncClient mqttAsyncClient = MqttNet.getInstance().getClient();

        if (null == mqttAsyncClient) {
            ALog.e(TAG, "asyncSend(): MqttNet::getClient() return null");
            return;
        }

        if (!(send instanceof MqttSend)) {
            ALog.d(TAG, "asyncSend(): bad parameter: need MqttSend");
            return;
        }

        MqttSend mqttSend = (MqttSend) send;

        // bad network
        Context context = MqttNet.getInstance().getContext();
        if (null != context && !NetTools.isAvailable(context)) {
            mqttSend.setStatus(MqttSendStatus.completed);
            mqttSend.onFailure(null, new BadNetworkException());
            return;
        }

        //gateway disconnect
        if (MqttNet.getInstance().getConnectState() != PersistentConnectState.CONNECTED) {
            mqttSend.setStatus(MqttSendStatus.completed);
            mqttSend.onFailure(null, new BadNetworkException());
            return;
        }


        //handle mqtt publish req
        if (send.getRequest() instanceof MqttPublishRequest) {
            MqttPublishRequest publishRequest = (MqttPublishRequest) send.getRequest();

            if (TextUtils.isEmpty(publishRequest.topic) || publishRequest.payloadObj == null) {
                ALog.e(TAG, "asyncSend(): bad parameters: topic or payload empty");
                return;
            }

            //RPC 请求，需要先订阅reply （complete 为 retry场景）
            if (publishRequest.isRPC && (mqttSend.getStatus() == MqttSendStatus.waitingToSend || mqttSend.getStatus() == MqttSendStatus.completed)) {
                try{
                    publishRequest.msgId = MqttAlinkProtocolHelper.parseMsgIdFromPayload(publishRequest.payloadObj.toString());

                    if (TextUtils.isEmpty(publishRequest.replyTopic))
                        publishRequest.replyTopic = publishRequest.topic+"_reply";

                    ALog.d(TAG,"publish: RPC sub reply topic: [ "+publishRequest.replyTopic+" ]");
                    mqttSend.setStatus(MqttSendStatus.waitingToSubReply);
                    mqttAsyncClient.subscribe(publishRequest.replyTopic,0,null,mqttSend,mqttSend);
                }catch (Exception e){
                    ALog.d(TAG,"asyncSend(), publish , send subsribe reply error, e = "+e.toString());
                    mqttSend.setStatus(MqttSendStatus.completed);
                    mqttSend.onFailure(null,new MqttThrowable(e.getMessage()));
                }
            } else {
                try {
                    String content = publishRequest.payloadObj.toString();
                    ALog.d(TAG,"publish: topic: [ "+publishRequest.topic+" ]");
                    ALog.d(TAG,"publish: payload: [ "+content+" ]");
                    MqttMessage message = new MqttMessage(content.getBytes("utf-8"));
                    message.setQos(0);

                    // isRPC 已订阅过了
                    if (publishRequest.isRPC) {
                        mqttSend.setStatus(MqttSendStatus.waitingToPublish);
                    }else {
                        mqttSend.setStatus(MqttSendStatus.waitingToComplete);
                    }

                    mqttAsyncClient.publish(publishRequest.topic, message, null, mqttSend);
                } catch (Exception e) {
                    ALog.d(TAG, "asyncSend(), send publish error, e = " + e.toString());
                    mqttSend.setStatus(MqttSendStatus.completed);
                    mqttSend.onFailure(null, new MqttThrowable(e.getMessage()));
                }
            }
        }
        // handle mqtt subscribe req
        else if (send.getRequest() instanceof MqttSubscribeRequest) {
            MqttSubscribeRequest subscribeRequest = (MqttSubscribeRequest) send.getRequest();

            if (TextUtils.isEmpty(subscribeRequest.topic)) {
                ALog.e(TAG, "asyncSend(): bad parameters: subsribe req , topic empty");
                return;
            }
            try{
                mqttSend.setStatus(MqttSendStatus.waitingToComplete);

                if (subscribeRequest.isSubscribe){
                    ALog.d(TAG,"subscribe: topic: [ "+subscribeRequest.topic+" ]");
                    mqttAsyncClient.subscribe(subscribeRequest.topic,0,null,mqttSend);
                } else {
                    ALog.d(TAG,"unsubscribe: topic: [ "+subscribeRequest.topic+" ]");
                    mqttAsyncClient.unsubscribe(subscribeRequest.topic,null,mqttSend);
                }
            }catch (Exception e){
                ALog.d(TAG,"asyncSend(), send subsribe error, e = "+e.toString());
                mqttSend.setStatus(MqttSendStatus.completed);
                mqttSend.onFailure(null,new MqttThrowable(e.getMessage()));
            }

        }



    }
}
