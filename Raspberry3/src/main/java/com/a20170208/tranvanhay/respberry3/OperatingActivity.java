package com.a20170208.tranvanhay.respberry3;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.ByteBuffer;

/**
 * Created by Tran Van Hay on 4/22/2017.
 */

public class OperatingActivity extends Activity {
    private static final String TAG = OperatingActivity.class.getSimpleName();
    private static int TIME_TAKE_PICTURE = 10000;
    // Instance for Realtime Database
    DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
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
        mapping();
        new TimeAnDate().showCurrentTime();
        Thread serverSocketThread = new Thread(new SocketServerThread(serverSocket));
        serverSocketThread.start();
        displayInMonitor();
        captureImage();
    }
    private void mapping() {
        // mapping
        txtSensor0 = (TextView)findViewById(R.id.txtSensor0);
        txtSensor1 = (TextView)findViewById(R.id.txtSensor1);
        txtSensor2 = (TextView)findViewById(R.id.txtSensor2);
        txtSensor3 = (TextView)findViewById(R.id.txtSensor3);
        txtSensor4 = (TextView)findViewById(R.id.txtSensor4);
        txtTime = (TextView)findViewById(R.id.txtTime);
    }
    /**
     * These below method serve for capture and send image to firebase
     */
    private void captureImage(){
        // Creates new handlers and associated threads for camera and networking operations.
        mCameraThread = new HandlerThread("CameraBackground");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());
        // Camera code is complicated, so we've shoved it all in this closet class for you.
        mCamera = CameraRaspi.getInstance();
        mCamera.initializeCamera(this, mCameraHandler, mOnImageAvailableListener);
        mTakePicture.post(runnableTakePicture);
        // Take time to take picture in Firebase
        mData.child("TIME_TAKE_PICTURE").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TIME_TAKE_PICTURE = Integer.valueOf(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                    // compress byte to byte
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    upLoadImage(byteArray);
                }
            };

    private  Handler mTakePicture = new Handler();
    private Runnable runnableTakePicture = new Runnable() {
        @Override
        public void run() {
            mCamera.takePicture();
            Log.d(TAG,"Run runnableTakePicture");
            mTakePicture.postDelayed(runnableTakePicture,TIME_TAKE_PICTURE);
        }
    };
    private void upLoadImage( byte[] data){
        count++;
        StorageReference mountainsRef = storageRef.child("RaspberryCamera").child("image"+count+".jpg");
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

    public void displayInMonitor(){
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
        mData.child("SocketServer").child("Flame 0").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtSensor2.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mData.child("SocketServer").child("Flame 1").addValueEventListener(new ValueEventListener() {
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

}
