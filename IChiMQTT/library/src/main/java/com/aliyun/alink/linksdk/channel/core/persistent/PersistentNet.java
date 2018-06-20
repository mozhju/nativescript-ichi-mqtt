package com.aliyun.alink.linksdk.channel.core.persistent;

import android.content.Context;
import com.aliyun.alink.linksdk.channel.core.base.ASend;
import com.aliyun.alink.linksdk.channel.core.base.ARequest;
import com.aliyun.alink.linksdk.channel.core.base.INet;
import com.aliyun.alink.linksdk.channel.core.base.IOnCallListener;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.MqttNet;

/**
 * 长连接
 */

public class PersistentNet implements IPersisitentNet {
    static private String TAG = "PersistentNet";

    static private String NETPRO_MQTT = "MQTT";

    private String netpro = NETPRO_MQTT;
    private IPersisitentNet iPersisitentNet = null;

    private static class InstanceHolder {
        private static final PersistentNet sInstance = new PersistentNet();
    }

    static public PersistentNet getInstance() {
        return InstanceHolder.sInstance;
    }

    public void setDefaultProtocol (String protocol) {
        if (protocol != null && protocol.equals(NETPRO_MQTT)) {
            netpro = NETPRO_MQTT;
            iPersisitentNet = MqttNet.getInstance();
        }
    }
    public String getDefaultProtocol () {
        return netpro;
    }

    public void init(Context context, PersistentInitParams initParams) {
        //setEnv (context, ALinkConfigure.alinkEnv);
        INet iNet = getINet();
        if (iNet instanceof MqttNet) {
            ((MqttNet)iNet).init(context, initParams);
        }
    }

    public void destroy() {
        IPersisitentNet iNet = getINet();
        if (iNet instanceof MqttNet) {
            ((MqttNet)iNet).destroy();
        }
    }

    @Override
    public PersistentConnectState getConnectState() {
        IPersisitentNet iNet = getINet();
        if (iNet instanceof MqttNet) {
            return ((MqttNet)iNet).getConnectState();
        }
        return null;
    }

    @Override
    public ASend asyncSend(ARequest request, IOnCallListener listener)
    {
        PersistentRequest pRequest = (PersistentRequest)request;
        return getINet().asyncSend(pRequest, listener);
    }

    @Override
    public void retry(ASend connect)
    {
        getINet().retry(connect);
    }

    @Override
    public void subscribe(String topic, IOnSubscribeListener listener) {
        getINet().subscribe(topic,listener);
    }

    @Override
    public void unSubscribe(String topic, IOnSubscribeListener listener) {
        getINet().unSubscribe(topic,listener);
    }

    /*Private Methods
    * */

    private IPersisitentNet getINet() {
        if (iPersisitentNet == null) {//default
            iPersisitentNet = MqttNet.getInstance();
        }

        return iPersisitentNet;
    }
}
