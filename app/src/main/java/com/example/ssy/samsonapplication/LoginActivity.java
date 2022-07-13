package com.example.ssy.samsonapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.view.WindowManager;

public class LoginActivity extends AppCompatActivity {


    private static String IP_ADDRESS = "digger.works";
    private static String TAG = "LOGINACTIVITYTAG";
    private EditText mEditTextId;
    private EditText mEditTextPwd;
    private String mJsonString;
    private ArrayList<PersonalData> mArrayList;
    //private TextView mTextViewResult;
    public  String UserName;
    String errorString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.login_layout);
        mEditTextId=  (EditText) findViewById(R.id.idEditText);
        mEditTextPwd= (EditText) findViewById(R.id.pwEditText);
        TextView logTV = findViewById(R.id.loginTextView);
        TextView regTV = findViewById(R.id.registerTextView);
        //mTextViewResult = (TextView)findViewById(R.id.textView_main_result); Debug를 위해서
        mArrayList = new ArrayList<>();

        logTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mArrayList.clear();

                String id =  mEditTextId.getText().toString();
                String pwd =  mEditTextPwd.getText().toString();
                SharedPreference.setAttribute(getApplicationContext() ,"userId", id);
                Log.d(TAG, "Pfdfddf - " + id+"pwd : "+pwd);
                if(id.equals("")||pwd.equals("")){
                    errorString = "아이디와 비밀번호를 모두 입력하여주세요.";
                    Toast toast = Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    GetData task = new GetData();
                    task.execute( "http://" + IP_ADDRESS + "/hairloss_login.php", id,pwd);

                    mEditTextId.setText("");
                    mEditTextPwd.setText("");

                    Log.d(TAG, "Put Extra  - " + UserName);
                }




            }
        });
        regTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = mEditTextId.getText().toString();
                String pwd = mEditTextPwd.getText().toString();

                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/hairloss_insert.php", id, pwd);


                mEditTextId.setText("");
                mEditTextPwd.setText("");

                Toast toast = Toast.makeText(getApplicationContext(), "등록되었습니다.", Toast.LENGTH_SHORT);
                toast.show();


            }
        });

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
    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;



        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //mTextViewResult.setText(result);



            if (result == null){
                Log.e(TAG, "login 결과값 null " + result);

            }
            else {

                mJsonString = result;
                String TAG_NAME = "UserName";
                Log.d("ddcc", result);
                Log.d("ddccd",  mJsonString );
                Toast toastEroor = Toast.makeText(getApplicationContext(),result, Toast.LENGTH_SHORT);


                try {

                    JSONObject jObject = new JSONObject(result);

                    JSONObject obj = jObject.getJSONObject("webnautes");
                    Log.d("ddc", obj.toString());
                    Log.d("ddd", obj.getString("UserName"));
                    UserName = obj.getString("UserName");

                    Toast toast = Toast.makeText(getApplicationContext(), "로그인되었습니다.", Toast.LENGTH_SHORT);
                    toast.show();
                    Intent intent =  new Intent(LoginActivity.this,
                            HomeLayoutActivity.class);
                    intent.putExtra("UserName", UserName);
                    Log.d(TAG, "Put Extra  - " + UserName);
                    startActivity(intent);

                } catch (Exception k) {

                    Log.e("ddd", k.toString());
                    toastEroor.show();

                }





             /*   try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray json = jsonObject.getJSONArray("webnautes"); // JSON 형식의 데이터 이름 webautes 가져오기


                    for(int i=0;i<json.length();i++){
                        JSONObject e = json.getJSONObject(i);
                        UserName = e.getString(TAG_NAME);

                    }



                } catch (JSONException e) {

                    Log.d(TAG, "GETNAME : ", e);
                }
                */

                //showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = (String)params[0];
            String id = (String)params[1];
            String pwd = (String)params[2];


            String postParameters = "id=" + id+ "&pwd=" + pwd;



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
    private void showResult(){

        String TAG_JSON="webnautes";
        String TAG_ID = "id";
        String TAG_PWD = "pwd";
        String TAG_NAME = "UserName";



        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String id = item.getString(TAG_ID);
                String pwd = item.getString(TAG_PWD);
                UserName = item.getString(TAG_NAME);
                PersonalData personalData = new PersonalData();

                personalData.setMember_id(id);
                personalData.setMember_pwd(pwd);



                mArrayList.add(personalData);

            }



        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }


}
