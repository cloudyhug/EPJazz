package com.example.lin.epnetworktest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity2 extends AppCompatActivity {

    private Switch s;
    private Button bc, bsr;
    private TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        s = (Switch) findViewById(R.id.switch1);
        bc = (Button) findViewById(R.id.buttonConnect);
        bsr = (Button) findViewById(R.id.buttonSR);
        t = (TextView) findViewById(R.id.textView);

        bc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ServerSocket server = new ServerSocket(9999);
                    Socket serviceSocket = server.accept();
                    DataInputStream sinput = new DataInputStream(serviceSocket.getInputStream());
                    DataOutputStream soutput = new DataOutputStream(serviceSocket.getOutputStream());

                    Socket client = new Socket(InetAddress.getLocalHost(), 9999);
                    DataInputStream cinput = new DataInputStream(client.getInputStream());
                    DataOutputStream coutput = new DataOutputStream(client.getOutputStream());

                    System.out.println("Coucou");

                    soutput.close();
                    sinput.close();
                    serviceSocket.close();
                    server.close();

                    coutput.close();
                    cinput.close();
                    client.close();
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        });
    }


}
