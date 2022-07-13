package com.example.ssy.samsonapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

// 에러나는데 괜춘함
public class MyTouchImage extends ImageView {

    Paint paint = new Paint();

    List<Point> points = new ArrayList<Point>();

    int hair_count = 0;
    int hole_count = 0;

    private MyTouchImage mImage =  MyTouchImage.this;

    String cue = "HAIR";
    private final String TAG = "Debugging";

    final int HAIR_CIRCLE_COLOR = Color.BLUE;
    final int HOLE_CIRCLE_COLOR = Color.YELLOW;

    private float triangle_height;
    private float triangle_width;
    private float circle_height;
    private float circle_width;

    Bitmap triangle_icon = BitmapFactory.decodeResource(getResources(), R.drawable.orange_triangle_resized);
    Bitmap circle_icon = BitmapFactory.decodeResource(getResources(), R.drawable.jade_circle_resized);

    public MyTouchImage(Context context, AttributeSet attrs) {
        super(context, attrs);

        triangle_height = triangle_icon.getHeight();
        triangle_width = triangle_icon.getWidth();

        circle_height = circle_icon.getHeight();
        circle_width = circle_icon.getWidth();
//        surfaceHolder = getHolder();

        MyTouchImage.this.buildDrawingCache();
        MyTouchImage.this.setDrawingCacheEnabled(true);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 모발 점 그리기
        for(Point p: points){
            if(p.hair_x == 0)
                continue;

            paint.setColor(HAIR_CIRCLE_COLOR);
            canvas.drawBitmap(circle_icon, p.hair_x - (triangle_width / 2), p.hair_y - (triangle_height / 2), paint);
        }
        // 모낭 점 그리기
        for(Point p: points){
            if(p.hole_x == 0)
                continue;
            paint.setColor(HOLE_CIRCLE_COLOR);
            canvas.drawBitmap(triangle_icon, p.hole_x - (circle_width / 2), p.hole_y - (triangle_height / 2), paint);
        }

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point p = new Point();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (cue){
                    case "HAIR" :
                        paint.setColor(HAIR_CIRCLE_COLOR);
                        p.hair_x = event.getX();
                        p.hair_y = event.getY();
                        points.add(p);

                        set_counts();
//                        hair_count++;
                        break;

                    case "HOLE":
                        paint.setColor(HOLE_CIRCLE_COLOR);
                        p.hole_x = event.getX();
                        p.hole_y = event.getY();
                        points.add(p);

                        set_counts();
//                        hole_count++;
                        break;

                    default:
                        break;
                }
        }

        return false;
    }

    class Point {
        float hair_x, hair_y;
        float hole_x, hole_y;
    }

    void set_counts(){
        int hair_temp = 0;
        int hole_temp = 0;

        for(int i = 0; i < points.size(); i++){
            if(points.get(i).hair_x != 0){
                hair_temp++;
            }
            if(points.get(i).hole_x != 0){
                hole_temp++;
            }
        }

        hair_count = hair_temp;
        hole_count = hole_temp;
    }
}