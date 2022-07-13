package com.example.ssy.samsonapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;

public class self_EditorActivity extends AppCompatActivity {

    LinearLayout layout;
    float x = 0;
    float y = 0;

    private TextView hair_counter;
    private TextView hole_counter;
    private ImageButton delete_btn;
    private ImageButton save_btn;
    private ImageButton temp1;
    private ImageButton temp2;
    private MyTouchImage touchImage;

    private final String HOLE_CUE = "HOLE";
    private final String HAIR_CUE = "HAIR";

    private final String TAG = "SELFEDITOR";
    private String userId,camType,date,densityImg;
    private float density;
    //아래는 서버 전송을 위해서
    public static final String UPLOAD_URL = "http://digger.works/hairloss_upload.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_editor);
        //upload server에 전송할 데이터들
        userId = SharedPreference.getAttribute(getApplicationContext(), "userId");
        camType = SharedPreference.getAttribute(getApplicationContext(), "camType");
        date = SharedPreference.getAttribute(getApplicationContext(), "date");


        hair_counter = findViewById(R.id.HairCounterTextView);
        hole_counter = findViewById(R.id.HoleCounterTextView);
        delete_btn = findViewById(R.id.Hair_Delete_Points_Button);
        save_btn = findViewById(R.id.Hair_Save_Picture_Button);
        temp1 = findViewById(R.id.self_EditorLayout_Button1);
        temp2 = findViewById(R.id.self_EditorLayout_Button2);
        touchImage =  findViewById(R.id.hairImage1);

        touchImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                hair_counter.setText(Integer.toString(touchImage.hair_count));
                hole_counter.setText(Integer.toString(touchImage.hole_count));

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
                hair_counter.setText(Integer.toString(touchImage.hair_count));
                hole_counter.setText(Integer.toString(touchImage.hole_count));
            }
        });

        temp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touchImage.cue = HAIR_CUE;
            }
        });

        temp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touchImage.cue = HOLE_CUE;
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

                /*서버에 사진 및 데이터 저장 */
                density = (float)touchImage.hole_count / (float)touchImage.hair_count;
                uploadImage(b);

                Intent intent = new Intent();
                if(touchImage.hole_count !=0 )
                    intent.putExtra("HairDensity", touchImage.hair_count / touchImage.hole_count);

                Log.v(TAG, "보내기전 : " + (float)touchImage.hair_count / (float)touchImage.hole_count);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        setResult(RESULT_CANCELED);
    }
    public void uploadImage(Bitmap bitmap) {
        class UploadImage extends AsyncTask<Bitmap, Void, String> {


            RequestHandler rh = new RequestHandler();

            @Override
            protected String doInBackground(Bitmap... params) {
                Log.d(TAG, "upload Do in Back Execute");
                Bitmap bitmap = params[0];
                String densityImg = getStringImage(bitmap);

                HashMap<String, String> data= new HashMap<>();
                data.put("userId", userId);
                data.put("date", date);
                data.put("camType", camType);
                data.put("density", String.valueOf(density));
                data.put("densityImg", densityImg);
                Log.d(TAG, "densityImg : "+densityImg);

                String result = rh.sendPostRequest(UPLOAD_URL, data);
                Log.d(TAG, "server result : "+result);
                return result;
            }
        }
        Log.d(TAG, "upload  get started");
        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

}
