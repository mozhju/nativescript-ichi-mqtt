
declare module cn {
    export module ichi {
        export module android {
            export module mqtt {
                interface IConnectionStateListener {
                    onConnectFail(message: string): void;
                    onConnected(): void;
                    onDisconnect(): void;
                }

                interface ISubscribeListener {
                    onSuccess(topic: string): void;
                    onFailed(topic: string, error: any): void;
                    needUISafety(): boolean;
                }

                interface IPushListener {
                    onCommand(topic: string, data: string): void;
                    shouldHandle(topic: string): boolean;
                }

                interface ICallListener {
                    onSuccess(request: any, response: any): void;
                    onFailed(request: any, error: any): void;
                    needUISafety(): boolean;
                }

                class ConnectionStateListener {
                    constructor(implementation: IConnectionStateListener);
                    onConnectFail(message: string): void;
                    onConnected(): void;
                    onDisconnect(): void;
                }

                class SubscribeListener {
                    constructor(implementation: ISubscribeListener);
                    onSuccess(topic: string): void;
                    onFailed(topic: string, error: any): void;
                    needUISafety(): boolean;
                }

                class PushListener {
                    constructor(implementation: IPushListener);
                    onCommand(topic: string, data: string): void;
                    shouldHandle(topic: string): boolean;
                }

                class CallListener {
                    constructor(implementation: ICallListener);
                    onSuccess(request: any, response: any): void;
                    onFailed(request: any, error: any): void;
                    needUISafety(): boolean;
                }

                export class MqttClient {
                    // 
                    constructor(productKey: string, deviceName:string, deviceSecret:string);
                    // 
                    constructor(serverUri: string, productKey: string, deviceName:string, deviceSecret:string,
                        subscriptionTopic:string, publishTopic:string);

                    // 设置SDK内部Log等级, 在initialization前设置。LogLevel : 1 DEBUG, 2 INFO, 3 WARN, 4 ERROR
                    public setLogLevel(level: number): void;

                    // 设置连接到MQTT服务器结果的回调, 在initialization前设置
                    public setConnectionStateListener(stateListener: ConnectionStateListener): void;

                    // 设置订阅结果的回调, 在initialization前设置
                    public setSubscribeListener(subscribeListener: SubscribeListener): void;

                    // 设置MQTT服务器push的消息的回调, 在initialization前设置
                    public setPushListener(pushListener: PushListener): void;

                    // 设置Publish请求结果的回调, 在initialization前设置
                    public setCallListener(callListener: CallListener): void;

                    // 初始化SDK，需要确保app 的 Activity已经生成
                    public initialization(): boolean;
                    // 初始化SDK
                    public initialization(activity: any): boolean;
                    
                    // 订阅主题
                    public subscribeTopic(): void;
                    public subscribeTopic(topic: string): void;

                    // 向MQTT服务器publish消息
                    public publishMessage(payloadObj: any): void;
                    public publishMessage(topic: string, payloadObj: any): void;

                    // 反初始化SDK
                    public deinitialization(): void;
                }
            }
        }
    }
}


export interface ConnectionStateListener {
    onConnectFail(message: string): void;
    onConnected(): void;
    onDisconnect(): void;
}

export interface SubscribeListener {
    onSuccess(topic: string): void;
    onFailed(topic: string, error: any): void;
    needUISafety(): boolean;
}

export interface PushListener {
    onCommand(topic: string, data: string): void;
    shouldHandle(topic: string): boolean;
}

export interface CallListener {
    onSuccess(request: any, response: any): void;
    onFailed(request: any, error: any): void;
    needUISafety(): boolean;
}

export class MqttClient {
    private client: cn.ichi.android.mqtt.MqttClient;

    // 
    constructor(productKey: string, deviceName:string, deviceSecret:string,
        serverUri?: string, subscriptionTopic?:string, publishTopic?:string)
    {
        if (serverUri && subscriptionTopic && publishTopic) {
            this.client = new cn.ichi.android.mqtt.MqttClient(serverUri, productKey, deviceName,
                deviceSecret, subscriptionTopic, publishTopic);
        } else {
            this.client = new cn.ichi.android.mqtt.MqttClient(productKey, deviceName, deviceSecret);
        }
    }

    // 设置SDK内部Log等级, 在initialization前设置。LogLevel : 1 DEBUG, 2 INFO, 3 WARN, 4 ERROR
    public setLogLevel(level: number): void {
        return this.client.setLogLevel(level);
    }

    // 设置连接到MQTT服务器结果的回调, 在initialization前设置
    public setConnectionStateListener(stateListener: ConnectionStateListener): void {
        var listener = new cn.ichi.android.mqtt.ConnectionStateListener({
            onConnectFail: function(message: string) {
                stateListener.onConnectFail(message);
            },
            onConnected: function() {
                stateListener.onConnected();
            },
            onDisconnect: function() {
                stateListener.onDisconnect();
            }
        });
        return this.client.setConnectionStateListener(listener);
    }

    // 设置订阅结果的回调, 在initialization前设置
    public setSubscribeListener(subscribeListener: SubscribeListener): void {
        var listener = new cn.ichi.android.mqtt.SubscribeListener({
            onSuccess: function(topic: string) {
                subscribeListener.onSuccess(topic);
            },
            onFailed: function(topic: string, error: any) {
                subscribeListener.onFailed(topic, error);
            },
            needUISafety: function() {
                return subscribeListener.needUISafety();
            }
        });
        return this.client.setSubscribeListener(listener);
    }

    // 设置MQTT服务器push的消息的回调, 在initialization前设置
    public setPushListener(pushListener: PushListener): void {
        var listener = new cn.ichi.android.mqtt.PushListener({
            onCommand: function(topic: string, data: string) {
                pushListener.onCommand(topic, data);
            },
            shouldHandle: function(topic: string) {
                return pushListener.shouldHandle(topic);
            }
        });
        return this.client.setPushListener(listener);
    }

    // 设置Publish请求结果的回调, 在initialization前设置
    public setCallListener(callListener: CallListener): void {
        var listener = new cn.ichi.android.mqtt.CallListener({
            onSuccess: function(request: any, response: any) {
                callListener.onSuccess(request, response);
            },
            onFailed: function(request: any, error: any) {
                callListener.onFailed(request, error);
            },
            needUISafety: function() {
                return callListener.needUISafety();
            }
        });
        return this.client.setCallListener(listener);
    }

    // 初始化SDK，需要确保app 的 Activity已经生成
    public initialization(activity?: any): boolean {
        if (activity) {
            return this.client.initialization(activity);
        } else {
            return this.client.initialization();
        }
    }
    
    // 订阅主题
    public subscribeTopic(topic?: string): void {
        if (topic) {
            return this.client.subscribeTopic(topic);
        } else {
            return this.client.subscribeTopic();
        }
    }

    // 向MQTT服务器publish消息
    public publishMessage(payloadObj: any, topic?: string): void {
        if (topic) {
            return this.client.publishMessage(topic, payloadObj);
        } else {
            return this.client.publishMessage(payloadObj);
        }
    }

    // 反初始化SDK
    public deinitialization(): void {
        return this.client.deinitialization();
    }
}

