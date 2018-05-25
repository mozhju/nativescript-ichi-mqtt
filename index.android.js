"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var MqttClient = (function () {
    function MqttClient(productKey, deviceName, deviceSecret, serverUri, subscriptionTopic, publishTopic) {
        if (serverUri && subscriptionTopic && publishTopic) {
            this.client = new cn.ichi.android.mqtt.MqttClient(productKey, deviceName, deviceSecret, serverUri, subscriptionTopic, publishTopic);
        }
        else {
            this.client = new cn.ichi.android.mqtt.MqttClient(productKey, deviceName, deviceSecret, null, null, null);
        }
    }
    MqttClient.prototype.setLogLevel = function (level) {
        return this.client.setLogLevel(level);
    };
    MqttClient.prototype.setConnectionStateListener = function (stateListener) {
        var listener = new cn.ichi.android.mqtt.ConnectionStateListener({
            onConnectFail: function (message) {
                stateListener.onConnectFail(message);
            },
            onConnected: function () {
                stateListener.onConnected();
            },
            onDisconnect: function () {
                stateListener.onDisconnect();
            }
        });
        return this.client.setConnectionStateListener(listener);
    };
    MqttClient.prototype.setSubscribeListener = function (subscribeListener) {
        var listener = new cn.ichi.android.mqtt.SubscribeListener({
            onSuccess: function (topic) {
                subscribeListener.onSuccess(topic);
            },
            onFailed: function (topic, error) {
                subscribeListener.onFailed(topic, error);
            },
            needUISafety: function () {
                return subscribeListener.needUISafety();
            }
        });
        return this.client.setSubscribeListener(listener);
    };
    MqttClient.prototype.setPushListener = function (pushListener) {
        var listener = new cn.ichi.android.mqtt.PushListener({
            onCommand: function (topic, data) {
                pushListener.onCommand(topic, data);
            },
            shouldHandle: function (topic) {
                return pushListener.shouldHandle(topic);
            }
        });
        return this.client.setPushListener(listener);
    };
    MqttClient.prototype.setCallListener = function (callListener) {
        var listener = new cn.ichi.android.mqtt.CallListener({
            onSuccess: function (request, response) {
                callListener.onSuccess(request, response);
            },
            onFailed: function (request, error) {
                callListener.onFailed(request, error);
            },
            needUISafety: function () {
                return callListener.needUISafety();
            }
        });
        return this.client.setCallListener(listener);
    };
    MqttClient.prototype.startListener = function (activity) {
        if (activity) {
            return this.client.startListener(activity);
        }
        else {
            return this.client.startListener(null);
        }
    };
    MqttClient.prototype.subscribeTopic = function (topic) {
        if (topic) {
            return this.client.subscribeTopic(topic);
        }
        else {
            return this.client.subscribeTopic(null);
        }
    };
    MqttClient.prototype.unSubscribeTopic = function (topic) {
        if (topic) {
            return this.client.unSubscribeTopic(topic);
        }
        else {
            return this.client.unSubscribeTopic(null);
        }
    };
    MqttClient.prototype.publishMessage = function (payloadObj, topic) {
        if (topic) {
            return this.client.publishMessage(topic, payloadObj);
        }
        else {
            return this.client.publishMessage(null, payloadObj);
        }
    };
    MqttClient.prototype.stopListener = function () {
        return this.client.stopListener();
    };
    return MqttClient;
}());
exports.MqttClient = MqttClient;
//# sourceMappingURL=mqtt.android.js.map