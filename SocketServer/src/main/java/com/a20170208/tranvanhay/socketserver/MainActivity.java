package com.a20170208.tranvanhay.socketserver;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends Activity {
    TextView info, infoip, msg;
    String message = "";
    ServerSocket serverSocket;
    EditText editText;

    // Variables to send data to client
    Byte A = 0;
    Byte B = 0;
    String C = "";

    // onCreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Mapped in Layout
        info = (TextView) findViewById(R.id.info);
        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);
        editText = (EditText)findViewById(R.id.editText);

        // Get Server's IP
        infoip.setText(getIpAddress());

        // Initialize socket thread
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    // onDestroy method
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // Socket server thread
    private class SocketServerThread extends Thread {
        // Static variable for listening port
        // Variable count show the number connection to server
        static final int SocketServerPORT = 8080;
        int count = 0;
        @Override
        public void run() {
            try {
                // Intialize a server socket in port 8080
                serverSocket = new ServerSocket(SocketServerPORT);
                // Do update UI in MainThread, because updating UI must be implement in MainThread
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        info.setText("I'm waiting here: " + serverSocket.getLocalPort());
                    }
                });
                while (true) {
                    Socket socket = serverSocket.accept();
                    // Increase count variable
                    count++;
                    // Read data from Client
                    if(!socket.isClosed() || !socket.isConnected() || !socket.isInputShutdown()) {
                        DataInputStream dIn = new DataInputStream(socket.getInputStream());
                        A = dIn.readByte();
                        B = dIn.readByte();
                        message = "A = " + A + "\nB = " + B;
                        message += "\nPORT: " + socket.getPort() + "\nCounter: " + count;
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Update msg in UI Thread/ Main Thread
                                msg.setText(message);
                            }
                        });
                    }
                    else{
                        socket.close();
                    }
                    // Initialize a SocketServerReplyThread object
                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                            socket, count);
                    // Start running Server Reply Thread
                    socketServerReplyThread.run();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    // ReplyThreadFromServer Class
    private class SocketServerReplyThread extends Thread {
        private Socket hostThreadSocket;  //this object specify whether this socket of which host
        int cnt;
        SocketServerReplyThread(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
        }
        @Override
        public void run() {
            // Create a message to Client's socket
            try {
                if(!hostThreadSocket.isClosed() || !hostThreadSocket.isConnected() || !hostThreadSocket.isInputShutdown()) {
                    DataOutputStream dOut = new DataOutputStream(hostThreadSocket.getOutputStream());
                    dOut.writeByte(cnt);
                    dOut.flush();
                    dOut.close();
                }
                hostThreadSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                message += "Something wrong when sending reply to client! " + e.toString() + "\n";
            }
            // Updating UI must be implemented in UI Thread
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    msg.setText(message);
                }
            });
        }
    }

    // Method in MainActivity Class used to get Server's IP
    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }
}