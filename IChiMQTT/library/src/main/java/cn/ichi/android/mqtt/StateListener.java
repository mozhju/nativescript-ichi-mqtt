package cn.ichi.android.mqtt;

import android.util.Log;

import com.aliyun.alink.linksdk.channel.core.persistent.event.IConnectionStateListener;

/**
 * Created by mozj on 2018/5/25.
 */

public class StateListener implements IConnectionStateListener {

    @Override
    public  void onConnectFail(String msg) { //通道连接失败
        Log.i("StateListener", "Failed: " + msg);
    }

    @Override
    public  void onConnected() { // 通道已连接
        Log.i("StateListener", "Connected " );

    }

    @Override
    public  void onDisconnect() { // 通道断连
        Log.i("StateListener", "onDisconnect");
    }
}
