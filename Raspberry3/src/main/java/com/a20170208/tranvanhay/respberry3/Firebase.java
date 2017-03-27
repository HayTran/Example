package com.a20170208.tranvanhay.respberry3;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.Socket;

import static android.R.id.message;

/**
 * Created by Tran Van Hay on 3/24/2017.
 */

public class Firebase {
    DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    // Instances for Authentication
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private int temperature, humidity, lightIntensity, flameValue, humiditySolid;
    private Socket socket;
    public Firebase() {
    }
    public Firebase(Socket socket, int temperature, int humidity, int flameValue, int humiditySolid, int lightIntensity) {
        this.flameValue = flameValue;
        this.humidity = humidity;
        this.humiditySolid = humiditySolid;
        this.lightIntensity = lightIntensity;
        this.temperature = temperature;
        this.socket = socket;
    }
    public void sendDataToFirebase(){
        // Send to Firebase
        mData.child("SocketServer").child("Socket IP").setValue("Soket Server: " + socket.getInetAddress());
        mData.child("SocketServer").child("Temperature").setValue(temperature+" Celius        ");
        mData.child("SocketServer").child("Humidity").setValue(humidity+" %      ");
        mData.child("SocketServer").child("Flame").setValue(flameValue+" %        ");
        mData.child("SocketServer").child("Humidity Solid").setValue(humiditySolid+" %         ");
        mData.child("SocketServer").child("Light Intensity").setValue(lightIntensity+" lux        ");
        mData.child("SocketServer").child("zMessage").setValue(message);
    }
    public void sendTimestampToFirebase(String dateAndTime){
        mData.child("At Current").setValue(dateAndTime);
    }
}
