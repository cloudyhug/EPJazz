package ep.net;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;

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

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner && !serverStarted) {
            // TODO : changer ServerAsyncTask pour donner (t, t_depart) au lieu d'un filename
            new ServerAsyncTask("filename").execute();
            serverStarted = true;
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case, we enable the
            // get file button.
            // TODO : changer bouton pour récupérer (t, t_depart)
            activity.enableGetFilenameButton();
        }
    }
}