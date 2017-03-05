package com.a20170208.tranvanhay.appat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {
    DatabaseReference mData;
    TextView textView;
    Button buttonYes;
    Button buttonNo;
    TextView textTime;


    // Authenication instance

    EditText editTextEmail, editTextPassword;
    Button btnDangNhap, btnDangKy;
    TextView textViewResult;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Define instance for firebase connection
        mData = FirebaseDatabase.getInstance().getReference();

        // Define instances for Main activity
        textView = (TextView)findViewById(R.id.textView);
        buttonYes = (Button)findViewById(R.id.buttonYes);
        buttonNo = (Button)findViewById(R.id.buttonNo);
        textTime = (TextView)findViewById(R.id.textTime);

        // Authenication instance
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        btnDangKy = (Button)findViewById(R.id.buttonDangKy);
        btnDangNhap = (Button)findViewById(R.id.buttonDangNhap);
        textViewResult = (TextView)findViewById(R.id.textViewResult);

        // Initial mAuth for Authenication methods
        mAuth = FirebaseAuth.getInstance();

        // Listen user
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(MainActivity.this,"User = " + user.getUid(),Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,"Not user",Toast.LENGTH_SHORT).show();
                }

            }
        };

        //Implement sign-up
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dangKy();
            }
        });

        // Implement sign-in
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Toast.makeText(MainActivity.this,"User = "  + user.getEmail(),Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,"No user sign-in",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Set value over firebase database
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
    }
    private void dangKy(){
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"Đăng ký thành công",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this,"Đăng ký thất bại",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void signIn(){
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"Sign in successfull",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this,"Sign in failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
