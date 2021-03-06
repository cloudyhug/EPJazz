/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ep.net;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An activity that uses WiFi Direct APIs to discover and connect with available
 * devices. WiFi Direct APIs are asynchronous and rely on callback mechanism
 * using interfaces to notify the application of operation success or failure.
 * The application should also register a BroadcastReceiver for notification of
 * WiFi state related events.
 */
public class WiFiDirectActivity extends Activity implements ChannelListener {
    private WifiP2pManager mManager;
    private Channel mChannel;
    private BroadcastReceiver mReceiver = null;

    private TextView tv;

    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();

    private DeviceListListener deviceListListener;
    private DeviceConnectionInfoListener connectionInfoListener;

    private Button getTimeButton;
    private Button connectButton;
    private Button terminateServerButton;

    private long time;
    private long startingTime;

    private WiFiDirectActivity activity;

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    public void setIsTerminateServerButtonEnabled(boolean enabled){
        terminateServerButton.setEnabled(enabled);
    }

    public void setIsGetTimeButtonEnabled(boolean enabled){
        getTimeButton.setEnabled(enabled);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_direct_activity);

        // add necessary intent values to be matched.
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        tv = findViewById(R.id.textView);

        deviceListListener = new DeviceListListener();
        connectionInfoListener = new DeviceConnectionInfoListener(this);

        connectButton = findViewById(R.id.connect_button);
        getTimeButton = findViewById(R.id.get_time_button);
        terminateServerButton = findViewById(R.id.terminate_button);
        terminateServerButton.setEnabled(false);

        time = 0;
        startingTime = 0;

        activity = this;

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiP2pDevice d = deviceListListener.getPeers().get(0);
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = d.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                connect(config);
            }
        });

        getTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ClientAsyncTask(activity, connectionInfoListener.getInfo().groupOwnerAddress).execute();
            }
        });

        terminateServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terminate(time, startingTime);
            }
        });

        if (!isWifiP2pEnabled) {
            Toast.makeText(WiFiDirectActivity.this, "Warning : Wifi P2P disabled",
                    Toast.LENGTH_SHORT).show();
        }
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(WiFiDirectActivity.this, "Discovery Initiated",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(WiFiDirectActivity.this, "Discovery Failed : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setDebugText(String t) {
        tv.setText(t);
    }

    public DeviceListListener getDeviceListListener() {
        return deviceListListener;
    }

    public ConnectionInfoListener getConnectionInfoListener() {
        return connectionInfoListener;
    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
        deviceListListener.clearPeers();
    }

    public void connect(WifiP2pConfig config) {
        mManager.connect(mChannel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(WiFiDirectActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setTime(long t) {
        time = t;
    }

    public void setStartingTime(long st) {
        startingTime = st;
    }

    public void terminate(long t, long st) {
        Intent data = new Intent();
        data.putExtra("time", t);
        data.putExtra("startingTime", st);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (mManager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            mManager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
