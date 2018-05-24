package cn.ichi.android.mqtt;


import com.aliyun.alink.linksdk.channel.core.persistent.event.IOnPushListener;

/**
 * Created by mozj on 2018/5/24.
 */

public interface PushListener {
    public void onCommand(String topic,String data);
    public boolean shouldHandle(String topic);
}
