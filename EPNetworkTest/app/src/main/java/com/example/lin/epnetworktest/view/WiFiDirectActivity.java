package com.example.lin.epnetworktest.view;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.lin.epnetworktest.R;
import com.example.lin.epnetworktest.controller.WiFiDirectBroadcastReceiver;

public class WiFiDirectActivity extends AppCompatActivity {

    private IntentFilter mIntentFilter;

    private WifiP2pManager mManager;

    private WifiP2pManager.Channel mChannel;

    private WiFiDirectBroadcastReceiver mReceiver;

    private Button startButton;

    // First method being called, at the creation of the activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIntentFilter = new IntentFilter();

        //  Indicates a change in the Wi-Fi P2P status.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        // Make sure Wifi is enabled on the Android terminal.
        WifiManager wifiManager  = (WifiManager)this.getApplicationContext().getSystemService(this.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        this.mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        this.mChannel = mManager.initialize(this, getMainLooper(), null);
        this.mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        this.startButton = (Button) findViewById(R.id.startbutton);
    }

    // Called everytime the start button is pressed.
    public void startP2p(View v) {
        // TODO :
        mReceiver.discover();
    }

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
