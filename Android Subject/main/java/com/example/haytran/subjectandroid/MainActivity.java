package com.example.haytran.subjectandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    Button btnChangeActivity;
    VideoView videoView;
    Uri path;
    int currentPosition = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnChangeActivity = (Button)findViewById(R.id.btnChangeActivity);
        videoView = (VideoView)findViewById(R.id.videoView);
        Intent intent = getIntent();
        path = intent.getData();
        currentPosition = intent.getIntExtra("currentPositionLand",0);
        String tmpPath = intent.getStringExtra("pathLandToPort");
        Log.d("Hay*Nhung",tmpPath+"");
        if(path != null) {
            videoView.setVideoURI(path);
            videoView.start();
        }else if (tmpPath != null){
            path = Uri.parse(intent.getStringExtra("pathLandToPort"));
            videoView.setVideoURI(path);
            videoView.seekTo(currentPosition);
            videoView.start();
        }
        btnChangeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent  intent = new Intent(MainActivity.this,FullscreenActivity.class);
                intent.putExtra("pathPortToLand",path+"");
                intent.putExtra("currentPositionPort",videoView.getCurrentPosition());
                startActivity(intent);
            }
        });
        verifyStoragePermissions(this);

    }
    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
