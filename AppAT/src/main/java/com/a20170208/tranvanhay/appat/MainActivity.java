package com.a20170208.tranvanhay.appat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    DatabaseReference mData;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    TextView textViewTimeAndDate, textViewLink;
    ImageView imageView;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapping();
        logIn();
        checkAccount();
        showTimeAndDate();
        checkStorage();
    }
    private class LoadImage extends AsyncTask<String, Integer,String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL  url = new URL(strings[0]);
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
               //Toast.makeText(MainActivity.this, "URL= " + strings[0], Toast.LENGTH_SHORT).show();
            } catch (MalformedURLException e) {
                Log.d("LoadImage","MaformedURL " + e.getMessage());
            } catch (IOException e) {
                Log.d("LoadImage","IOException " + e.getMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            imageView.setImageBitmap(bitmap);
          //  Toast.makeText(MainActivity.this, "Load finish", Toast.LENGTH_SHORT).show();
        }
    }
    private void mapping(){
        // Define instance for firebase connection
        mData = FirebaseDatabase.getInstance().getReference();
        // Initial mAuth for Authenication methods
        mAuth = FirebaseAuth.getInstance();
        textViewTimeAndDate = (TextView)findViewById(R.id.textViewTimeAndDate);
        textViewLink = (TextView)findViewById(R.id.textViewLink);
        imageView = (ImageView)findViewById(R.id.imageView);
    }
    private void showTimeAndDate(){
        mData.child("At Current").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textViewTimeAndDate.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void checkStorage(){
        mData.child("Storage Image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textViewLink.setText(dataSnapshot.getValue().toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new LoadImage().execute(textViewLink.getText().toString());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void dangKy(){
        String email = "tranvanhay@gmail.com";
        String password = "vanhay2020";
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                          //  Toast.makeText(MainActivity.this,"Đăng ký thành công",Toast.LENGTH_SHORT).show();
                        }else{
                          //  Toast.makeText(MainActivity.this,"Đăng ký thất bại",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void logIn(){
        String email = "tranvanhay@gmail.com";
        String password = "vanhay2020";
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                          //  Toast.makeText(MainActivity.this,"Sign in successfull",Toast.LENGTH_SHORT).show();
                        }else{
                          //  Toast.makeText(MainActivity.this,"Sign in failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void checkAccount(){
        // Listen user
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                   // Toast.makeText(MainActivity.this,"User = " + user.getUid(),Toast.LENGTH_SHORT).show();
                } else {
                 //   Toast.makeText(MainActivity.this,"Not user",Toast.LENGTH_SHORT).show();
                }

            }
        };
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
}
