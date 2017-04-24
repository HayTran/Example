package com.a20170208.tranvanhay.appat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class DisplayActivity extends AppCompatActivity {
    private static final String TAG = "DisplayActivity";
    DatabaseReference mData;
    TextView textViewTime,textViewFlame0,textViewFlame1,textViewHumidity,textViewTemperature,textViewLightIntensity,textViewMQ2,textViewMQ7;
    ImageView imageView;
    Bitmap bitmap;
    Button btnChangeToPingActivity, btnSignOut,btnCheckFCM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        mapping();
        init();
        addControl();
    }
    private void mapping() {
        textViewMQ2 = (TextView)findViewById(R.id.textViewMQ2DisplayActivity);
        textViewMQ7 = (TextView)findViewById(R.id.textViewMQ7DisplayActivity);
        textViewTime = (TextView)findViewById(R.id.textViewTimeDisplayActivity);
        textViewFlame0 = (TextView)findViewById(R.id.textViewFlame0DisplayActivity);
        textViewFlame1 = (TextView)findViewById(R.id.textViewFlame1DisplayActivity);
        textViewHumidity = (TextView)findViewById(R.id.textViewHumidityDisplayActivity);
        textViewTemperature = (TextView)findViewById(R.id.textViewTemperatureDisplayActivity);
        textViewLightIntensity = (TextView)findViewById(R.id.textViewLightIntensityDisplayActivity);
        imageView = (ImageView)findViewById(R.id.imageViewDisplayActivity);
        btnChangeToPingActivity = (Button)findViewById(R.id.btnChangeToPingActivityDisplayActivity);
        btnSignOut = (Button)findViewById(R.id.btnSignOutDisplayActivity);
        btnCheckFCM = (Button)findViewById(R.id.btnCheckFCMDisplayActivity);
    }
    private void init() {
        // Define instance for firebase connection
        mData = FirebaseDatabase.getInstance().getReference();
    }
    private void addControl() {
        showInfoFromFirebase();
        checkImageStorage();
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Log.d(TAG,"Display Activity Change To Sign In Activity");
                Intent intent = new Intent(DisplayActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });
        btnChangeToPingActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"Display Activity Change To Ping Activity");
                Intent intent = new Intent(DisplayActivity.this,PingActivity.class);
                startActivity(intent);
            }
        });
        btnCheckFCM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FCMServerThread("Testing from App AT").start();
            }
        });
    }
    private void showInfoFromFirebase() {
        mData.child("At Current").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textViewTime.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkImageStorage() {
        mData.child("Storage Image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String pathImage = dataSnapshot.getValue().toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new LoadImage().execute(pathImage);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // listen when Socket Server has change value then set new value
        mData.child("SocketServer").child("Temperature").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textViewTemperature.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mData.child("SocketServer").child("Humidity").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textViewHumidity.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mData.child("SocketServer").child("Flame 0").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textViewFlame0.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mData.child("SocketServer").child("Flame 1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textViewFlame1.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mData.child("SocketServer").child("Light Intensity").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textViewLightIntensity.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mData.child("SocketServer").child("MQ2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textViewMQ2.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mData.child("SocketServer").child("MQ7").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textViewMQ7.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mData.child("At Current").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textViewTime.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private class LoadImage extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                //Toast.makeText(SignInActivity.this, "URL= " + strings[0], Toast.LENGTH_SHORT).show();
            } catch (MalformedURLException e) {
                Log.d("LoadImage", "MaformedURL " + e.getMessage());
            } catch (IOException e) {
                Log.d("LoadImage", "IOException " + e.getMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            imageView.setImageBitmap(bitmap);
            //  Toast.makeText(SignInActivity.this, "Load finish", Toast.LENGTH_SHORT).show();
        }
    }
}