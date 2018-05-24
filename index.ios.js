"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var MqttClient = (function () {
    function MqttClient(productKey, deviceName, deviceSecret, serverUri, subscriptionTopic, publishTopic) {
    }

    // 设置SDK内部Log等级, 在initialization前设置。LogLevel : 1 DEBUG, 2 INFO, 3 WARN, 4 ERROR
    MqttClient.prototype.setLogLevel = function (level) {
        throw "Not implemented";
    };

    // 设置连接到MQTT服务器结果的回调, 在initialization前设置
    MqttClient.prototype.setConnectionStateListener = function (stateListener) {
        throw "Not implemented";
    };

    // 设置订阅结果的回调, 在initialization前设置
    MqttClient.prototype.setSubscribeListener = function(subscribeListener) {
        throw "Not implemented";
    };

    // 设置MQTT服务器push的消息的回调, 在initialization前设置
    MqttClient.prototype.setPushListener = function(pushListener) {
        throw "Not implemented";
    };

    // 设置Publish请求结果的回调, 在initialization前设置
    MqttClient.prototype.setCallListener = function(callListener) {
        throw "Not implemented";
    };

    // 初始化SDK，需要确保app 的 Activity已经生成
    MqttClient.prototype.initialization = function(activity) {
        throw "Not implemented";
    }
    
    // 订阅主题
    MqttClient.prototype.subscribeTopic = function(topic) {
        throw "Not implemented";
    }

    // 向MQTT服务器publish消息
    MqttClient.prototype.publishMessage = function(payloadObj, topic) {
        throw "Not implemented";
    }

    // 反初始化SDK
    MqttClient.prototype.deinitialization = function() {
        throw "Not implemented";
    }


    return MqttClient;
}());
exports.MqttClient = MqttClient;
//# sourceMappingURL=index.ios.js.map