package com.aliyun.alink.linksdk.channel.core.persistent.event;

import java.util.HashMap;
import java.util.Set;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.aliyun.alink.linksdk.tools.ALog;

/**长连接事件分发
 * 注册不同的事件回调（收到下推数据，通道连接状态变化，通道对应的session状态变化），接收相应通知
 * Created on 17/4/14.
 */

public class PersistentEventDispatcher {
    static private final String TAG = PersistentEventDispatcher.class.getName();

    static public final byte MSG_CONNECTED = 0X01;
    static public final byte MSG_DISCONNECT = 0X02;
    static public final byte MSG_CONNECT_FAIL = 0X07;
    static public final byte MSG_RECEIVECMD = 0X03;
    static public final byte MSG_NEEDLOGIN = 0X04;
    static public final byte MSG_SESSION_EFFECTIVE = 0X05;
    static public final byte MSG_SESSION_INVALID = 0X06;

    protected HashMap<IOnPushListener, Boolean> onPushListeners = null;
    protected HashMap<IConnectionStateListener,Boolean> tunnelListeners = null;
    protected HashMap<INetSessionStateListener,Boolean> sessionListeners = null;
    protected MainThreadHandler mainThreadHandler = null;

    private PersistentEventDispatcher() {
    }

    private static class InstanceHolder {
        private static final PersistentEventDispatcher sInstance;
        static {
            sInstance = new PersistentEventDispatcher();
            sInstance.init();
        }
    }

    /**返回EventDispatcher单例对象
     * @return EventDispatcher对象实例
     */
    public static PersistentEventDispatcher getInstance(){
        return InstanceHolder.sInstance;
    }

    void init() {
        if (mainThreadHandler == null) {
            mainThreadHandler = new MainThreadHandler();
        }
    }

    /**注册数据下推接收器
     * @param listener 长连接通道下推数据回调接口
     * @param needUISafety  调用回调接口是否需要切换到UI线程
     */
    public void registerOnPushListener(IOnPushListener listener, boolean needUISafety) {
        synchronized (this) {
            if (null == listener)
                return;

            if (null == this.onPushListeners)
                this.onPushListeners = new HashMap<>();

            this.onPushListeners.put(listener, needUISafety);
        }
    }

    /**取消注册数据下推接收器
     * @param listener 上次调用registerOnPushListener传入的接口对象
     */
    public void unregisterOnPushListener(IOnPushListener listener) {
        synchronized (this) {
            if (null == listener || null == this.onPushListeners || 0 >= this.onPushListeners.size())
                return;

            this.onPushListeners.remove(listener);
        }
    }

    /**注册连接状态变化通知
     * @param listener 回调接口
     * @param needUISafety  调用回调接口是否需要切换到UI线程
     */
    public void registerOnTunnelStateListener(IConnectionStateListener listener, boolean needUISafety) {
        synchronized (this) {
            if (null == listener)
                return;

            if (null == this.tunnelListeners)
                this.tunnelListeners = new HashMap<>();

            this.tunnelListeners.put(listener, needUISafety);
        }
    }

    /**取消注册连接状态变化通知
     * @param listener 回调接口
     */
    public void unregisterOnTunnelStateListener(IConnectionStateListener listener) {
        synchronized (this) {
            if (null == listener || null == this.tunnelListeners || 0 >= this.tunnelListeners.size())
                return;

            this.tunnelListeners.remove(listener);
        }
    }

    /**注册通道绑定的session有效性变化通知
     * @param listener 回调接口
     * @param needUISafety  调用回调接口是否需要切换到UI线程
     */
    public void registerNetSessionStateListener(INetSessionStateListener listener, boolean needUISafety) {
        synchronized (this) {
            if (null == listener)
                return;

            if (null == this.sessionListeners)
                this.sessionListeners = new HashMap<>();

            this.sessionListeners.put(listener, needUISafety);
        }
    }

    /**取消注册通道绑定的session有效性变化通知
     * @param listener 回调接口
     */
    public void unregisterNetSessionStateListener(INetSessionStateListener listener) {
        synchronized (this) {
            if (null == listener || null == this.sessionListeners || 0 >= this.sessionListeners.size())
                return;

            this.sessionListeners.remove(listener);
        }
    }

    /**
     * @param what
     * @param method For MSG_RECEIVECMD
     * @param content For MSG_RECEIVECMD
     * @param message FOr MSG_CONNECT_FAIL
     */
    public void broadcastMessage(int what, String method, String content,String message) {
        synchronized (this) {
            if (what == MSG_RECEIVECMD && onPushListeners != null) {

                Set<IOnPushListener> listeners = onPushListeners.keySet();
                for (IOnPushListener listener : onPushListeners.keySet()) {

                    if (false == listener.shouldHandle(method)) {
                        continue;
                    }

                    if (listeners.contains(listener)) {
                        mainThreadHandler.sendMessage(MSG_RECEIVECMD, listener,method, content);
                    } else {
                        listener.onCommand(method,content);
                    }
                }
            } else if (what == MSG_CONNECTED || what == MSG_DISCONNECT || what == MSG_CONNECT_FAIL) {
                if (tunnelListeners != null) {
                    Set<IConnectionStateListener> listeners = tunnelListeners.keySet();
                    for (IConnectionStateListener listener : listeners) {
                        if (listeners.contains(listener)) {
                            mainThreadHandler.sendMessage(what, listener, message);
                        } else {
                            OnTunnelState(what, listener, message);
                        }
                    }
                }
            } else if (what == MSG_SESSION_EFFECTIVE || what == MSG_SESSION_INVALID || what == MSG_NEEDLOGIN) {
                if (sessionListeners != null) {
                    Set<INetSessionStateListener> listeners = sessionListeners.keySet();
                    for (INetSessionStateListener listener : listeners) {
                        if (listeners.contains(listener)) {
                            mainThreadHandler.sendMessage(what, listener, content);
                        } else {
                            OnSessionState(what, listener);
                        }
                    }
                }
            }
        }
    }

    static private class MainThreadHandler extends Handler {

        public MainThreadHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            if (null == msg || null == msg.obj || (false == msg.obj instanceof Argument))
                return;

            Argument argument = (Argument) msg.obj;
            if (argument.listener instanceof IOnPushListener) {
                IOnPushListener listener = (IOnPushListener) argument.listener;
                if (msg.what == MSG_RECEIVECMD) {
                    listener.onCommand(argument.method,argument.content);
                }
            }

            if (argument.listener instanceof IConnectionStateListener) {
                OnTunnelState (msg.what, (IConnectionStateListener) argument.listener,argument.content);
            }
        }

        public void sendMessage(int what, Object listener, String content) {
            Message msg = this.obtainMessage();
            msg.what = what;
            msg.obj = new Argument(listener, content);

            this.sendMessageDelayed(msg, 10);
        }

        public void sendMessage(int what,Object listenr, String method,String content){
            Message msg = this.obtainMessage();
            msg.what = what;
            msg.obj = new Argument(listenr,method,content);

            this.sendMessageDelayed(msg, 10);
        }

        static private class Argument {
            public Object listener;
            public String content;
            public String method;

            public Argument(Object listener, String content) {
                this.listener = listener;
                this.content = content;
            }

            public Argument(Object listener,String method, String content) {
                this.listener = listener;
                this.method = method;
                this.content = content;
            }
        }
    }

    static void OnTunnelState (int type, IConnectionStateListener listener,String content) {
        if (listener != null) {
            try {
                if (type == MSG_CONNECTED) {
                    listener.onConnected();
                } else if (type == MSG_DISCONNECT) {
                    listener.onDisconnect();
                } else if (type == MSG_CONNECT_FAIL) {
                    listener.onConnectFail(content);
                }
            } catch (Exception e) {
                ALog.e(TAG, "catch exception from IConnectionStateListener");
            }
        }
    }

    static void OnSessionState (int type, INetSessionStateListener listener) {
        if (listener != null) {
            try {
                if (type == MSG_SESSION_EFFECTIVE) {
                    listener.onSessionEffective();
                } else if (type == MSG_SESSION_INVALID) {
                    listener.onSessionInvalid();
                } else if (type == MSG_NEEDLOGIN) {
                    listener.onNeedLogin();
                }
            } catch (Exception e) {
                ALog.e(TAG, "catch exception from INetSessionStateListener");
            }
        }
    }
}
