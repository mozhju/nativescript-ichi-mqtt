package cn.ichi.android.mqtt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aliyun.alink.linksdk.channel.core.base.AError;
import com.aliyun.alink.linksdk.channel.core.base.ARequest;
import com.aliyun.alink.linksdk.channel.core.base.AResponse;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private EditText edtName;
    private EditText edtQty;
    private EditText edtFee;

    private Button btnShow;
    private Button btnOrder;
    private Button btnBlack;

    String productKey = "C1oPllCH8n8";
    //    String deviceName = "Ichi98000000011";
//    String deviceSecret = "55uWQzmszzENJ3gN6HBZaYOSA2nAEerO";
    String deviceName = "Ichi37000000062";
    String deviceSecret = "aLkgyjLyQRRfJv2VRE9FfoMux5Nng3yb";

    private MqttClient m_MqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_MqttClient = new MqttClient(productKey, deviceName, deviceSecret, null, null, null);

        m_MqttClient.setLogLevel(2);

        m_MqttClient.setPushListener(new PushListener() {
            @Override
            public void onCommand(String topic, String data) {
                Log.i("MainActivity", "PushListener onCommand: " + topic);
            }

            @Override
            public boolean shouldHandle(String topic) {
                Log.i("MainActivity", "PushListener shouldHandle: " + topic);
                return true;
            }
        });
        m_MqttClient.setConnectionStateListener(new ConnectionStateListener() {
            @Override
            public void onConnectFail(String msg) {
                Log.i("MainActivity", "ConnectionStateListener onConnectFail: " + msg);
            }

            @Override
            public void onConnected() {
                Log.i("MainActivity", "ConnectionStateListener onConnected");
            }

            @Override
            public void onDisconnect() {
                Log.i("MainActivity", "ConnectionStateListener onDisconnect");
            }
        });
        m_MqttClient.setSubscribeListener(new SubscribeListener() {
            @Override
            public void onSuccess(String var1) {
                Log.i("MainActivity", "SubscribeListener onSuccess: " + var1);
            }

            @Override
            public void onFailed(String var1, Object var2) {
                Log.i("MainActivity", "SubscribeListener onFailed: " + var1);
            }

            @Override
            public boolean needUISafety() {
                Log.i("MainActivity", "SubscribeListener needUISafety" );
                return false;
            }
        });
        m_MqttClient.setCallListener(new CallListener() {
            @Override
            public void onSuccess(Object request, Object response) {
                Log.i("MainActivity", "CallListener onSuccess" );
            }

            @Override
            public void onFailed(Object request, Object error) {
                Log.i("MainActivity", "CallListener onFailed" );
            }

            @Override
            public boolean needUISafety() {
                Log.i("MainActivity", "CallListener needUISafety" );
                return false;
            }
        });

        m_MqttClient.startListener(this);

        edtName = (EditText)findViewById(R.id.name);
        edtQty = (EditText)findViewById(R.id.qty);
        edtFee = (EditText)findViewById(R.id.fee);

        btnShow = (Button)findViewById(R.id.btnShow);
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_MqttClient.publishMessage(null, edtName.getText().toString());
            }
        });

        btnOrder = (Button)findViewById(R.id.btnOrder);
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        btnBlack = (Button)findViewById(R.id.btnBlack);
        btnBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        m_MqttClient.stopListener();

        super.onDestroy();
    }
}
