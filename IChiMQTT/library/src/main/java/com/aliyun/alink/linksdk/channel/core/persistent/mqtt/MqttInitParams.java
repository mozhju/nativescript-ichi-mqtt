package com.aliyun.alink.linksdk.channel.core.persistent.mqtt;

import android.text.TextUtils;
import com.aliyun.alink.linksdk.channel.core.persistent.PersistentInitParams;

/**
 * Created by huanyu.zhy on 17/11/10.
 */
public class MqttInitParams extends PersistentInitParams {
    public String productKey;
    public String deviceName;
    public String deviceSecret;


    public MqttInitParams(String productKey,String deviceName, String deviceSecret){
        this.productKey = productKey;
        this.deviceName = deviceName;
        this.deviceSecret = deviceSecret;
    }

    public boolean checkValid(){
        if (TextUtils.isEmpty(productKey) || TextUtils.isEmpty(deviceName) || TextUtils.isEmpty(deviceSecret)) {
            return false;
        }
        return true;
    }
}
