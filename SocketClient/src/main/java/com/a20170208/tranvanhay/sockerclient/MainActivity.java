package com.a20170208.tranvanhay.sockerclient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends Activity {
    Handler mHandler = new Handler();
    String message = "";
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;
    TextView msg;
    byte hay = 0, nhung = 127;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(connectToServer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Mapped the variable in activity
        editTextAddress = (EditText)findViewById(R.id.address);
        editTextPort = (EditText)findViewById(R.id.port);
        buttonConnect = (Button)findViewById(R.id.connect);
        buttonClear = (Button)findViewById(R.id.clear);
        msg = (TextView)findViewById(R.id.msg);

        buttonClear.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                msg.setText(" ");
            }});
        //Register a thread
        mHandler.post(connectToServer);
    }

    // Do connect to server automactically
    private Runnable connectToServer = new Runnable() {
        @Override
        public void run() {
            // Begin connecting to server, intialize a object MyClientTask
            MyClientTask myClientTask = new MyClientTask(
                    editTextAddress.getText().toString(),
                    Integer.parseInt(editTextPort.getText().toString()));
            myClientTask.execute();
            mHandler.postDelayed(connectToServer,50);
        }
    };

    // My Client Task Class doing in AsynTask mode to get data from Server
    public class MyClientTask extends AsyncTask<Void, Void, Void> {
        String dstAddress;
        int dstPort;
        String response = "";
        MyClientTask(String addr, int port){
            dstAddress = addr;
            dstPort = port;
        }
        // Override the doInBackground AsynTask's method
        @Override
        protected Void doInBackground(Void... arg0) {
            Socket socket = null;
            try {
                hay++;
                if(hay >= 127){
                    hay = 0;
                }
                nhung --;
                if(nhung <= 0){
                    nhung = 127;
                }
                // Intialize a socket with Address and Port belong to Server.
                socket = new Socket(dstAddress, dstPort);
                //Createa stream to send data to server
                DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
                dOut.writeByte(hay);
                dOut.flush();
                dOut.writeByte(nhung);
                dOut.flush();
                message = "hay = " + hay + "\nnhung = "+nhung;
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msg.setText(message);
                    }
                });
                // Receive data sent from server
                SocketClientReceiveThread socketClientReceiveThread = new SocketClientReceiveThread(socket);
                socketClientReceiveThread.run();
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }finally{
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
        // Override the doInBackground AsynTask's method
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
    private class SocketClientReceiveThread extends Thread {
        private Socket hostThreadSocket;  //this object specify whether this socket of which host
        SocketClientReceiveThread(Socket socket) {
            hostThreadSocket = socket;
        }
        @Override
        public void run() {
            // Create a message to Client's socket
            try {
                DataInputStream dIn = new DataInputStream(hostThreadSocket.getInputStream());
                message += "The number received from server: "+ dIn.read();
                // Updating UI must be implemented in UI Thread
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msg.setText(message);
                    }
                });
                dIn.close();
                hostThreadSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                message += "Something wrong when receive reply from server " + e.toString() + "\n";
                // Updating UI must be implemented in UI Thread
            }
            // Update into activity once again
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    msg.setText(message);
                }
            });
        }
    }
}