package com.a20170208.tranvanhay.respberry3;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Tran Van Hay on 3/14/2017.
 */

// ReplyThreadFromServer Class
public class SocketServerReplyThread extends Thread {
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
//            mData.child("Error").setValue(e.toString());
        }
    }
}
