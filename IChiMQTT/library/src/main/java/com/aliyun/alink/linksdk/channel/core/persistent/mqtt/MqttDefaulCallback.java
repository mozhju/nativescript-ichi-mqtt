package com.aliyun.alink.linksdk.channel.core.persistent.mqtt;

import com.aliyun.alink.linksdk.channel.core.persistent.PersistentConnectState;
import com.aliyun.alink.linksdk.channel.core.persistent.event.PersistentEventDispatcher;
import com.aliyun.alink.linksdk.tools.ALog;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by huanyu.zhy on 17/11/9.
 */
public class MqttDefaulCallback implements MqttCallbackExtended {

    private static final String TAG = "MqttDefaulCallback";

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        ALog.d(TAG,"connectComplete," +serverURI);
        MqttNet.getInstance().setConnectState(PersistentConnectState.CONNECTED);
        PersistentEventDispatcher.getInstance().broadcastMessage(PersistentEventDispatcher.MSG_CONNECTED, null,null,null);
    }

    @Override
    public void connectionLost(Throwable cause) {
        ALog.d(TAG,"connectionLost,cause:" + cause);
        cause.printStackTrace();
        MqttNet.getInstance().setConnectState(PersistentConnectState.DISCONNECTED);
        PersistentEventDispatcher.getInstance().broadcastMessage(PersistentEventDispatcher.MSG_DISCONNECT, null,null,null);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        ALog.d(TAG,"messageArrived,topic = [" + topic + "] , msg = ["
            + new String(message.getPayload(), "UTF-8") + "],  ");
        PersistentEventDispatcher.getInstance().broadcastMessage(PersistentEventDispatcher.MSG_RECEIVECMD, topic,new String(message.getPayload(), "UTF-8"),null);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        //如果是QoS0的消息，token.resp是没有回复的
        ALog.d(TAG,"deliveryComplete! " + ((token == null || token.getResponse() == null) ? "null"
            : token.getResponse().getKey()));
    }
}
