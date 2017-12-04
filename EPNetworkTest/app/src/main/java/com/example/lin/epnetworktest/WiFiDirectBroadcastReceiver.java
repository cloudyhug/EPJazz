package com.example.lin.epnetworktest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;

import java.util.ArrayList;

/**
 * Created by lin on 23/11/17.
 */

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WiFiDirectActivity activity;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager mManager;
    private WifiP2pManager.PeerListListener mPeerListListener;
    private ArrayList<String> connectedPeers;

    public WiFiDirectBroadcastReceiver(WifiP2pManager mManager,
                                       WifiP2pManager.Channel mChannel,
                                       WiFiDirectActivity activity) {
        super();
        this.activity = activity;
        this.mChannel = mChannel;
        this.mManager = mManager;
        this.connectedPeers = new ArrayList<>();
        this.mPeerListListener = new WifiP2pManager.PeerListListener() {

            // This method is called everytime we request a peer list from
            // the WifiP2pManager object.
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                checkAvailableConnections(peers);
            }
        };
    }

    // Checks the available connections in the peer list.
    public void checkAvailableConnections(WifiP2pDeviceList peers) {
        if (peers.getDeviceList().size() == 0) {
            // No peers found on the network yet -> become the leader

        }

        // Iterating through the peer list
        for (WifiP2pDevice device : peers.getDeviceList()) {
            // If we are not yet connected to this device
            if (!connectedPeers.contains(device.deviceAddress)) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        // launch socket connection with peer ?

                    }

                    @Override
                    public void onFailure(int reason) {
                        // failure
                    }
                });
                connectedPeers.add(device.deviceAddress);
                startSocketConnection(device.deviceName);
            }
        }
    }

    public void startSocketConnection(String hostname) {

    }

    // Any Wifi P2P event will call this method.
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                activity.setIsWifiP2pEnabled(true);
            } else {
                activity.setIsWifiP2pEnabled(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // The peer list has changed!  We should probably do something about
            // that.

            // Request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (mManager != null) {
                mManager.requestPeers(mChannel, mPeerListListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed!  We should probably do something about
            // that.

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // ?
        }
    }
}
