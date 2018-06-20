package com.aliyun.alink.linksdk.channel.core.persistent.mqtt;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import android.content.Context;
import android.text.TextUtils;
import com.aliyun.alink.linksdk.channel.core.base.ARequest;
import com.aliyun.alink.linksdk.channel.core.base.ASend;
import com.aliyun.alink.linksdk.channel.core.base.IOnCallListener;
import com.aliyun.alink.linksdk.channel.core.persistent.IOnSubscribeListener;
import com.aliyun.alink.linksdk.channel.core.persistent.IPersisitentNet;
import com.aliyun.alink.linksdk.channel.core.persistent.PersistentConnectState;
import com.aliyun.alink.linksdk.channel.core.persistent.PersistentInitParams;
import com.aliyun.alink.linksdk.channel.core.persistent.event.PersistentEventDispatcher;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.request.MqttSubscribeRequest;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.send.MqttSend;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.send.MqttSendExecutor;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.utils.MqttTrustManager;
import com.aliyun.alink.linksdk.tools.ALog;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by huanyu.zhy on 17/11/7.
 */
public class MqttNet implements IPersisitentNet {
    private static final String TAG = "MqttNet";


    private Context context;
    private MemoryPersistence persistence;
    private MqttAsyncClient mqttAsyncClient;
    private SSLSocketFactory socketFactory;
    private MqttConnectOptions connOpts;

    private boolean isInitConnect = false;
    private PersistentConnectState connectState = PersistentConnectState.DISCONNECTED;

    private static class InstanceHolder {
        private static final MqttNet sInstance = new MqttNet();
    }

    static public MqttNet getInstance() {
        return InstanceHolder.sInstance;
    }

    /*
     * I/F
     * */

    public void init(Context context, PersistentInitParams initParams) {
        ALog.d(TAG,"init()");

        if (isInitConnect || connectState == PersistentConnectState.CONNECTING || connectState == PersistentConnectState.CONNECTED) {
            ALog.d(TAG,"init(), already init, ignore init call!");
            return;
        }

        if (context == null || initParams == null || !(initParams instanceof MqttInitParams) || !((MqttInitParams)initParams).checkValid()) {
            ALog.e(TAG,"init error ,params error");
            return;
        }

        this.context = context;
        MqttInitParams mqttInitParams = (MqttInitParams) initParams;

        MqttConfigure.productKey = mqttInitParams.productKey;
        MqttConfigure.deviceName = mqttInitParams.deviceName;
        MqttConfigure.deviceSecret = mqttInitParams.deviceSecret;

        if (MqttConfigure.mqttRootCrtFile == null) {
            try {
                MqttConfigure.mqttRootCrtFile = context.getAssets().open(MqttConfigure.DEFAULT_ROOTCRT);
            }catch (Exception e) {
                ALog.e(TAG, "setCertFile : cannot config cert file：" + e.getMessage());
            }
        }
        mqttClientConnect();
    }

    public void destroy(){
        isInitConnect = false;
    }

    public void setConnectState(PersistentConnectState connectState) {
        this.connectState = connectState;
    }

    @Override
    public PersistentConnectState getConnectState() {
        return connectState;
    }

    @Override
    public ASend asyncSend(ARequest request, IOnCallListener listener) {

        MqttSend mqttSend = new MqttSend(request,listener);
        MqttSendExecutor sendExecutor = new MqttSendExecutor();
        sendExecutor.asyncSend(mqttSend);
        return mqttSend;
    }

    @Override
    public void retry(ASend connect) {
        MqttSendExecutor sendExecutor = new MqttSendExecutor();
        sendExecutor.asyncSend(connect);
    }

    @Override
    public void subscribe(String topic, IOnSubscribeListener listener) {
        if (TextUtils.isEmpty(topic)) {
            ALog.d(TAG,"subscribe, topic is empty");
            return;
        }

        MqttSubscribeRequest subscribeRequest = new MqttSubscribeRequest();
        subscribeRequest.topic = topic;
        subscribeRequest.isSubscribe = true;
        MqttSend mqttSend = new MqttSend(subscribeRequest,listener);
        MqttSendExecutor sendExecutor = new MqttSendExecutor();
        sendExecutor.asyncSend(mqttSend);
    }

    @Override
    public void unSubscribe(String topic, IOnSubscribeListener listener) {
        if (TextUtils.isEmpty(topic)) {
            ALog.d(TAG,"unSubscribe, topic is empty");
            return;
        }

        MqttSubscribeRequest subscribeRequest = new MqttSubscribeRequest();
        subscribeRequest.topic = topic;
        subscribeRequest.isSubscribe = false;
        MqttSend mqttSend = new MqttSend(subscribeRequest,listener);
        MqttSendExecutor sendExecutor = new MqttSendExecutor();
        sendExecutor.asyncSend(mqttSend);
    }

    /** 获取Client
     * @return
     */
    public IMqttAsyncClient getClient(){
        return this.mqttAsyncClient;
    }

    public Context getContext(){
        return this.context;
    }

    /*Private Methods
    * */

    private void mqttClientConnect(){
        persistence = new MemoryPersistence();
        String timestamp = System.currentTimeMillis() + "";

        String serverURI = MqttConfigure.mqttHost;
        if (TextUtils.isEmpty(MqttConfigure.mqttHost)) {
            serverURI = "ssl://"+MqttConfigure.productKey+MqttConfigure.DEFAULT_HOST;
        } else if (!MqttConfigure.mqttHost.startsWith("ssl://")){
            serverURI = "ssl://"+serverURI;
        }

        //// Daily Test Evi URI
        //serverURI = "ssl://10.125.0.27:1883";

        String clientId = MqttConfigure.deviceName+"&"+MqttConfigure.productKey;

        Map<String, String> params = new HashMap<String, String>();
        params.put("productKey", MqttConfigure.productKey);
        params.put("deviceName", MqttConfigure.deviceName);
        params.put("clientId", clientId);
        params.put("timestamp", timestamp);

        String mqttClientId = clientId +
            "|securemode="+MqttConfigure.SECURE_MODE +
            ",signmethod="+MqttConfigure.SIGN_METHOD+
            ",timestamp="+timestamp+"|";
        String mqttUsername = MqttConfigure.deviceName + "&" + MqttConfigure.productKey;
        String mqttPassword = hmacSign(params, MqttConfigure.deviceSecret);
        try {
            mqttAsyncClient = new MqttAsyncClient(serverURI,mqttClientId,persistence);
        }catch (Exception e){
            ALog.e(TAG,"create mqtt client error,e"+e.toString());
            e.printStackTrace();
            return;
        }
        connOpts = new MqttConnectOptions();
        connOpts.setMqttVersion(4); // MQTT 3.1.1
        if (MqttConfigure.isCheckRootCrt) {
            try {
                socketFactory = createSSLSocket();//Daily Need rm
                connOpts.setSocketFactory(socketFactory);
            }catch (Exception e){
                ALog.e(TAG,"create SSL Socket error"+e.toString());
                e.printStackTrace();
            }
        }
        //设置是否自动重连
        connOpts.setAutomaticReconnect(true);
        //如果是true，那么清理所有离线消息，即QoS1或者2的所有未接收内容
        connOpts.setCleanSession(false);
        connOpts.setUserName(mqttUsername);
        connOpts.setPassword(mqttPassword.toCharArray());
        connOpts.setKeepAliveInterval(65);

        mqttAsyncClient.setCallback(new MqttDefaulCallback());
        try {
            connectState = PersistentConnectState.CONNECTING;
            mqttAsyncClient.connect(connOpts, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    ALog.d(TAG,"connect onSuccess");
                    isInitConnect = true;
                    connectState = PersistentConnectState.CONNECTED;
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    ALog.d(TAG,"connect onFailure, exce = "+exception.toString());
                    connectState = PersistentConnectState.CONNECTFAIL;
                    PersistentEventDispatcher.getInstance().broadcastMessage(PersistentEventDispatcher.MSG_CONNECT_FAIL, null,null,exception.toString());
                }
            });
            ALog.d(TAG,"mqtt client connect..");
        }catch (Exception e){
            ALog.e(TAG," mqtt client connect error,e"+e.toString());
            e.printStackTrace();
            connectState = PersistentConnectState.CONNECTFAIL;
            PersistentEventDispatcher.getInstance().broadcastMessage(PersistentEventDispatcher.MSG_CONNECT_FAIL, null,null,e.toString());
        }
    }


    private  SSLSocketFactory createSSLSocket() throws Exception {
        SSLContext context = SSLContext.getInstance("TLSV1.2");
        context.init(null, new TrustManager[] {new MqttTrustManager(MqttConfigure.mqttRootCrtFile)}, null);
        SSLSocketFactory socketFactory = context.getSocketFactory();
        return socketFactory;
    }


    private String hmacSign(Map<String,String> params, String secret){
        if (params == null || TextUtils.isEmpty(secret))
            return null;

        //将参数Key按字典顺序排序
        String[] sortedKeys = params.keySet().toArray(new String[] {});
        Arrays.sort(sortedKeys);

        //生成规范化请求字符串
        StringBuilder canonicalizedQueryString = new StringBuilder();
        for (String key : sortedKeys) {
            if ("sign".equalsIgnoreCase(key)) {
                continue;
            }
            canonicalizedQueryString.append(key).append(params.get(key));
        }
        try {
            SecretKey secretKey = new SecretKeySpec(secret.getBytes("utf-8"), "hmacsha1");
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            byte[] data = mac.doFinal(canonicalizedQueryString.toString().getBytes("utf-8"));
            return bytesToHexString(data);
        } catch (Exception e) {
            ALog.e(TAG,"hmacSign error, e"+e.toString());
            e.printStackTrace();
        }
        return null;
    }


    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }
}
