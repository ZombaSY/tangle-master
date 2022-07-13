package com.example.ssy.samsonapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class PerspectiveHairGridviewActivity extends Activity implements View.OnClickListener {

    private String imagepath = null;

    ImageView img1;
    ImageView img2;
    ImageView img3;
    ImageView img4;
    ImageView img5;
    ImageView img6;
    TextView back_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.perspective_hair_gridview_layout);

        img1 = findViewById(R.id.HairImageView1);
        img2 = findViewById(R.id.HairImageView2);
        img3 = findViewById(R.id.HairImageView3);
        img4 = findViewById(R.id.HairImageView4);
        img5 = findViewById(R.id.HairImageView5);
        img6 = findViewById(R.id.HairImageView6);

        Intent intent = new Intent(this.getIntent());
        back_text = findViewById(R.id.BackButton);

//        myGridAdaptor = new MyGridAdaptor(this);
//        gv.setAdapter(myGridAdaptor);

//        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                StrictMode.VmPolicy.Builder builder1 = new StrictMode.VmPolicy.Builder();
//                StrictMode.setVmPolicy(builder1.build());
//
//                File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//                File image = new File(storageDir.getAbsolutePath() + "/my_picture.jpg");
//                storageDir.mkdir();
//                imagepath = image.getAbsolutePath();
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
//
//                startActivityForResult(intent, 1);
//
//                switch (position){
//                    case 0 :
//                        loadImage();
//                }
//            }
//        });
    }

    @Override
    public void onClick(View v) {

        if(v==img1){
            takePicture(1001);
        }
        if(v==img2){
            takePicture(1002);
        }
        if(v==img3){
            takePicture(1003);
        }
        if(v==img4){
            takePicture(1004);
        }
        if(v==img5){
            takePicture(1005);
        }
        if(v==img6){
            takePicture(1006);
        }
        if(v==back_text){
            finish();
        }
    }

        @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1001) {
                loadImage(img1);
            } else if (requestCode == 1002){
                loadImage(img2);
            } else if (requestCode == 1003){
                loadImage(img3);
            } else if (requestCode == 1004){
                loadImage(img4);
            } else if (requestCode == 1005){
                loadImage(img5);
            } else if (requestCode == 1006){
                loadImage(img6);
            }

//            else if (requestCode == 2) {
//                Uri selectedImage = data.getData();
//                imagepath = getPath(selectedImage);
//                String[] filePath = {MediaStore.Images.Media.DATA};
//                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
//                c.moveToFirst();
//                int columnIndex = c.getColumnIndex(filePath[0]);
//                String picturePath = c.getString(columnIndex);
//                c.close();
//                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
//                //Log.w("path of image from gallery......******************.........", picturePath+"");
//                img1.setImageBitmap(thumbnail);
//            }
        }
    }

    private void takePicture(final int request_code){
        StrictMode.VmPolicy.Builder builder1 = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder1.build());

        File storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir.getAbsolutePath() + "/my_picture.jpg");
        storageDir.mkdir();
        imagepath = image.getAbsolutePath();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
        startActivityForResult(intent, request_code);
    }

    private void loadImage(ImageView imgv) {
        if (imagepath.isEmpty()) return;
        Bitmap myPictureBitmap = BitmapFactory.decodeFile(imagepath);
//        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
//            myPictureBitmap =
// Bitmap.createScaledBitmap(myPictureBitmap, imageview.getWidth(),imageview.getHeight(),true);
//        }
        imgv.setImageBitmap(myPictureBitmap);
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor =
                managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}