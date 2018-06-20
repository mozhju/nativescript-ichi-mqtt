package com.aliyun.alink.linksdk.channel.core.persistent.mqtt.utils;


import android.text.TextUtils;

import org.json.JSONObject;

/**
 * Created by huanyu.zhy on 17/11/9.
 */
public class MqttAlinkProtocolHelper {

    private static final String TAG = "MqttAlinkProtocolHelper";

    public static String parseMsgIdFromPayload(String payload) {
        if (TextUtils.isEmpty(payload))
            return null;
        try {
            JSONObject payloadJson = new JSONObject(payload);
            return payloadJson.getString("id");
        } catch (Exception ex) {
            return null;
        }
    }

}
