package com.example.lin.epnetworktest.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import com.example.lin.epnetworktest.model.Client;
import com.example.lin.epnetworktest.model.Server;
import com.example.lin.epnetworktest.view.WiFiDirectActivity;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by lin on 23/11/17.
 */

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WiFiDirectActivity activity;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager mManager;
    private WifiP2pManager.ActionListener actionListenerConnect;
    private WifiP2pManager.ActionListener actionListenerDiscover;
    private WifiP2pManager.PeerListListener mPeerListListener;
    private ArrayList<String> mConnectedPeers;
    private boolean initDone;
    private Client client;
    private Server server;

    public WiFiDirectBroadcastReceiver(WifiP2pManager mManager,
                                       WifiP2pManager.Channel mChannel,
                                       WiFiDirectActivity activity) {
        super();
        // The main activity
        this.activity = activity;

        // WifiP2pManager.Channel and WifiP2pManager : used for P2P between
        // the devices
        this.mChannel = mChannel;
        this.mManager = mManager;

        // ActionListener : to decide what to do when a new P2P connection happens
        actionListenerConnect = new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // TODO : do we need to do something here ?
            }

            @Override
            public void onFailure(int reason) {
                // TODO : manage the failure case
            }
        };

        // ActionListener : to decide what to do when we discover the peers
        actionListenerDiscover = new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank.
            }

            @Override
            public void onFailure(int reasonCode) {
                // Code for when the discovery initiation fails goes here.
                // TODO : alert the user that something went wrong ?
            }
        };

        // A list of the peers this device is connected to.
        mConnectedPeers = new ArrayList<>();

        // The init method has never been called yet.
        initDone = false;

        // PeerListListener : to decide what to do when a discovery starts.
        mPeerListListener = new WifiP2pManager.PeerListListener() {

            // This method is called everytime we request a peer list from
            // the WifiP2pManager object.
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                if (initDone)
                    // If init has been called once, the sockets are up and we can just
                    // make P2P connections.
                    checkAvailableConnections(peers);
                else
                    // Call the init method which starts the sockets and makes the P2P connections.
                    init(peers);
            }
        };

        // Sockets are not initialised yet.
        client = null;
        server = null;

        // Start the peer discovery

    }

    // This method determines if the terminal is going to be a client or the server.
    public void init(WifiP2pDeviceList peers) {
        if (activity.getConnectionInfo().isGroupOwner) {
            server = new Server(activity.getText());
            server.execute();
        } else {
            client = new Client(activity.getText(), activity.getConnectionInfo());
            client.execute();
        }

        /*
        if (peers.getDeviceList().size() == 0) {
            // No peers were found on the network yet, so this device becomes the leader.
            isLeader = true;
            server = new Server(); // TODO : add the correct arguments to the constructor
        } else {
            // Peers were found on the network. We need to check whether a device has
            // become the leader, or if there is no leader yet.
            for (WifiP2pDevice device : peers.getDeviceList()) {
                // TODO : ask each device if it is the leader (how ?)
            }
        }
        */
    }

    // Called by the button on the main activity to start the song.
    public void start() {
        // TODO : actually write what the method does
        mManager.discoverPeers(mChannel, actionListenerDiscover);
    }

    // Checks the available P2P connections in the peer list.
    // Called everytime the peer list changes.
    public void checkAvailableConnections(WifiP2pDeviceList peers) {
        // Iterating through the peer list
        for (WifiP2pDevice device : peers.getDeviceList()) {
            // If we are not yet connected to this device then connect to it
            // and add it to the connected peer list.
            if (!mConnectedPeers.contains(device.deviceAddress)) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                mManager.connect(mChannel, config, actionListenerConnect);
                mConnectedPeers.add(device.deviceAddress);
            }
        }
    }

    // Any Wifi P2P event will call this method.
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // If we have just been connected to / disconnected from the P2P network.
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // TODO : if P2P is already enabled, what to do here ?
            } else {
                // P2P has been disabled. We need to reactivate it.
                try {
                    // Make sure Wifi is enabled
                    WifiManager wifiManager  = (WifiManager) activity.getApplicationContext().getSystemService(activity.WIFI_SERVICE);
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

            // Connection state changed -> modifying the activity's info attribute.

            if (mManager == null)
                return;

            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                mManager.requestConnectionInfo(mChannel,
                        new WifiP2pManager.ConnectionInfoListener() {

                            @Override
                            public void onConnectionInfoAvailable(
                                    WifiP2pInfo info) {
                                if (info != null) {
                                    activity.setConnectionInfo(info); // When connection is established with other device, We can find that info from wifiP2pInfo here.
                                }
                            }
                        }

                );
            } else {
                activity.resetData(); // When connection lost then we can reset data about that connection.
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // TODO : identify what actually happened in order to get us here
        }
    }
}
