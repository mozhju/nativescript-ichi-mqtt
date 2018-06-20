package cn.ichi.android.mqtt;

import android.util.Log;

import com.aliyun.alink.linksdk.channel.core.persistent.event.IOnPushListener;

/**
 * Created by mozj on 2018/5/25.
 */

public class OnPushListener implements IOnPushListener {


    @Override
    public void onCommand(String topic,String data)
    {
        Log.i("OnPushListener", "onCommand to: " + topic);
        Log.i("OnPushListener", "onCommand to: " + data);
    }

    @Override
    public boolean shouldHandle(String topic) {
        Log.i("OnPushListener", "shouldHandle to: " + topic);
        return true;
    }
}
