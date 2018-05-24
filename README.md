# nativescript-ichi-printer

mqtt client for NativeScript.

## Supported platforms

- Android (any device with Android 4.4 and higher)

There is no support for iOS yet!

## Installing

```
tns plugin add nativescript-ichi-mqtt
```

## Usage

Here is a TypeScript example:

```js
import {MqttClient} from "nativescript-ichi-mqtt";

// new Mqtt Client
var mqttClient = new MqttClient("productKey", "deviceName", "deviceSecret");
// var mqttClient = new MqttClient("productKey", "deviceName", "deviceSecret",
//        "serverUri", "subscriptionTopic", "publishTopic");

// LogLevel : 1 DEBUG, 2 INFO, 3 WARN, 4 ERROR
mqttClient.setLogLevel(3);

var stateListener = {
        onConnectFail: function(message) {
            console.log("stateListener onConnectFail: ", message);
        },
        onConnected: function() {
            console.log("stateListener onConnected");
        },
        onDisconnect: function() {
            console.log("stateListener onDisconnect");
        }
    }
mqttClient.setConnectionStateListener(stateListener);

var subscribeListener = {
        onSuccess: function(topic) {
            console.log("subscribeListener onSuccess: ", topic);
        },
        onFailed: function(topic, error) {
            console.log("subscribeListener onFailed: ", topic);
        },
        needUISafety: function() {
            console.log("subscribeListener needUISafety");
            return true;
        }
    }
mqttClient.setSubscribeListener(subscribeListener);

var pushListener = {
        onCommand: function(topic, data) {
            console.log("pushListener onCommand: ", topic);
        },
        shouldHandle: function(topic) {
            console.log("pushListener shouldHandle", topic);
            return true;
        }
    }
mqttClient.setPushListener(pushListener);

var callListener = {
        onSuccess: function(request, response) {
            console.log("callListener onSuccess: ");
        },
        onFailed: function(request, error) {
            console.log("callListener onFailed: ");
        },
        needUISafety: function() {
            console.log("callListener needUISafety");
            return true;
        }
    }
mqttClient.setCallListener(callListener);


mqttClient.initialization();

// mqttClient.subscribeTopic();
// mqttClient.subscribeTopic("subscribeTopic2");

//mqttClient.publishMessage("publishMessage");
mqttClient.publishMessage("publishMessage", "publishTopic2");


// mqttClient.deinitialization();

```



