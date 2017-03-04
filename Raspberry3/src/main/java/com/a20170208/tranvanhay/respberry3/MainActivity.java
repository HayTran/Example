package com.a20170208.tranvanhay.respberry3;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectToFirebase.initConnection();
        mData = FirebaseDatabase.getInstance().getReference();
        textView = (TextView)findViewById(R.id.textView);
        textView.setText("Xin chào ");
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
                    try{
                        if(dataSnapshot.getValue().equals("Yes button")){
                            mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
                        }
                        else {
                            mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
                        }
                    }catch (IOException e) {
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
    private  Runnable showCurrentTime = new Runnable() {
        @Override
        public void run() {
            ConnectToFirebase.atCurrent();
            mHandler.postDelayed(showCurrentTime, INTERVAL_BETWEEN_BLINKS_MS);
        }
    };
}
