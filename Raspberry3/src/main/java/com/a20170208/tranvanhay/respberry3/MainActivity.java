package com.a20170208.tranvanhay.respberry3;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by Tran Van Hay on 3/3/2017.
 */

public class MainActivity extends Activity {
    // Instance for Realtime Database
    DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    // Instances for Authentication
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    // Instance for a handler
    private Handler mHandler = new Handler();
    // Instance for creation a Server Socket
    protected ServerSocket serverSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signIn();
        checkAccount();
        mHandler.post(showCurrentTime);
        Thread serverSocketThread = new Thread(new SocketServerThread(serverSocket));
        serverSocketThread.start();
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Used for shut down socket current running when app destroy
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private void signIn(){
        String email = "tranvanhay@gmail.com";
        String pass = "vanhay2020";
        mAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mData.child("Sign-in:").setValue("Success");
                        }
                        else{
                            mData.child("Sign-in:").setValue("Not success");
                        }
                    }
                });
    }
    private void checkAccount(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mData.child("Check account:").setValue("User existing:" + user.getEmail());
                } else {
                    mData.child("Check account:").setValue("User not existing");
                }
            }
        };
    }
    private Runnable showCurrentTime = new Runnable() {
        @Override
        public void run() {
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss --- dd/MM/yyyy");
                mData = FirebaseDatabase.getInstance().getReference();
                mData.child("At Current").setValue(format.format(date));
                mHandler.postDelayed(showCurrentTime,1000);
        }
    };
}