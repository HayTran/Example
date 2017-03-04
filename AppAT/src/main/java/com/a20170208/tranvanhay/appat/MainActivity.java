package com.a20170208.tranvanhay.appat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends AppCompatActivity {
    DatabaseReference mData;
    TextView textView;
    Button buttonYes;
    Button buttonNo;
    TextView textTime;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mData = FirebaseDatabase.getInstance().getReference();
        textView = (TextView)findViewById(R.id.textView);
        buttonYes = (Button)findViewById(R.id.buttonYes);
        buttonNo = (Button)findViewById(R.id.buttonNo);
        textTime = (TextView)findViewById(R.id.textTime);
        storage = FirebaseStorage.getInstance();
        mData.child("LED").setValue("Intial");
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textView.setText(dataSnapshot.child("LED").getValue().toString());
                textTime.setText(dataSnapshot.child("At Current").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mData.child("LED").setValue("Yes button", new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if(databaseError == null ){
                            Toast.makeText(MainActivity.this,"Gửi lên thành công",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this,"Gửi lên thất bại",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mData.child("LED").setValue("No button", new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if(databaseError == null ){
                            Toast.makeText(MainActivity.this,"Gửi lên thành công",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this,"Gửi lên thất bại",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // Dùng Firebase Storage
    }
}
