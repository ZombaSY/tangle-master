package com.example.ssy.samsonapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;


import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


public class HistoryActivity extends DemoBase implements SeekBar.OnSeekBarChangeListener, OnChartValueSelectedListener {

    private static String IP_ADDRESS = "digger.works";
    private static String TAG = "HISTORYACTIVITYTAG";
    private String mJsonString;
    private String UserId;
    private String camType, valueType;
    private LineChart chart;
    private ArrayList<String> valValues;
    private ArrayList<Entry> values;
    private String valValue;
    private LineDataSet set1;

    final float maximum = 10f;
    final float minimum = 0f;
    final float error = (maximum - minimum) * 0.1f;
    final float axis_maximum = maximum + error;
    final float axis_minimum = minimum - error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);
        values = new ArrayList<>();
        valValues = new ArrayList<>();
        ImageButton goto_editor_btn = findViewById(R.id.HistoryLayoutGoToEditorButton);
        ImageButton goto_home_btn = findViewById(R.id.HistoryLayoutGoToHomeButton);

        UserId = SharedPreference.getAttribute(getApplicationContext(), "userId");

        final String[] hairview_spinner_contents = {"앞머리","윗머리", "정수리", "뒷머리", "옆머리"};
        final String[] hair_variable_spinner_contents = {"모발굵기", "모발밀도" ,"RedDot","YellowDot"};
        final String[] hair_data = {"Thickness", "Density" ,"RedDot","YellowDot"};

        Spinner hairview_spinner = findViewById(R.id.HairViewSpinner);
        ArrayAdapter<String> spinner_adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hairview_spinner_contents);
        hairview_spinner.setAdapter(spinner_adapter1);

        Spinner hair_variable_spinner = findViewById(R.id.HairVariableSpinner);
        ArrayAdapter<String> spinner_adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hair_variable_spinner_contents);
        hair_variable_spinner.setAdapter(spinner_adapter2);

        //헤어뷰 스피너 리스너
        hairview_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               camType = String.valueOf(position);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //모발변수 스피너 리스너
        hair_variable_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                Log.d(TAG,"OnClick Spinner");
                valueType = hair_data[position];
                Log.d(TAG,"hair_data : "+valueType);

                if(!camType.equals("")&&!camType.equals("")){
                    if(set1 != null){
                      for(int i = 0 ; i< set1.getEntryCount(); i++){
                          set1.removeEntry(i);
                      }
                    }
                    GetData task = new GetData();
                    task.execute( "http://" + IP_ADDRESS + "/hairloss_history.php", UserId,camType,valueType);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        goto_home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //chart 초기화
        {   // // Chart Style // //
            chart = findViewById(R.id.chart1);

            // background color
            chart.setBackgroundColor(Color.WHITE);

            // disable description text
            chart.getDescription().setEnabled(false);

            // enable touch gestures
            chart.setTouchEnabled(true);

            // set listeners
            chart.setOnChartValueSelectedListener(this);
            chart.setDrawGridBackground(false);

            // create marker to display box when values are selected
            MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

            // Set the marker to the chart
            mv.setChartView(chart);
            chart.setMarker(mv);

            // enable scaling and dragging
            chart.setDragEnabled(true);
            chart.setScaleEnabled(true);
            // chart.setScaleXEnabled(true);
            // chart.setScaleYEnabled(true);

            // force pinch zoom along both axis
            chart.setPinchZoom(true);
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        XAxis xAxis;
        {   // // X-Axis Style // //
            xAxis = chart.getXAxis();

            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 10f, 0f);
        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chart.getAxisLeft();

            // disable dual axis (only use LEFT axis)
            chart.getAxisRight().setEnabled(false);

            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f);

            // axis range
            yAxis.setAxisMaximum(axis_maximum);
            yAxis.setAxisMinimum(axis_minimum);
        }

        {   // // Create Limit Lines // //
            LimitLine llXAxis = new LimitLine(9f, "Index 10");
            llXAxis.setLineWidth(4f);
            llXAxis.enableDashedLine(10f, 10f, 0f);
            llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            llXAxis.setTextSize(10f);
            llXAxis.setTypeface(tfRegular);

            LimitLine ll1 = new LimitLine(10f, "Upper Limit");
            ll1.setLineWidth(4f);
            ll1.enableDashedLine(10f, 10f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(10f);
            ll1.setTypeface(tfRegular);

            LimitLine ll2 = new LimitLine(0f, "Lower Limit");
            ll2.setLineWidth(4f);
            ll2.enableDashedLine(10f, 10f, 0f);
            ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            ll2.setTextSize(10f);
            ll2.setTypeface(tfRegular);

            // draw limit lines behind data instead of on top
            yAxis.setDrawLimitLinesBehindData(true);
            xAxis.setDrawLimitLinesBehindData(true);

            // add limit line+s
            yAxis.addLimitLine(ll1);
            yAxis.addLimitLine(ll2);
            //xAxis.addLimitLine(llXAxis);
        }

    }

    class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //mTextViewResult.setText(result);
            if (result == null){
                Log.e(TAG, "history 결과값 null " + result);
            }
            else {
                mJsonString = result;
                Log.d("ddcc", result);
                Log.d("ddccd",  mJsonString );
                try {
                    JSONObject jObject = new JSONObject(result);

                    JSONArray json = jObject.getJSONArray("webnautes");
                    Log.d("ddc", json.toString());

                    for(int i=0;i<json.length();i++){
                        JSONObject e = json.getJSONObject(i);
                        valValue = e.getString(valueType);
                        //float val = Float.parseFloat(valValue);
                        valValues.add(valValue);
                        Log.d(TAG,"VALVALUE"+valValue);
                    }
                } catch (Exception k) {
                    Log.d("ddd", k.toString());
                }

                Log.d("YS Test", valValues.size() + "");

                drawGraph();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = (String)params[0];
            //String id = (String)params[1];
            //String camType = (String)params[2];
            //String  valueType = (String)params[3];

            Log.d(TAG,"PARAMS"+UserId+camType+valueType);
            String postParameters = "id=" + params[1]+ "&camType=" + params[2] + "&valueType=" + params[3] ;
            valValues = new ArrayList<>();


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
                return null;
            }

        }
    }

    //그래프 그리는 부분의 한 부분
    private void setData(int count, float range) {
        values = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            Log.d(TAG,"hbnghbvbgvyg"+ valValue);
           String valString = valValues.get(i);
             //Log.d(TAG,valValues.get(0));
            float val = Float.parseFloat(valString);
            values.add(new Entry(i, val, getResources().getDrawable(R.drawable.star)));
        }

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        }
        else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            set1.setDrawIcons(false);
            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f);
            // black lines and points
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            // line thickness and point size
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            // draw points as solid circles
            set1.setDrawCircleHole(false);
            // customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            // text size of values
            set1.setValueTextSize(9f);
            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            // set the filled area
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            // set data
            chart.setData(data);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onValueSelected(Entry e, Highlight h) {
    }

    @Override
    public void onNothingSelected() {
    }

    public void drawGraph(){
        setData(valValues.size(), maximum);
        Log.d("YS Test", "그래프 그리기 시작");
        // draw points over time
        chart.animateX(100);
        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        // draw legend entries as lines
        l.setForm(Legend.LegendForm.LINE);
        Log.d("YS Test", "그래프 그리기 종료");
    }
}

