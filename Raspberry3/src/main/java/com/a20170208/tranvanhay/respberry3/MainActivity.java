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
    DatabaseReference mData;
    private Handler mHandler = new Handler();
    //Authenication
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    ServerSocket serverSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        signIn();
        checkAccount();
        mHandler.post(showCurrentTime);
        Thread serverSocketThread = new Thread(new SocketServerListeningThread(serverSocket));
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
        // Used for shut down socket current running
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
                            mData.child("Sign-in:").push().setValue("Success");
                        }
                        else{
                            mData.child("Sign-in:").push().setValue("Not success");
                        }
                    }
                });
    }
    private void signUp() {
        String email = "abcd@gmail.com";
        String password = "vanhay2021";
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mData.child("Sign-up:").push().setValue("Create a new user successfully");
                        }else {
                            mData.child("Sign-up:").push().setValue("Can not create a new user");
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
                    mData.child("Sign-in:").push().setValue("User existing:" + user.getEmail());
                } else {
                    mData.child("Sign-in:").push().setValue("User not existing");
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