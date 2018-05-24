

export declare interface ConnectionStateListener {
    onConnectFail(message: string): void;
    onConnected(): void;
    onDisconnect(): void;
}

export declare interface SubscribeListener {
    onSuccess(topic: string): void;
    onFailed(topic: string, error: any): void;
    needUISafety(): boolean;
}

export declare interface PushListener {
    onCommand(topic: string, data: string): void;
    shouldHandle(topic: string): boolean;
}

export declare interface CallListener {
    onSuccess(request: any, response: any): void;
    onFailed(request: any, error: any): void;
    needUISafety(): boolean;
}

export declare class MqttClient {
    private client;

    // 
    constructor(productKey: string, deviceName:string, deviceSecret:string,
        serverUri?: string, subscriptionTopic?:string, publishTopic?:string);

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
    public initialization(activity?: any): boolean;
    
    // 订阅主题
    public subscribeTopic(topic?: string): void;

    // 向MQTT服务器publish消息
    public publishMessage(payloadObj: any, topic?: string): void;

    // 反初始化SDK
    public deinitialization(): void;
}


