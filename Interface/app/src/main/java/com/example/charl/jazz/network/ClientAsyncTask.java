package com.example.charl.jazz.network;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by lin on 25/03/18.
 */

public class ClientAsyncTask extends AsyncTask<Void, Void, String> {
    private WiFiDirectActivity activity;
    private InetAddress serverAddress;

    public ClientAsyncTask(WiFiDirectActivity activity, InetAddress serverAddress) {
        this.serverAddress = serverAddress;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Void... args) {
        try {
            Socket socket = new Socket();
            socket.bind(null);
            socket.connect(new InetSocketAddress(serverAddress, 42042));

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream out = new PrintStream(socket.getOutputStream());

            out.println("time?");
            out.flush();

            for (String inputLine; (inputLine = in.readLine()) != null; ) {
                if (inputLine.startsWith("time:"))
                    return inputLine.substring(5);
            }
            socket.close();
        } catch (IOException e) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        String[] times = result.split(" ");
        long t1 = Long.valueOf(times[0]);
        long tStart = Long.valueOf(times[1]);
        activity.terminate(tStart - t1);
    }
}