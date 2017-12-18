package com.example.lin.epnetworktest.model;

import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by lin on 04/12/17.
 */

public class Client extends AsyncTask<Void, Void, Void> {
    private TextView textView;
    private WifiP2pInfo info;
    private Socket socket;

    public Client(TextView textView, WifiP2pInfo info) {
        this.textView = textView;
        this.info = info;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        try {
            socket = new Socket();
            socket.bind(null);
            int retry = 10;
            do {
                socket.connect((new InetSocketAddress(info.groupOwnerAddress, 8000)), 500);
                retry--;
            } while (!socket.isConnected() && retry > 0);
            // TODO : get data
        } catch (IOException e) {
            // TODO : manage error
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // TODO : textView.setText(response);
        super.onPostExecute(result);
    }

}