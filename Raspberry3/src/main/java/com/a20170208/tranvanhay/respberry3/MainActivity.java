package com.a20170208.tranvanhay.respberry3;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

/**
 * Created by Tran Van Hay on 3/3/2017.
 */

public class MainActivity extends Activity {
    DatabaseReference mData;
    private Handler mHandler = new Handler();
    private Gpio mLedGpio;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int INTERVAL_BETWEEN_BLINKS_MS = 1000;
    TextView textView;

    //Power off the device
    PowerManager pm;

    //Authenication
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    // Firebase Instance ID
    private FirebaseInstanceId firebaseInstanceId;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        textView.setText("Xin chào ");
        ConnectToFirebase.initConnection();
        mData = FirebaseDatabase.getInstance().getReference();

        // Power off the device
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mData.child("Turn off:").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString() == "true"){
                    pm.reboot("null");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Controlling I/O
        PeripheralManagerService service = new PeripheralManagerService();
        try {
            String pinName = BoardDefaults.getGPIOForLED();
            mLedGpio = service.openGpio(pinName);
            mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            Log.i(TAG, "Start blinking LED GPIO pin");
            // Post a Runnable that continuously switch the state of the GPIO, blinking the
            // corresponding LED
            mData.child("LED").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue().equals("Yes button")) {
                            mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
                        } else {
                            mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error on PeripheralIO API", e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mHandler.post(showCurrentTime);
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }

        //Authenication
        // Initial mAuth for Authenication methods
        mAuth = FirebaseAuth.getInstance();
       // mAuth.signOut();
        // Listen user
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

        // Sign in
        signIn();

        // Sign up
        //signUp();

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

    @Override
    public void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove pending blink Runnable from the handler.
        mHandler.removeCallbacks(mBlinkRunnable);
        // Close the Gpio pin.
        Log.i(TAG, "Closing LED GPIO pin");
        try {
            mLedGpio.close();
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        } finally {
            mLedGpio = null;
        }
    }

    private Runnable mBlinkRunnable = new Runnable() {
        @Override
        public void run() {
            // Exit Runnable if the GPIO is already closed
            if (mLedGpio == null) {
                return;
            }
            try {
                // Toggle the GPIO state
                mLedGpio.setValue(!mLedGpio.getValue());
                Log.d(TAG, "State set to " + mLedGpio.getValue());
                // Reschedule the same runnable in {#INTERVAL_BETWEEN_BLINKS_MS} milliseconds
                mHandler.postDelayed(mBlinkRunnable, INTERVAL_BETWEEN_BLINKS_MS);
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };
    private Runnable showCurrentTime = new Runnable() {
        @Override
        public void run() {
            ConnectToFirebase.atCurrent();
            mHandler.postDelayed(showCurrentTime, INTERVAL_BETWEEN_BLINKS_MS);
        }
    };

    // Control led whether it is turned off or turn on
    private void controlLed(boolean ledState) {
        if (mLedGpio == null) {
            return;
        }
        try {
            // Toggle the GPIO state
            mLedGpio.setValue(ledState);
            Log.d(TAG, "State set to " + ledState);
            // Reschedule the same runnable in {#INTERVAL_BETWEEN_BLINKS_MS} milliseconds
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }
    }

}
