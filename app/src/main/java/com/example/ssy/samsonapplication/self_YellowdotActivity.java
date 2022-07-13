package com.example.ssy.samsonapplication;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class self_YellowdotActivity extends AppCompatActivity {

    private TextView yellowdot_counter;
    private ImageButton delete_btn;
    private ImageButton save_btn;
    private ImageButton change_btn;

    private MyTouchImage touchImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_yellowdot);

        yellowdot_counter = findViewById(R.id.YellowDotCounterTextView);
        delete_btn = findViewById(R.id.YellowDot_Delete_Button);
        save_btn = findViewById(R.id.YellowDot_Save_Button);
        change_btn = findViewById(R.id.YellowDot_Change_Button);
        touchImage =  findViewById(R.id.hairImage3);

        touchImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                yellowdot_counter.setText(Integer.toString(touchImage.hair_count)); // 여기서는 머리카락=RedDot 으로 해놓음

                return true;
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 마지막 인덱스 지우기
                if(touchImage.points.size() != 0){
                    touchImage.points.remove(touchImage.points.size() - 1);
                    touchImage.set_counts();
                }
                yellowdot_counter.setText(Integer.toString(touchImage.hair_count));
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap b = touchImage.getDrawingCache();
                String add_directory = "/dir1/dir2";
                File dir = new File(Environment.getExternalStorageDirectory(), add_directory);
                dir.mkdir();

                String file_name = System.currentTimeMillis() + ".jpg";
                File file = new File (dir,file_name);

                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(file);
                    b.compress(Bitmap.CompressFormat.JPEG, 100,fos);

                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "에러났다 이말이야. 경로확인", Toast.LENGTH_SHORT).show();
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "사진 저장됨.", Toast.LENGTH_SHORT).show();

                finish();
            }
        });

    }
}
