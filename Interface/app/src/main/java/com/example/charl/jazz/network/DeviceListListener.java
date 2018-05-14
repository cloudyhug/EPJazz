package com.example.charl.jazz.network;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 21/03/18.
 */

public class DeviceListListener implements PeerListListener {
    private WifiP2pDevice device;
    private List<WifiP2pDevice> peers = new ArrayList<>();

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        peers.clear();
        peers.addAll(peerList.getDeviceList());
    }

    public List<WifiP2pDevice> getPeers() {
        return peers;
    }

    public void clearPeers() {
        peers.clear();
    }

    public void updateThisDevice(WifiP2pDevice device) {
        this.device = device;
    }
}