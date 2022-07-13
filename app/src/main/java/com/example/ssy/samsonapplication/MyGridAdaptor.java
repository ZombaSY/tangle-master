//package com.example.ssy.samsonapplication;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.GridView;
//import android.widget.ImageView;
//
//public class MyGridAdaptor extends BaseAdapter{
//
//    Context context;
//    Integer[] posterID = {
//            R.drawable.a1, R.drawable.a2, R.drawable.a3,
//            R.drawable.a4, R.drawable.a5, R.drawable.a6,
//    };
//
//    public MyGridAdaptor(Context c) {
//        context = c;
//    }
//    public int getCount(){
//        return posterID.length;
//    }
//
//    public Object getItem(int position){
//        return posterID[position];
//    }
//
//    public long getItemId(int position){
//        return posterID[position];
//    }
//
//    public View getView(int position, View convertView, ViewGroup parent){
//
//        Log.d("Debugging", "position :" + position);
//
//        ImageView imageView = new ImageView(context);
//        imageView.setLayoutParams(new GridView.LayoutParams(350, 350));
//        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        imageView.setPadding(5, 5, 5, 5);
//
//        imageView.setImageResource(posterID[position]);
//
//        return imageView;
//    }
//}
