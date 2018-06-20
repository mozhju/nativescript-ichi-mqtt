package cn.ichi.android.mqtt;

import android.app.Activity;
import android.content.Context;
import android.media.MediaRouter;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;

import com.aliyun.alink.linksdk.channel.core.base.AError;
import com.aliyun.alink.linksdk.channel.core.base.ARequest;
import com.aliyun.alink.linksdk.channel.core.base.AResponse;
import com.aliyun.alink.linksdk.channel.core.base.IOnCallListener;
import com.aliyun.alink.linksdk.channel.core.persistent.IOnSubscribeListener;
import com.aliyun.alink.linksdk.channel.core.persistent.PersistentNet;
import com.aliyun.alink.linksdk.channel.core.persistent.event.IConnectionStateListener;
import com.aliyun.alink.linksdk.channel.core.persistent.event.IOnPushListener;
import com.aliyun.alink.linksdk.channel.core.persistent.event.PersistentEventDispatcher;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.MqttInitParams;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.request.MqttPublishRequest;
import com.aliyun.alink.linksdk.tools.ALog;


/**
 * Created by mozj on 2018/5/9.
 */

public class MqttClient {

    private static String TAG = "MqttClient";

    private String m_ProductKey = null;
    private String m_DeviceName = null;
    private String m_DeviceSecret = null;

    private String m_ServerUri = null;
    private String m_SubscriptionTopic = null;
    private String m_PublishTopic = null;

    // 连接到MQTT服务器时回调
    private IConnectionStateListener m_StateListener = new StateListener();
//    private IConnectionStateListener m_StateListener = new IConnectionStateListener() {
//        @Override
//        public  void onConnectFail(String msg) { //通道连接失败
//            log("Failed to connect to: " + m_ServerUri);
//            log("Failed message: " + msg);
//        }
//
//        @Override
//        public  void onConnected() { // 通道已连接
//            log("Connected to: " + m_ServerUri);
//
//            subscribeTopic(null);
//        }
//
//        @Override
//        public  void onDisconnect() { // 通道断连
//            log("Disconnect to: " + m_ServerUri);
//        }
//    };
    // 订阅时回调
    private IOnSubscribeListener m_SubscribeListener = new OnSubscribeListener();
//    private IOnSubscribeListener m_SubscribeListener = new IOnSubscribeListener() {
//        @Override
//        public void onSuccess(String var1) {
//            log("onSuccess: " + var1);
//        }
//
//        @Override
//        public void onFailed(String var1, AError var2) {
//            log("onFailed: " + var1);
//            log("onFailed: " + var2.getMsg());
//        }
//
//        @Override
//        public boolean needUISafety() {
//            log("SubscribeListener needUISafety");
//            return true;
//        }
//    };
    // 收到MQTT服务器push的消息时回调
    private IOnPushListener m_PushListener = new OnPushListener();
//    private IOnPushListener m_PushListener = new IOnPushListener() {
//
//        @Override
//        public void onCommand(String topic,String data)
//        {
//            log("onCommand to: " + topic);
//            log("onCommand to: " + data);
//        }
//
//        @Override
//        public boolean shouldHandle(String topic) {
//            log("shouldHandle to: " + topic);
//            return true;
//        }
//    };
    // Publish请求时回调
    private IOnCallListener m_CallListener = new OnCallListener();
//    private IOnCallListener m_CallListener = new IOnCallListener() {
//        @Override
//        public void onSuccess(ARequest request, AResponse response) {
//            log("send , onSuccess");
//        }
//
//        @Override
//        public void onFailed(ARequest request, AError error) {
//            log("send , onFailed: " + error.getMsg());
//        }
//
//        @Override
//        public boolean needUISafety() {
//            log("CallListener needUISafety");
//            return true;
//        }
//    };

    private PersistentNet m_PersistentNet = null;
    private PersistentEventDispatcher m_Dispatcher = null;

    public MqttClient(String productKey, String deviceName, String deviceSecret,
                      String serverUri, String subscriptionTopic, String publishTopic) {
        m_ProductKey = productKey;
        m_DeviceName = deviceName;
        m_DeviceSecret = deviceSecret;

        if (serverUri != null && !serverUri.isEmpty()) {
            m_ServerUri = serverUri;
        } else {
            m_ServerUri = "ssl://" + productKey + ".iot-as-mqtt.cn-shanghai.aliyuncs.com:1883";
        }
        if (subscriptionTopic != null && !subscriptionTopic.isEmpty()) {
            m_SubscriptionTopic = subscriptionTopic;
        } else {
            m_SubscriptionTopic = "/" + productKey + "/" + deviceName + "/data";
        }
        if (publishTopic != null && !publishTopic.isEmpty()) {
            m_PublishTopic = publishTopic;
        } else {
            m_PublishTopic = "/" + productKey + "/" + deviceName + "/PrintSuccess";
        }

        m_PersistentNet = PersistentNet.getInstance();
        m_Dispatcher = PersistentEventDispatcher.getInstance();
    }


    public void setLogLevel(int level) {
        ALog.setLevel((byte)level);
    }


    public boolean startListener(Object object) {
        if (object != null && !(object instanceof Context)) {
            return false;
        }

        Context context = (Context)object;
        if(context == null) {
            context = Utils.getActivity();
        }

        if(context == null) {
            return false;
        }

        MqttInitParams initParams = new MqttInitParams(m_ProductKey, m_DeviceName, m_DeviceSecret);

        m_PersistentNet.init(context, initParams);

        // 注册通道监听
        m_Dispatcher.registerOnTunnelStateListener(m_StateListener, true);

        // 注册Push监听
        PersistentEventDispatcher.getInstance().registerOnPushListener(m_PushListener,true);

        return true;
    }


    // 连接到MQTT服务器结果回调
    public void setConnectionStateListener(final ConnectionStateListener stateListener) {
        if (stateListener != null) {
//            m_StateListener = new IConnectionStateListener() {
//                @Override
//                public  void onConnectFail(String msg) { //通道连接失败
//                    log("Failed to connect to: " + m_ServerUri);
//                    log("Failed message: " + msg);
//                    stateListener.onConnectFail(msg);
//                }
//
//                @Override
//                public  void onConnected() { // 通道已连接
//                    log("Connected to: " + m_ServerUri);
//                    stateListener.onConnected();
//
//                    subscribeTopic(null);
//                }
//
//                @Override
//                public  void onDisconnect() { // 通道断连
//                    log("Disconnect to: " + m_ServerUri);
//
//                    stateListener.onDisconnect();
//
//                }
//            };
        }
    }


    // 订阅结果回调
    public void setSubscribeListener(final SubscribeListener subscribeListener) {
        if (subscribeListener != null) {
//            m_SubscribeListener = new IOnSubscribeListener() {
//                @Override
//                public void onSuccess(String var1) {
//                    log("onSuccess: " + var1);
//                    subscribeListener.onSuccess(var1);
//                }
//
//                @Override
//                public void onFailed(String var1, AError var2) {
//                    log("onFailed: " + var1);
//                    log("onFailed: " + var2.getMsg());
//                    subscribeListener.onFailed(var1, var2);
//                }
//
//                @Override
//                public boolean needUISafety() {
//                    log("SubscribeListener needUISafety");
//                    return subscribeListener.needUISafety();
//                }
//            };
        }
    }


    // 收到MQTT服务器push的消息时回调
    public void setPushListener(final PushListener pushListener) {
        if (pushListener != null) {
//            m_PushListener = new IOnPushListener() {
//
//                @Override
//                public void onCommand(String topic,String data)
//                {
//                    log("onCommand to: " + topic);
//                    log("onCommand to: " + data);
//
//                    pushListener.onCommand(topic, data);
//                }
//
//                @Override
//                public boolean shouldHandle(String topic) {
//                    log("shouldHandle to: " + topic);
//
//                    return pushListener.shouldHandle(topic);
//                }
//            };
        }
    }


    // Publish请求结果回调
    public void setCallListener(final CallListener callListener) {
        if (callListener != null) {
//            m_CallListener = new IOnCallListener() {
//                @Override
//                public void onSuccess(ARequest request, AResponse response) {
//                    log("send , onSuccess");
//
//                    callListener.onSuccess(request, response);
//                }
//
//                @Override
//                public void onFailed(ARequest request, AError error) {
//                    log("send , onFailed: " + error.getMsg());
//
//                    callListener.onFailed(request, error);
//                }
//
//                @Override
//                public boolean needUISafety() {
//                    log("CallListener needUISafety");
//
//                    return callListener.needUISafety();
//                }
//            };
        }
    }


    public void subscribeTopic(String topic) {
        if (topic == null) {
            topic = m_SubscriptionTopic;
        }

        //订阅
        m_PersistentNet.subscribe(topic, m_SubscribeListener);
    }


    public void unSubscribeTopic(String topic) {
        if (topic == null){
            topic = m_SubscriptionTopic;
        }

        //取消订阅
        m_PersistentNet.unSubscribe(topic, m_SubscribeListener);
    }


    public void publishMessage(String topic, Object payloadObj) {
        if (topic == null) {
            topic = m_PublishTopic;
        }

        // Publish 请求
        MqttPublishRequest publishRequest = new MqttPublishRequest();
        publishRequest.isRPC = false;
        publishRequest.topic = m_PublishTopic;
        publishRequest.payloadObj = payloadObj;

        m_PersistentNet.asyncSend(publishRequest, m_CallListener );
    }


     public void stopListener() {
         //取消订阅
         m_PersistentNet.unSubscribe(m_SubscriptionTopic, m_SubscribeListener);

         //取消监听
         m_Dispatcher.unregisterOnPushListener(m_PushListener);

         //取消监听
         m_Dispatcher.unregisterOnTunnelStateListener(m_StateListener);
    }


    private void log(String msg) {
        Log.d(TAG, msg);
    }
}
