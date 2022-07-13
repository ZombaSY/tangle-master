package com.example.ssy.samsonapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeLayoutActivity extends Activity {
    public String TAG = "Debugging";
    private String myStatus ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        int PERMISSION_ALL = 1;

        String[] PERMISSIONS = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        ImageButton normal_cam_btn = findViewById(R.id.NormalCameraButton);
        ImageButton usb_camera_btn = findViewById(R.id.USBCameraButton);

        RelativeLayout home_Editor = findViewById(R.id.home_to_Editor);
        RelativeLayout home_History = findViewById(R.id.home_to_History);

        TextView statusTv = findViewById(R.id.statusTV);


        Intent intent = getIntent();

        myStatus= intent.getStringExtra("UserName");
        if(myStatus.equals("")){
            myStatus = "나의 상태 관리";
        }else{
            myStatus = myStatus+"님의 상태 관리";
        }
        statusTv.setText(myStatus);


        normal_cam_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeLayoutActivity.this,
                        PerspectiveHairGridviewActivity.class);
                startActivity(intent);
            }
        });

        usb_camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(HomeLayoutActivity.this,
                        USBCameraActivity.class);
                startActivity(intent);
            }
        });

       home_Editor.setOnClickListener(new View.OnClickListener() {
        @Override
          public void onClick(View v) {
                Intent intent = new Intent(HomeLayoutActivity.this,
                    EditorActivity.class);
                startActivity(intent);
          }
      });

       home_History.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeLayoutActivity.this,
                        HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    public static boolean hasPermissions(Context context, String[] permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) !=
                        PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}