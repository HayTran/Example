package com.a20170208.tranvanhay.respberry3;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Tran Van Hay on 3/3/2017.
 */

public class SocketServerThread extends Thread {
    private DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    static final int SocketServerPORT = 8080;
    int count1 = 0, count2 = 0;
    String message1 = "", message2="";
    ServerSocket serverSocket;
    SocketServerThread (ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
    int A = 0 , B = 0 , C = 0;
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(SocketServerPORT);
            mData.child("SocketServer").child("Notify").setValue("IP:"+this.getIpAddress()+":"+serverSocket.getLocalPort());
            while (true) {
                count1 ++;
                count2 ++;
                if(count1 >=255){
                    count1 = 0;
                }
                Socket socket = serverSocket.accept();
                DataInputStream dIn = new DataInputStream(socket.getInputStream());
                if(dIn.available() == 0) {
                    // Read three value sent from ESP
                    A = dIn.readByte();
                    B = dIn.readByte();
                    C = dIn.readByte();
                    // Convert signed byte into integer
                    if(A < 0){
                        int tmp = A & 0x7f;
                        A = tmp + 128;
                    }
                    if(B < 0){
                        int tmp = B & 0x7f;
                        B = tmp + 128;
                    }
                    if(C < 0){
                        int tmp = C & 0x7f;
                        C = tmp + 128;
                    }
                    message1 = "A=" + A + ",B=" + B + ",C=" + C;
                    message2 = "IP:" + socket.getInetAddress() + ",Counter1: " + count1+ ",Counter2: " + count2;
                    mData.child("SocketServer").child("Message1").setValue(message1);
                    mData.child("SocketServer").child("Message2").setValue(message2);
                }
                else{
                    dIn.close();
                    socket.close();
                }
                // Initialize a SocketServerReplyThread object
                SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                        socket, count1);
                // Start running Server Reply Thread
                socketServerReplyThread.run();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            mData.child("Error").setValue(e.toString());
        }
    }
    // Get Server's IP waiting socket coming
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
                        ip += ""
                                + inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            mData.child("Error").setValue(e.toString());
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }
    // ReplyThreadFromServer Class
    class SocketServerReplyThread extends Thread {
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
                DataOutputStream dOut = new DataOutputStream(hostThreadSocket.getOutputStream());
                if(!hostThreadSocket.isClosed() || !hostThreadSocket.isConnected() || !hostThreadSocket.isInputShutdown()) {
                    dOut.writeByte(cnt);
                    dOut.flush();
                }
                dOut.close();
                hostThreadSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                mData.child("Error").setValue(e.toString());
            }
        }
    }
}


