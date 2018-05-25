package cn.ichi.android.mqtt;


/**
 * Created by mozj on 2018/5/24.
 */

public interface SubscribeListener {
    public void onSuccess(String var1);
//    public void onFailed(String var1, AError var2);
    public void onFailed(String var1, Object var2);
    public boolean needUISafety();
}
