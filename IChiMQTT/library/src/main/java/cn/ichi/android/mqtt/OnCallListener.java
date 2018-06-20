package cn.ichi.android.mqtt;

import android.util.Log;

import com.aliyun.alink.linksdk.channel.core.base.AError;
import com.aliyun.alink.linksdk.channel.core.base.ARequest;
import com.aliyun.alink.linksdk.channel.core.base.AResponse;
import com.aliyun.alink.linksdk.channel.core.base.IOnCallListener;

/**
 * Created by mozj on 2018/5/25.
 */

public class OnCallListener implements IOnCallListener {
    @Override
    public void onSuccess(ARequest request, AResponse response) {
        Log.i("OnCallListener", "send , onSuccess");
    }

    @Override
    public void onFailed(ARequest request, AError error) {
        Log.i("OnCallListener", "send , onFailed: " + error.getMsg());
    }

    @Override
    public boolean needUISafety() {
        Log.i("OnCallListener", "CallListener needUISafety");
        return true;
    }
}
