package com.example.lin.epnetworktest.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;

import com.example.lin.epnetworktest.view.WiFiDirectActivity;

import java.lang.reflect.Method;

/**
 * Created by lin on 23/11/17.
 */

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WiFiDirectActivity activity;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager mManager;

    private WifiP2pManager.PeerListListener mPeerListListener;

    public WiFiDirectBroadcastReceiver(final WifiP2pManager mManager,
                                       final WifiP2pManager.Channel mChannel,
                                       final WiFiDirectActivity activity) {
        super();
        // The main activity
        this.activity = activity;

        // WifiP2pManager.Channel and WifiP2pManager : used for P2P between
        // the devices
        this.mChannel = mChannel;
        this.mManager = mManager;

        mPeerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                for (WifiP2pDevice device : peers.getDeviceList()) {
                    if (activity.getConnectedPeers().contains(device.deviceAddress))
                        continue; // already connected to this device

                    WifiP2pConfig config = new WifiP2pConfig();
                    config.deviceAddress = device.deviceAddress;

                    mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            // successful connection to the device
                        }

                        @Override
                        public void onFailure(int reason) {
                            // failure
                        }
                    });
                    activity.getConnectedPeers().add(device.deviceAddress); // conditional execution ?
                }
            }
        };
    }

    // Any Wifi P2P event will call this method.
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // If we have just been connected to / disconnected from the P2P network.
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state != WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is disabled. We need to activate it.
                try {
                    // Make sure Wifi is enabled
                    WifiManager wifiManager  = (WifiManager) activity.getApplicationContext().getSystemService(WiFiDirectActivity.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(true);

                    // Enable Wifi P2P
                    Method method1 = mManager.getClass().getMethod("enableP2p", WifiP2pManager.Channel.class);
                    method1.invoke(mManager, mChannel);
                } catch (Exception e) {
                    // Could not activate either Wifi or P2P modes.
                    // TODO : manage the exception ?
                }
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // The peer list has changed.

            // Request available peers from the WifiP2pManager
            // This method makes a callback on PeerListListener.onPeersAvailable()
            if (mManager != null) {
                mManager.requestPeers(mChannel, mPeerListListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // TODO : new connections or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // TODO : identify what actually happened in order to get us here
        }
    }
}
