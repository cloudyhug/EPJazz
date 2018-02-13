package com.example.lin.epnetworktest.view;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lin.epnetworktest.R;
import com.example.lin.epnetworktest.controller.WiFiDirectBroadcastReceiver;
import com.example.lin.epnetworktest.model.StartSongTask;

import java.util.ArrayList;
import java.util.List;

public class WiFiDirectActivity extends AppCompatActivity {

    private IntentFilter mIntentFilter;

    private WifiP2pManager mManager;

    private WifiP2pManager.Channel mChannel;

    private WiFiDirectBroadcastReceiver mReceiver;

    private List<String> mConnectedPeers;

    private Button startButton;

    private TextView text;

    // First method being called, at the creation of the activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make sure Wifi is enabled on the Android terminal.
        WifiManager wifiManager  = (WifiManager)this.getApplicationContext().getSystemService(this.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        // TODO : call NTP methods ?

        this.mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        this.mChannel = mManager.initialize(this, getMainLooper(), null);

        mConnectedPeers = new ArrayList<>();

        this.mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();

        //  Indicates a change in the Wi-Fi P2P status.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        this.startButton = (Button) findViewById(R.id.startbutton);
        this.text = (TextView) findViewById(R.id.text);

        // Launch the discovery
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // if the discovery process detects peers, the system broadcasts the WIFI_P2P_PEERS_CHANGED_ACTION intent
            }

            @Override
            public void onFailure(int reasonCode) {

            }
        });
    }

    public List<String> getConnectedPeers() {
        return mConnectedPeers;
    }

    public void startButtonPressed() {
        for (String address : mConnectedPeers) {
            new StartSongTask().execute(); // TODO : thread
        }
    }

    public TextView getText() { return text; }

    // CTRL+C - CTRL+V from the tutorial... TODO : explain how this works
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
}
