package com.a20170208.tranvanhay.respberry3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Tran Van Hay on 3/24/2017.
 */

public class StartUpActivity extends Activity {
    private static final String TAG = StartUpActivity.class.getSimpleName();
    // Instance for Realtime Database
    DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    // Instances for Authentication
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logIn();
        checkStepStartUpActivity();
        setContentView(R.layout.activity_start_up);
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
    /**
     * These method for authentication
     */
    private void logIn(){
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
    private void checkStepStartUpActivity(){
        checkAccount();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(TAG,"CheckStep: User = " + user.getUid());
            Intent intent = new Intent(StartUpActivity.this,OperatingActivity.class);
            startActivity(intent);
        } else {
            Log.d(TAG,"CheckStep: Not user");
        }
    }
}