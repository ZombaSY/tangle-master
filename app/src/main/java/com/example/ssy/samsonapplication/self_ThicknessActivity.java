package com.example.ssy.samsonapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class self_ThicknessActivity extends AppCompatActivity {

    private final String TAG = "Debugging";

    // 모낭이 여러개 보이는 사진일 때. 21 ~ 23
    private final float ONE_MM_PER_DP = 22;
    // 모낭이 1~2개 정도 보이는 확대된 사진일 떄. 55 ~ 58
    private final float ONE_MM_PER_DP_MAGNIFICATED = 56;

    private TextView hair_thickness;

    private int left_bar_xDelta;
    private int left_bar_yDelta;
    private int line_bar_xDelta;
    private int line_bar_yDelta;

    private float MAX_SCALE = 1f;
    private float MIN_SCALE = 0.03f;

    private int delta_x;
    private int delta_y;
    private float angle;
    private ImageView ruler_line;
    private ImageView ruler_left_bar;

    private int touched_x = 0;
    private int touched_y = 0;
    private double touched_vector_distance;
    private double delta_vector_distance;
    private float current_scale_status = 1;
    private float scale_temp = 1;
    private float changed_scale_ratio;
    private float ruler_length;
    private float hair_thickness_micrometer;

    private int pivot_x;
    private int pivot_y;
    private int ruler_orientation_calliborate;

    private boolean center_touched = false;

    private float DEVICE_DENSITY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_thickness);

        hair_thickness = findViewById(R.id.Hair_Thickness);
        ruler_line = findViewById(R.id.Ruler_Line);
        ruler_left_bar = findViewById(R.id.Ruler_Bar_Left);

        DEVICE_DENSITY = getResources().getDisplayMetrics().density;

        ruler_line.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                delta_x = (int) event.getRawX();
                delta_y = (int) event.getRawY();

                final int pivot_left_x = ruler_line.getLeft();
                final int pivot_left_y = ruler_line.getTop() + (int) ruler_line.getPivotY() + 80; // 근데 이렇게하면 핸드폰 화면의 절대좌표가 아니라 애플리케이션의 절대좌표가나옴.야매로 80정도 더함. 80은 맨 위 (kt 시간 ... 와이파이 배터리)
                final int pivot_right_x = ruler_line.getRight();
                final int pivot_right_y = ruler_line.getTop() + (int) ruler_line.getPivotY() + 80;

                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                boolean over_sclaed = false;

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:

                        touched_x = (int) event.getRawX();
                        touched_y = (int) event.getRawY();

                        touched_vector_distance = Math.sqrt(Math.pow((touched_x - pivot_left_x), 2) + Math.pow((touched_y - pivot_left_y), 2)) * 2; // Pivot의 위치가 가운데에서 왼쪽 끝으로 변했기 때문에 변화량을 반으로 하기위해 마지막에 2를 곱함

                        if ((ruler_line.getWidth() * current_scale_status - touched_vector_distance / 2) < 80) { //else if 에서 위에꺼 주석해서 if로 바꿈
                            Log.v(TAG, "오른쪽 터치됨");
                            pivot_x = pivot_left_x;
                            pivot_y = pivot_left_y;
                            ruler_line.setPivotX(0); // 왼쪽 바 기준
                            ruler_orientation_calliborate = 90;

                        } else {
                            Log.v(TAG, "가운대 터치됨");

                            pivot_x = pivot_left_x;
                            pivot_y = pivot_left_y;

                            RelativeLayout.LayoutParams left_params = (RelativeLayout.LayoutParams) ruler_left_bar.getLayoutParams();
                            left_bar_xDelta = x - left_params.leftMargin;
                            left_bar_yDelta = y - left_params.topMargin;

                            RelativeLayout.LayoutParams line_params = (RelativeLayout.LayoutParams) ruler_line.getLayoutParams();
                            line_bar_xDelta = x - line_params.leftMargin;
                            line_bar_yDelta = y - line_params.topMargin;

                            center_touched = true;
                        }

                        break;

                    case MotionEvent.ACTION_MOVE:

                        if (center_touched == false) {
                            angle = (float) -Math.toDegrees(Math.atan2(delta_x - pivot_x, delta_y - pivot_y)); // 마이너스 붙여야 정방향으로감. 거꾸로빼서 그런가?
                            delta_vector_distance = Math.sqrt(Math.pow((delta_x - pivot_x), 2) + Math.pow((delta_y - pivot_y), 2)) * 2; // Pivot의 위치가 바껴서 2를 곱함. 근데 터치한대로 줄자가 움직이면 보기 불편해서 더 크게함

                            changed_scale_ratio = (float) (delta_vector_distance / touched_vector_distance) - 1;
                            changed_scale_ratio *= current_scale_status * 2f; // 변화량에 현재 비율을 곱해서 작아졌거나 커졌을때 변화량에 차이를 줌. 안주면 비율이 작아졌을때 많이움직이고, 커졌을때 적게움직임

                            scale_temp = current_scale_status + changed_scale_ratio;

                            ruler_left_bar.setRotation(angle + ruler_orientation_calliborate);
                            ruler_line.setRotation(angle + ruler_orientation_calliborate); // 이미지마다 Orientation이 달라서 다른값을 거해줘야 Angle이 나옴. 그리고 Bar보다 무조건 나중에 Rotate해줘야됨

                            ruler_length = ruler_line.getWidth() * scale_temp / DEVICE_DENSITY; // 각 Device마다 dp가 다르기 떄문에 나눠줘서 줄자의 길이를 맞춰줘야함
                            hair_thickness_micrometer = 100 * ruler_length / ONE_MM_PER_DP; // 배율에따라 ONE_MM_PER_DP를 바꿔줘야됨

                            if (scale_temp >= MAX_SCALE) {
                                over_sclaed = true;
                                scale_temp = MAX_SCALE;
                                ruler_line.setScaleX(scale_temp);
                                break;
                            } else if (scale_temp <= MIN_SCALE) {
                                over_sclaed = true;
                                scale_temp = MIN_SCALE;
                                ruler_line.setScaleX(scale_temp);
                                break;
                            } else {
                                ruler_line.setScaleX(scale_temp);
                            }
                        } else {

                            RelativeLayout.LayoutParams left_params = (RelativeLayout.LayoutParams) ruler_left_bar
                                    .getLayoutParams();
                            left_params.leftMargin = x - left_bar_xDelta;
                            left_params.topMargin = y - left_bar_yDelta;

                            ruler_left_bar.setLayoutParams(left_params);

                            RelativeLayout.LayoutParams line_params = (RelativeLayout.LayoutParams) ruler_line
                                    .getLayoutParams();
                            line_params.leftMargin = x - line_bar_xDelta;
                            line_params.topMargin = y - line_bar_yDelta;

                            ruler_line.setLayoutParams(line_params);
                        }

                        hair_thickness.setText(Float.toString(hair_thickness_micrometer) + "um");

                        break;

                    case MotionEvent.ACTION_UP:

                        if (over_sclaed == true)
                            current_scale_status = MAX_SCALE;
                        else if (over_sclaed == true)
                            current_scale_status = MIN_SCALE;
                        else
                            current_scale_status = scale_temp;

                        center_touched = false;

                        break;
                }
                return true;
            }
        });
    }
}

