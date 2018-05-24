"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var MqttClient = (function () {
    function MqttClient(productKey, deviceName, deviceSecret, serverUri, subscriptionTopic, publishTopic) {
        if (serverUri && subscriptionTopic && publishTopic) {
            this.client = new cn.ichi.android.mqtt.MqttClient(serverUri, productKey, deviceName,
                deviceSecret, subscriptionTopic, publishTopic);
        } else {
            this.client = new cn.ichi.android.mqtt.MqttClient(productKey, deviceName, deviceSecret);
        }
    }

    // 设置SDK内部Log等级, 在initialization前设置。LogLevel : 1 DEBUG, 2 INFO, 3 WARN, 4 ERROR
    MqttClient.prototype.setLogLevel = function (level) {
        return this.client.setLogLevel(level);
    };

    // 设置连接到MQTT服务器结果的回调, 在initialization前设置
    MqttClient.prototype.setConnectionStateListener = function (stateListener) {
        var listener = new cn.ichi.android.mqtt.ConnectionStateListener( {
            onConnectFail: function(message) {
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
    };

    // 设置订阅结果的回调, 在initialization前设置
    MqttClient.prototype.setSubscribeListener = function(subscribeListener) {
        var listener = new cn.ichi.android.mqtt.SubscribeListener( {
                    onSuccess: function(topic) {
                        subscribeListener.onSuccess(topic);
                    },
                    onFailed: function(topic, error) {
                        subscribeListener.onFailed(topic, error);
                    },
                    needUISafety: function() {
                        return subscribeListener.needUISafety();
                    }
                });
        return this.client.setSubscribeListener(listener);
    };

    // 设置MQTT服务器push的消息的回调, 在initialization前设置
    MqttClient.prototype.setPushListener = function(pushListener) {
        var listener = new cn.ichi.android.mqtt.PushListener({
            onCommand: function(topic, data) {
                pushListener.onCommand(topic, data);
            },
            shouldHandle: function(topic) {
                return pushListener.shouldHandle(topic);
            }
        });
        return this.client.setPushListener(listener);
    };

    // 设置Publish请求结果的回调, 在initialization前设置
    MqttClient.prototype.setCallListener = function(callListener) {
        var listener = new cn.ichi.android.mqtt.CallListener({
            onSuccess: function(request, response) {
                callListener.onSuccess(request, response);
            },
            onFailed: function(request, error) {
                callListener.onFailed(request, error);
            },
            needUISafety: function() {
                return callListener.needUISafety();
            }
        });
        return this.client.setCallListener(listener);
    };

    // 初始化SDK，需要确保app 的 Activity已经生成
    MqttClient.prototype.initialization = function(activity) {
        if (activity) {
            return this.client.initialization(activity);
        } else {
            return this.client.initialization();
        }
    }
    
    // 订阅主题
    MqttClient.prototype.subscribeTopic = function(topic) {
        if (topic) {
            return this.client.subscribeTopic(topic);
        } else {
            return this.client.subscribeTopic();
        }
    }

    // 向MQTT服务器publish消息
    MqttClient.prototype.publishMessage = function(payloadObj, topic) {
        if (topic) {
            return this.client.publishMessage(topic, payloadObj);
        } else {
            return this.client.publishMessage(payloadObj);
        }
    }

    // 反初始化SDK
    MqttClient.prototype.deinitialization = function() {
        return this.client.deinitialization();
    }


    return MqttClient;
}());
exports.MqttClient = MqttClient;
//# sourceMappingURL=index.android.js.map