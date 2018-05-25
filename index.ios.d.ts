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
export declare class MqttClient {
    private client;
    constructor(productKey: string, deviceName: string, deviceSecret: string, serverUri?: string, subscriptionTopic?: string, publishTopic?: string);
    setLogLevel(level: number): void;
    setConnectionStateListener(stateListener: ConnectionStateListener): void;
    setSubscribeListener(subscribeListener: SubscribeListener): void;
    setPushListener(pushListener: PushListener): void;
    setCallListener(callListener: CallListener): void;
    startListener(activity?: any): boolean;
    subscribeTopic(topic?: string): void;
    unSubscribeTopic(topic?: string): void;
    publishMessage(payloadObj: any, topic?: string): void;
    stopListener(): void;
}
