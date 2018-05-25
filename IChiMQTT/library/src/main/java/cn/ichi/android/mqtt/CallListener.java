package cn.ichi.android.mqtt;


/**
 * Created by mozj on 2018/5/24.
 */

public interface CallListener {
//    public void onSuccess(ARequest request, AResponse response);
//    public void onFailed(ARequest request, AError error);
    public void onSuccess(Object request, Object response);
    public void onFailed(Object request, Object error);
    public boolean needUISafety();
}
