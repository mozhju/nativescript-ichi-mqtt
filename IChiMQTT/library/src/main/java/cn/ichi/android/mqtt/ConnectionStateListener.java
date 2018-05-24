package cn.ichi.android.mqtt;

/**
 * Created by mozj on 2018/5/24.
 */

public interface ConnectionStateListener {
    public  void onConnectFail(String msg);
    public  void onConnected();
    public  void onDisconnect();
}
