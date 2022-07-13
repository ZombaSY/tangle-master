package com.example.ssy.samsonapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.view.calender.horizontal.umar.horizontalcalendarview.DayDateMonthYearModel;
import com.view.calender.horizontal.umar.horizontalcalendarview.HorizontalCalendarView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class EditorActivity extends Activity{

    private final String TAG = "EDITORACTIVITYTAG";

    private ImageButton back_btn;
    private ImageButton goto_library_btn;
    private String imagepath = null;
    private DBHelper mDBHelper = new DBHelper(EditorActivity.this);
    private Button save_btn;
    private ImageButton hair_thickness, hair_density, hair_yellowdot, hair_reddot;
    private TextView thickness, density, yellowdot, reddot;
    private EditText memo;
    String calendar_date = null;

    private String asdasd = "초기화";

    private final int DENSITY_REQUEST_CODE = 1001;
    //Editor_hairDensity
    //서버에서 json 객체 다운로드 url
    private static String IP_ADDRESS = "digger.works";
    public static final String DOWNLOAD_URL = "http://digger.works/hairloss_download.php";
    private String userId,camType,date;
    private String mJsonString;
    String thicknessNum,densityNum;
    String yellowDotNum,redDotNum;
    String thickImg,densityImg,rdImg,ydImg,inMemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_layout);

        /*id, date, option type 정보를 주면 그 정보를 받아오는 */
        userId = SharedPreference.getAttribute(getApplicationContext(), "userId");
        date = SharedPreference.getAttribute(getApplicationContext(), "date");
        camType = SharedPreference.getAttribute(getApplicationContext(), "camType");



         Log.d(TAG, "server start"+userId+date+camType);

         GetData task = new GetData();
         task.execute(DOWNLOAD_URL ,userId ,date,camType);



//        back_btn = findViewById(R.id.BackButton);
//        goto_library_btn = findViewById(R.id.EditorLibraryButton);
 //       save_btn = findViewById(R.id.EditorLayoutSaveButton);
        hair_thickness = findViewById(R.id.Editor_hairThickness);
        hair_density = findViewById(R.id.Editor_hairDensity);
        hair_yellowdot = findViewById(R.id.Editor_hairYellowdot);
        hair_reddot = findViewById(R.id.Editor_hairReddot);
        thickness = findViewById(R.id.Editor_hairThickness_Textview);
        density = findViewById(R.id.Editor_hairDensity_Textview);
        reddot = findViewById(R.id.Editor_hairReddot_Textview);
        yellowdot = findViewById(R.id.Editor_hairYelowdot_Textview);
        memo = findViewById(R.id.Editor_memo_editText);
        save_btn = findViewById(R.id.Button_save);
        final int table_column_num = mDBHelper.getColumnNum();
        final String[] spinner_contents = {"앞머리","윗머리", "정수리", "뒷머리","옆머리"};

        Spinner spinner = findViewById(R.id.Editor_HairViewSpinner);
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinner_contents);

        spinner.setAdapter(spinner_adapter);


        //Log.d(TAG, "dfe - " +inMemo);

        hair_thickness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditorActivity.this,
                        self_ThicknessActivity.class);
                startActivity(intent);
            }
        });
        hair_density.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditorActivity.this,
                        self_EditorActivity.class);
                startActivityForResult(intent, DENSITY_REQUEST_CODE);
            }
        });
        hair_yellowdot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditorActivity.this,
                        self_YellowdotActivity.class);
                startActivity(intent);
            }
        });
        hair_reddot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditorActivity.this,
                        self_ReddotActivity.class);
                startActivity(intent);
            }
        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String memo_text = memo.getText().toString();

                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/hairloss_insert.php", memo_text);

                Toast toast = Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT);
                toast.show();


            }
        });

        //스피너 리스너
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String type = String.valueOf(position);
                SharedPreference.setAttribute(getApplicationContext(), "camType", type);
                //SharedPreference.setAttribute(getApplicationContext(), "camType", type);
                //Type을 앞머리 와 같은 식으로 텍스트로 DB에 저장할 경우, Unique 키를 사용할 수 없기 때문에, position 값을 저장 ex) 앞머리는 0

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        save_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String[] current_data;
//                current_data = mDBHelper.getUserData(calendar_date).split("\\s");
//                String[] new_data = getNewData();
//
//                for(int i=0; i<table_column_num; i++){
//                    if(new_data[i].equals(""))
//                        //getText().toString()에서 값이 없으면 null 이 아니라 ""를 반환함 *중요*
//                        new_data[i] = current_data[i];
//                }
//                try {
//                    mDBHelper.insertData(new_data, calendar_date);
//                    Toast.makeText(getApplicationContext(),
//                            "Update Successful!", Toast.LENGTH_SHORT).show();
//                }
//                catch (Exception e){
//                    Toast.makeText(getApplicationContext(),
//                            "Update Failed!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        back_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        goto_library_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(
//                        Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, 2);
//            }
//        });

        HorizontalCalendarView hcv = findViewById(R.id.horizontalcalendarview);
        hcv.setContext(EditorActivity.this);
        hcv.showControls(false);
        hcv.setControlTint(R.color.colorAccent);
        hcv.changeAccent(R.color.black);

    }
    private class GetData extends AsyncTask<String, Void, String> {


        String errorString = null;


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "post response - " + result);

            if (result == null){

                Log.d(TAG, "post response - " + "null");
            }
            else {

                mJsonString = result;
                try {
                JSONObject jObject = new JSONObject(result);

                JSONArray json = jObject.getJSONArray("webnautes");
                Log.d(TAG, "json :  - "+json.toString());
                for(int i=0;i<json.length();i++){
                    JSONObject e = json.getJSONObject(i);
                    thicknessNum  =  e.getString("thickness");
                    thickImg = e.getString("thickImg");
                    densityNum =  e.getString("density");
                    Log.d(TAG, "ddddsfddd - " + densityNum);
                    densityImg = e.getString("densityImg");
                    redDotNum = e.getString("redDot");
                    rdImg =  e.getString("rdImg");
                    yellowDotNum = e.getString("yellowDot");
                    ydImg = e.getString("ydImg");
                    inMemo = e.getString("memo");
                    Log.d(TAG, "dfe - " +inMemo);


                }

                    thickness.setText(thicknessNum);
                    density.setText(densityNum);
                    reddot.setText(redDotNum);
                    yellowdot.setText(yellowDotNum);
                    memo.setText(inMemo);
                    if(!thickImg.equals("")){
                        byte[] decodedThickImg = Base64.decode(thickImg, Base64.DEFAULT);
                        final Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedThickImg, 0, decodedThickImg.length);
                        Bitmap bitmap1 = Bitmap.createScaledBitmap(decodedByte, hair_thickness.getWidth(),hair_thickness.getHeight(), true);
                        hair_thickness.setImageBitmap(bitmap1);
                    }
                    if(!densityImg.equals("")){
                        byte[] decodedThickImg = Base64.decode(densityImg, Base64.DEFAULT);
                        final Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedThickImg, 0, decodedThickImg.length);
                        Bitmap bitmap1 = Bitmap.createScaledBitmap(decodedByte, hair_density.getWidth(),hair_density.getHeight(), true);
                        hair_density.setImageBitmap(bitmap1);
                    }
                    if(!rdImg.equals("")){
                        byte[] decodedThickImg = Base64.decode(rdImg, Base64.DEFAULT);
                        final Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedThickImg, 0, decodedThickImg.length);
                        Bitmap bitmap1 = Bitmap.createScaledBitmap(decodedByte, hair_reddot.getWidth(),hair_reddot.getHeight(), true);
                        hair_reddot.setImageBitmap(bitmap1);
                    }
                    if(!ydImg.equals("")){
                        byte[] decodedThickImg = Base64.decode(ydImg, Base64.DEFAULT);
                        final Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedThickImg, 0, decodedThickImg.length);
                        Bitmap bitmap1 = Bitmap.createScaledBitmap(decodedByte,hair_yellowdot.getWidth(),hair_yellowdot.getHeight(), true);
                        hair_yellowdot.setImageBitmap(bitmap1);
                    }



                } catch (Exception k) {

                    Log.e(TAG, "json error : "+ k.toString());

                }

                // showResult();
            }



        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = (String)params[0];
            String userId = (String)params[1];
            String dDate = (String)params[2];
            String camType = (String)params[3];


            String postParameters = "userId=" + userId+ "&dDate=" + dDate+ "&camType=" + camType;



            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "GetData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    // 캘린더 클릭 리스너
    public void newDateSelected(DayDateMonthYearModel selectedDate) {
        calendar_date = selectedDate.year+selectedDate.monthNumeric +selectedDate.date;

        String transDate = selectedDate.year+"-"+selectedDate.monthNumeric +"-"+selectedDate.date;
        SharedPreference.setAttribute(getApplicationContext(), "date", transDate);

        if(mDBHelper.availableDate(calendar_date))
            mDBHelper.addData(calendar_date);

        String[] current_data = mDBHelper.getUserData(calendar_date).split("-");

        for(int i=0; i<current_data.length; i++){
            if(current_data[i].equals("null"))
                current_data[i] = "";
        }
        memo.setText(current_data[1]);
        thickness.setText(current_data[2]);
        density.setText(current_data[3]);
        yellowdot.setText(current_data[4]);
        reddot.setText(current_data[5]);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (resultCode == RESULT_OK) {
//            if (requestCode == 2) {
//                Uri selectedImage = data.getData();
//                imagepath = getPath(selectedImage);
//                String[] filePath = { MediaStore.Images.Media.DATA };
//                Cursor c = getContentResolver().query(
//                        selectedImage,filePath, null, null, null);
//                c.moveToFirst();
//                int columnIndex = c.getColumnIndex(filePath[0]);
//                String picturePath = c.getString(columnIndex);
//                c.close();
//                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
//                goto_library_btn.setImageBitmap(thumbnail);
//            }
//        }

        switch (resultCode){
            case RESULT_OK:
                if (requestCode == DENSITY_REQUEST_CODE) {
                    final float  temp = data.getFloatExtra("HairDensity", 0f);
                    // 뭔버그냐 왜 안받아져@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    Log.v(TAG, "보낸 후 : " + temp);
                    Log.v(TAG, "있긴있냐 : " + data.hasExtra("HairDensity"));
                    density.setText(Float.toString(temp));
                }
                break;

             default:
                 break;
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection,
                null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String[] getNewData(){

        String[] new_data = {
                calendar_date,
                memo.getText().toString(),
               thickness.getText().toString(),
                density.getText().toString(),
                yellowdot.getText().toString(),
                reddot.getText().toString()};

        return new_data;
    }
    class InsertData extends AsyncTask<String, Void, String>{




        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String id = (String)params[1];
            String pwd = (String)params[2];

            String serverURL = (String)params[0];
            String postParameters = "id=" + id+ "&pwd=" + pwd;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
}
