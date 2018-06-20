package com.aliyun.alink.linksdk.channel.core.persistent.mqtt;

import java.io.InputStream;

/**
 * Created by huanyu.zhy on 17/11/8.
 */
public class MqttConfigure {

    /**
     * SSL证书
     */
    static public final String DEFAULT_ROOTCRT = "root.crt";

    static public final String DEFAULT_HOST = ".iot-as-mqtt.cn-shanghai.aliyuncs.com:1883";//daily: ssl://10.125.0.27:1883

    /**
     * securemode代表目前安全模式，可选值有2 （TLS直连模式）、3（TCP直连模式），此处固定为2 （TLS直连模式）。
     *
     * mqttClientId格式中||内扩展参数
     */
    static final public int SECURE_MODE = 2;

    /**
     * signmethod代表签名算法类型，这里使用hmacsha1
     *
     * mqttClientId格式中||内扩展参数
     */
    static final public String SIGN_METHOD = "hmacsha1";


    /**
     * 三元组
     */
    public static String productKey;
    public static String deviceName;
    public static String deviceSecret;

    /**
     * 服务端连接目的地址
     *
     * 默认为： "ssl://" + productKey + ".iot-as-mqtt.cn-shanghai.aliyuncs.com:1883"
     */
    static public String mqttHost = null;

    /**
     * 是否校验SSL证书
     */
    static public boolean isCheckRootCrt = true;

    /**
     * SSL 证书
     *
     * 可以保存在assets目录下，并通过以下代码获取
     * Application.getAssets().open("fileName");
     */
    static public InputStream mqttRootCrtFile;

}
