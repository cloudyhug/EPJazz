package com.example.charl.jazz.network;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;
import android.widget.Toast;

public class DeviceConnectionInfoListener implements ConnectionInfoListener {
    private WiFiDirectActivity activity;
    private WifiP2pInfo info;

    private boolean serverStarted;

    public DeviceConnectionInfoListener(WiFiDirectActivity activity) {
        this.activity = activity;
        serverStarted = false;
    }

    public WifiP2pInfo getInfo() {
        return info;
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        // The owner IP is now known.
        this.info = info;

        // After the group negotiation, we assign the group owner as the server.
        if (info.groupFormed && info.isGroupOwner && !serverStarted) {
            activity.setDebugText("we are leader");
            long time = System.currentTimeMillis();
            long startingTime = time + 15000;
            activity.setTime(time);
            activity.setStartingTime(startingTime);
            activity.setIsTerminateServerButtonEnabled(true);
            activity.setIsGetTimeButtonEnabled(false);
            new ServerAsyncTask(time, startingTime).execute();
            serverStarted = true;
        } else if (info.groupFormed) {
            activity.setDebugText("we are not leader");
            // The other device acts as the client
        }
    }
}