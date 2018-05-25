
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

    // 
    constructor(productKey: string, deviceName:string, deviceSecret:string,
        serverUri?: string, subscriptionTopic?:string, publishTopic?:string)
    {
    }

    // 设置SDK内部Log等级, 在startListener前设置。LogLevel : 1 DEBUG, 2 INFO, 3 WARN, 4 ERROR
    public setLogLevel(level: number): void {
        throw "Not implemented";
    }

    // 设置连接到MQTT服务器结果的回调, 在startListener前设置
    public setConnectionStateListener(stateListener: ConnectionStateListener): void {
        throw "Not implemented";
    }

    // 设置订阅结果的回调, 在startListener前设置
    public setSubscribeListener(subscribeListener: SubscribeListener): void {
        throw "Not implemented";
    }

    // 设置MQTT服务器push的消息的回调, 在startListener前设置
    public setPushListener(pushListener: PushListener): void {
        throw "Not implemented";
    }

    // 设置Publish请求结果的回调, 在startListener前设置
    public setCallListener(callListener: CallListener): void {
        throw "Not implemented";
    }

    // 开始监听，需要确保app 的 Activity已经生成
    public startListener(activity?: any): boolean {
        throw "Not implemented";
    }
    
    // 订阅主题
    public subscribeTopic(topic?: string): void {
        throw "Not implemented";
    }

    // 取消订阅
    public unSubscribeTopic(topic?: string): void {
        throw "Not implemented";
    }

    // 向MQTT服务器publish消息
    public publishMessage(payloadObj: any, topic?: string): void {
        throw "Not implemented";
    }

    // 停止监听
    public stopListener(): void {
        throw "Not implemented";
    }
}

