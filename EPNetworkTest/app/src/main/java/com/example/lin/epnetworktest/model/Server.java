package com.example.lin.epnetworktest.model;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by lin on 08/12/17.
 */

public class Server extends AsyncTask<Void, Void, Void> {
    private TextView textView;
    private ServerSocket serverSocket;
    private Socket socket;

    public Server(TextView textView) {
        this.textView = textView;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            serverSocket = new ServerSocket(8000);
            socket = serverSocket.accept();
            // TODO : send data to clients
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
