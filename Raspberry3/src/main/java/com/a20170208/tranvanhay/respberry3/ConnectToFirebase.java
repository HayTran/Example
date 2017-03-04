package com.a20170208.tranvanhay.respberry3;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by Tran Van Hay on 3/3/2017.
 */
// This class to define if Android Things working or not.
// Moreover, this make Pi is not trapped in a earlier installed application.

public class ConnectToFirebase {
    private static  DatabaseReference mData;
    // This static method to inform over database in Firebase time when Android Things turned on.
    protected static void initConnection(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss --- dd/MM/yyyy");
        mData = FirebaseDatabase.getInstance().getReference();
        mData.child("Start up time").push().setValue(format.format(date));
    }

    // This static method to inform over database in Firebase current time
    // to specify whether or not Android Thing are working properly.
    protected  static void atCurrent(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss --- dd/MM/yyyy");
        mData = FirebaseDatabase.getInstance().getReference();
        mData.child("At Current").setValue(format.format(date));
    }
}
