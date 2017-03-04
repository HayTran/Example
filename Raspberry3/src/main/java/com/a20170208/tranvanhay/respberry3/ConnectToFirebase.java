package com.a20170208.tranvanhay.respberry3;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by Tran Van Hay on 3/3/2017.
 */

public class ConnectToFirebase {
    private static  DatabaseReference mData;
    protected static void initConnection(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss --- dd/MM/yyyy");
        mData = FirebaseDatabase.getInstance().getReference();
        mData.child("Start up time").push().setValue(format.format(date));
    }
    protected  static void atCurrent(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss --- dd/MM/yyyy");
        mData = FirebaseDatabase.getInstance().getReference();
        mData.child("At Current").setValue(format.format(date));
    }
}
