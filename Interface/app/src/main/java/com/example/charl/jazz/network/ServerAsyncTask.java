package com.example.charl.jazz.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by lin on 25/03/18.
 */

public class ServerAsyncTask extends AsyncTask<Void, Void, Void> {
    private long tStart;

    public ServerAsyncTask(long ts) {
        tStart = ts;
    }

    @Override
    protected Void doInBackground(Void... args) {
        try {
            ServerSocket serverSocket = new ServerSocket(42042);
            while (true) {
                Socket socket = serverSocket.accept();
                long t1 = System.currentTimeMillis();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintStream out = new PrintStream(socket.getOutputStream());
                for (String inputLine; (inputLine = in.readLine()) != null; ) {
                    if (inputLine.equals("time?")) {
                        out.println("time:" + t1 + " " + tStart);
                        out.flush();
                    }
                }
                socket.close();
            }
        } catch (IOException e) {
        }
        return null;
    }
}