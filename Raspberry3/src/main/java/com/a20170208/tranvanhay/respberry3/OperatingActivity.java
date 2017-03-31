package com.a20170208.tranvanhay.respberry3;

import android.app.Activity;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.ByteBuffer;

/**
 * Created by Tran Van Hay on 3/24/2017.
 */

public class OperatingActivity extends Activity {
    private static final String TAG = OperatingActivity.class.getSimpleName();
    // Instance for Realtime Database
    DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    // Instances for Authentication
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    // Instance for creation a Server Socket
    protected ServerSocket serverSocket;
    // Variable for display
    TextView txtSensor0,txtSensor1,txtSensor2,txtSensor3,txtSensor4,txtTime;
    // Instances for camera action
    private CameraRaspi mCamera;
    private Handler mCameraHandler;
    private HandlerThread mCameraThread;
    // Instances for Storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    int count = 0;
    String linkURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operating);
        logIn();
        checkAccount();
        Thread serverSocketThread = new Thread(new SocketServerThread(serverSocket));
        serverSocketThread.start();
        new TimeAnDate().showCurrentTime();
        displayInMonitor();
        doCaptureAction();
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
        mCamera.shutDown();
        mCameraThread.quitSafely();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    /**
     * These below method serve for capture and send image to firebase
     */
    private  void doCaptureAction(){
        // Creates new handlers and associated threads for camera and networking operations.
        mCameraThread = new HandlerThread("CameraBackground");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());
        // Camera code is complicated, so we've shoved it all in this closet class for you.
        mCamera = CameraRaspi.getInstance();
        mCamera.initializeCamera(this, mCameraHandler, mOnImageAvailableListener);
        mTakePicture.post(runnableTakePicture);
    }
    private ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = reader.acquireLatestImage();
                    // get image bytes
                    ByteBuffer imageBuf = image.getPlanes()[0].getBuffer();
                    final byte[] imageBytes = new byte[imageBuf.remaining()];
                    imageBuf.get(imageBytes);
                    image.close();
                    upLoadImage(imageBytes);
                }
            };

    private  Handler mTakePicture = new Handler();
    private Runnable runnableTakePicture = new Runnable() {
        @Override
        public void run() {
            mCamera.takePicture();
            Log.d(TAG,"Run runnableTakePicture");
            mTakePicture.postDelayed(runnableTakePicture,5000);
        }
    };
    private void upLoadImage( byte[] data){
        count++;
        StorageReference mountainsRef = storageRef.child("mountains" + count + ".jpg");
        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d(TAG,"Upload Image failed");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                linkURL  = downloadUrl + "";
                mData.child("Storage Image").setValue(linkURL);
                Log.d(TAG,"Upload Image successfully ");
            }
        });
    }
    /**
     * This method show value in monitor
     */
    public void displayInMonitor(){
        // mapping
        txtSensor0 = (TextView)findViewById(R.id.txtSensor0);
        txtSensor1 = (TextView)findViewById(R.id.txtSensor1);
        txtSensor2 = (TextView)findViewById(R.id.txtSensor2);
        txtSensor3 = (TextView)findViewById(R.id.txtSensor3);
        txtSensor4 = (TextView)findViewById(R.id.txtSensor4);
        txtTime = (TextView)findViewById(R.id.txtTime);
        // listen when Socket Server has change value then set new value
        mData.child("SocketServer").child("Temperature").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtSensor0.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mData.child("SocketServer").child("Humidity").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtSensor1.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mData.child("SocketServer").child("Flame").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtSensor2.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mData.child("SocketServer").child("Humidity Solid").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtSensor3.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mData.child("SocketServer").child("Light Intensity").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtSensor4.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mData.child("At Current").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtTime.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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
}
