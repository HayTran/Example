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

import static android.R.id.message;

/**
 * Created by Tran Van Hay on 3/3/2017.
 */

public class SocketServerThread extends Thread {
    private DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    static final int SocketServerPORT = 8080;
    int count1 = 0, count2 = 0;
    ServerSocket serverSocket;
    SocketServerThread (ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
    private int flameValue0 = 0, flameValue1 = 0, lightIntensity0 = 0, lightIntensity1 = 0, humiditySolid0 = 0,humiditySolid1 = 0;
    private static int temperature = 0, humidity = 0, lightIntensity = 0;
    private static int flameValue = 0, humiditySolid = 0;
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
                lightIntensity0 = dIn.readUnsignedByte();
                lightIntensity1 = dIn.readUnsignedByte();
                humiditySolid0 = dIn.readUnsignedByte();
                humiditySolid1 = dIn.readUnsignedByte();
                // Convert value
                convertValue();
                // Send data to firebase
                //sendDataToFirebase(socket);
                new Firebase(socket,temperature,humidity,flameValue,humiditySolid,lightIntensity).sendDataToFirebase();
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
        flameValue = flameValue0 + flameValue1*256;
        flameValue = 100 - (flameValue/1024)*100;
        lightIntensity = lightIntensity0 + lightIntensity1*256;
        humiditySolid = humiditySolid0 + humiditySolid1*256;
        humiditySolid = 100 - (humiditySolid/1024)*100;
    }
    public void sendDataToFirebase(Socket socket){
        // Send to Firebase
        mData.child("SocketServer").child("Socket IP").setValue("Soket Server: " + socket.getInetAddress());
        mData.child("SocketServer").child("Temperature").setValue(temperature+" Celius        ");
        mData.child("SocketServer").child("Humidity").setValue(humidity+" %      ");
        mData.child("SocketServer").child("Flame").setValue(flameValue+" %        ");
        mData.child("SocketServer").child("Humidity Solid").setValue(humiditySolid+" %         ");
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
    public static int getFlameValue() {
        return flameValue;
    }
    public static int getHumidity() {
        return humidity;
    }
    public static int getHumiditySolid() {
        return humiditySolid;
    }
    public static int getLightIntensity() {
        return lightIntensity;
    }
    public static int getTemperature() {
        return temperature;
    }
}


