package com.a20170208.tranvanhay.respberry3;

import android.util.Log;

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
import java.util.Formatter;

import static android.R.id.message;

/**
 * Created by Tran Van Hay on 3/3/2017.
 */

public class SocketServerThread extends Thread {
    private static final String TAG = "SockerServerThread";
    private DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    static final int SocketServerPORT = 8080;
    int count1 = 0, count2 = 0;
    ServerSocket serverSocket;
    SocketServerThread (ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
    private static int temperature = 0, humidity = 0;
    private int flameValue0_0 = 0, flameValue0_1 = 0, lightIntensity0 = 0, lightIntensity1 = 0,flameValue1_0 = 0, flameValue1_1 = 0;
    private int mq2Value0 = 0, mq2Value1 = 0, mq7Value0 = 0, mq7Value1 = 0;
    private static double flameValue0 = 0, flameValue1 = 0, lightIntensity = 0, mq2Value = 0, mq7Value = 0;
    private byte [] macAddr = new byte[10];
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
                Log.d("SocketServerThread","run() before serverSocket.accept()");
                Socket socket = serverSocket.accept();
                Log.d("SocketServerThread","run() after serverSocket.accept()");
                DataInputStream dIn = new DataInputStream(socket.getInputStream());
                // Read three value sent from ESP
                humidity = dIn.readUnsignedByte();
                temperature = dIn.readUnsignedByte();
                flameValue0_0 = dIn.readUnsignedByte();
                flameValue0_1 = dIn.readUnsignedByte();
                flameValue1_0 = dIn.readUnsignedByte();
                flameValue1_1 = dIn.readUnsignedByte();
                lightIntensity0 = dIn.readUnsignedByte();
                lightIntensity1 = dIn.readUnsignedByte();
                mq2Value0 = dIn.readUnsignedByte();
                mq2Value1 = dIn.readUnsignedByte();
                mq7Value0 = dIn.readUnsignedByte();
                mq7Value1 = dIn.readUnsignedByte();
                for (int i = 5 ; i >= 0 ; i--){
                    macAddr[i] = dIn.readByte();
                }
                Log.d(TAG,"MAC Addr:"+byteToHex(macAddr).toString());
                // Convert value
                convertValue();
                sendDataToFirebase(socket);
                //new Firebase(socket,temperature,humidity,flameValue,humiditySolid,lightIntensity).sendDataToFirebase();
                // Initialize a SocketServerReplyThread object
                SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                        socket, count1);
                // Start running Server Reply Thread
                socketServerReplyThread.run();
                //Log MAC address of a node

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            mData.child("Error").setValue(e.toString());
        }
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
    private void convertValue(){
        flameValue0 = flameValue0_0 + flameValue0_1*256;
        flameValue0 = 100 - (flameValue0/1024)*100;
        flameValue1 = flameValue1_0 + flameValue1_1*256;
        flameValue1 = 100 - (flameValue1/1024)*100;
        lightIntensity = lightIntensity0 + lightIntensity1*256;
        mq2Value = mq2Value0 + mq2Value1*256;
        mq7Value = mq7Value0 + mq7Value1*256;
    }
    public void sendDataToFirebase(Socket socket){
        // Send to Firebase
        mData.child("SocketServer").child("Socket IP").setValue("Soket Server: " + socket.getInetAddress());
        mData.child("SocketServer").child("Temperature").setValue(temperature+" Celius        ");
        mData.child("SocketServer").child("Humidity").setValue(humidity+" %      ");
        mData.child("SocketServer").child("Flame 0").setValue(flameValue0+" %        ");
        mData.child("SocketServer").child("Flame 1").setValue(flameValue1+" %         ");
        mData.child("SocketServer").child("MQ2").setValue(mq2Value+"           ");
        mData.child("SocketServer").child("MQ7").setValue(mq7Value+"           ");
        mData.child("SocketServer").child("Light Intensity").setValue(lightIntensity+" lux        ");
        mData.child("SocketServer").child("zMessage").setValue(message);
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
    String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (int i = 0; i <= 5 ; i++)
        {
            formatter.format("%02x", hash[i]);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}


