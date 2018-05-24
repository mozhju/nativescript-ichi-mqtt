package cn.ichi.android.mqtt;

import com.aliyun.alink.linksdk.channel.core.base.AError;
import com.aliyun.alink.linksdk.channel.core.base.ARequest;
import com.aliyun.alink.linksdk.channel.core.base.AResponse;
import com.aliyun.alink.linksdk.channel.core.base.IOnCallListener;

/**
 * Created by mozj on 2018/5/24.
 */

public interface CallListener {
    public void onSuccess(ARequest request, AResponse response);
    public void onFailed(ARequest request, AError error);
    public boolean needUISafety();
}
