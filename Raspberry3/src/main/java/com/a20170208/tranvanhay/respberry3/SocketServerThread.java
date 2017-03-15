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
    String message ="";
    ServerSocket serverSocket;
    SocketServerThread (ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
    int flameValue0 = 0, flameValue1 = 0, lux0 = 0, lux1 = 0, humiditySolid0 = 0,humiditySolid1 = 0;
    int temperature = 0, humidity = 0, lux = 0;
    float flameValue = 0, humditySolid = 0;
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(SocketServerPORT);
            mData.child("SocketServer").child("zNotify").setValue("IP:"+this.getIpAddress()+":"+serverSocket.getLocalPort());
            while (true) {
                count1 ++;
                count2 ++;
                if(count1 >=255){
                    count1 = 0;
                }
                Socket socket = serverSocket.accept();
                DataInputStream dIn = new DataInputStream(socket.getInputStream());
                // Read three value sent from ESP
                humidity = dIn.readUnsignedByte();
                temperature = dIn.readUnsignedByte();
                flameValue0 = dIn.readUnsignedByte();
                flameValue1 = dIn.readUnsignedByte();
                lux0 = dIn.readUnsignedByte();
                lux1 = dIn.readUnsignedByte();
                humiditySolid0 = dIn.readUnsignedByte();
                humiditySolid1 = dIn.readUnsignedByte();
                // Convert value
                flameValue = byteToInt(flameValue0,flameValue1);
                flameValue = 100 - (flameValue/1024)*100;
                lux = byteToInt(lux0,lux1);
                humditySolid = byteToInt(humiditySolid0,humiditySolid1);
                humditySolid = 100 - (humditySolid/1024)*100;
                // Send to Firebase
                message = "IP:" + socket.getInetAddress() + ",Counter1: " + count1+ ",Counter2: " + count2 +"             ";
                mData.child("SocketServer").child("Humidity").setValue(humidity+" %      ");
                mData.child("SocketServer").child("Temperature").setValue(temperature+" Celius        ");
                mData.child("SocketServer").child("Flame Sensor").setValue(flameValue+" %        ");
                mData.child("SocketServer").child("Light").setValue(lux+" lux        ");
                mData.child("SocketServer").child("Humidity Solid").setValue(humditySolid+" %         ");
                mData.child("SocketServer").child("zMessage").setValue(message);
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
    private int byteToInt(int byteZero, int byteOne){
        return byteZero+ byteOne*256;
    }
}


