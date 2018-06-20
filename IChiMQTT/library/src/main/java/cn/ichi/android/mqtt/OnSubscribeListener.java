package cn.ichi.android.mqtt;

import android.util.Log;

import com.aliyun.alink.linksdk.channel.core.base.AError;
import com.aliyun.alink.linksdk.channel.core.persistent.IOnSubscribeListener;

/**
 * Created by mozj on 2018/5/25.
 */

public class OnSubscribeListener implements IOnSubscribeListener {
    @Override
    public void onSuccess(String var1) {
        Log.i("OnSubscribeListener", "onSuccess: " + var1);
    }

    @Override
    public void onFailed(String var1, AError var2) {
        Log.i("OnSubscribeListener", "onFailed: " + var1);
        Log.i("OnSubscribeListener", "onFailed: " + var2.getMsg());
    }

    @Override
    public boolean needUISafety() {
        Log.i("OnSubscribeListener", "SubscribeListener needUISafety");
        return true;
    }
}
