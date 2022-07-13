package com.example.ssy.samsonapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiangdg.usbcamera.UVCCameraHelper;
import com.jiangdg.usbcamera.utils.FileUtils;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;
import com.serenegiant.usb.widget.CameraViewInterface;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class USBCameraActivity extends Activity
        implements CameraDialog.CameraDialogParent, CameraViewInterface.Callback{

    private static final String TAG = "USBCameraActivityDebug";
    @BindView(R.id.camera_view)
    public View mTextureView;
    @BindView(R.id.capture)
    public Button capture_btn;
    @BindView(R.id.textview1)
    public TextView textview1;
    @BindView(R.id.ImgView1)
    public ImageView imgview1;

    private UVCCameraHelper mCameraHelper;
    private CameraViewInterface mUVCCameraView;

    private boolean isRequest;
    private boolean isPreview;

    private boolean grantExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
//                Log.v(TAG,"Permission is granted");
                return true;
            }else{
//                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }else{
//            Toast.makeText(this, "External Storage Permission is Grant", Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "External Storage Permission is Grant ");
            return true;
        }
    }

    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= 23) {
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                //resume tasks needing this permission
            }
        }
    }

    private UVCCameraHelper.OnMyDevConnectListener listener =
            new UVCCameraHelper.OnMyDevConnectListener() {

        @Override
        public void onAttachDev(UsbDevice device) {
            if (mCameraHelper == null || mCameraHelper.getUsbDeviceCount() == 0) {
                showShortMsg("카메라를 확인해주세요.");
                return;
            }
            // request open permission
            if (!isRequest) {
                isRequest = true;
                if (mCameraHelper != null) {
                    mCameraHelper.requestPermission(0);
                }
            }
        }

        @Override
        public void onDettachDev(UsbDevice device) {
            // close camera
            if (isRequest) {
                isRequest = false;
                mCameraHelper.closeCamera();
                showShortMsg("카메라가 해제되었습니다.");
            }
        }

        @Override
        public void onConnectDev(UsbDevice device, boolean isConnected) {
            if (!isConnected) {
                showShortMsg("연결 실패. 해상도를 확인해주세요.");
                isPreview = false;
            } else {
                isPreview = true;
                showShortMsg("연결중...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Looper.prepare();
                        Looper.loop();
                    }
                }).start();
            }
        }

        @Override
        public void onDisConnectDev(UsbDevice device) {
            showShortMsg("연결 해제");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usb_camera_layout);
        ButterKnife.bind(this);
        // step.1 initialize UVCCameraHelper
        mUVCCameraView = (CameraViewInterface) mTextureView;
        mUVCCameraView.setCallback(this);
        mCameraHelper = UVCCameraHelper.getInstance();
        mCameraHelper.setDefaultFrameFormat(UVCCameraHelper.FRAME_FORMAT_YUYV);
        mCameraHelper.initUSBMonitor(this, mUVCCameraView, listener);

        Button back_btn = findViewById(R.id.BackButton);

        grantExternalStoragePermission();

//        mCameraHelper.updateResolution(1280, 720); // 화면이 안나옴
//        Log.v(TAG, "높이 : " + mCameraHelper.getPreviewHeight() + "넓이 : " + mCameraHelper.getPreviewWidth());

        mCameraHelper.setOnPreviewFrameListener(
                new AbstractUVCCameraHandler.OnPreViewResultListener() {
            @Override
            public void onPreviewResult(byte[] nv21Yuv) {

            }
        });

        capture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCameraHelper == null || !mCameraHelper.isCameraOpened()) {
                    showShortMsg("카메라 열기 실패");
                }
                String picPath = UVCCameraHelper.ROOT_PATH;
                String add_directory = "/dir1/dir2";
                File dir = new File (picPath + add_directory);
                dir.mkdirs();
                final String pic_path =
                        dir.getAbsolutePath() + File.separator + System.currentTimeMillis()
                        + UVCCameraHelper.SUFFIX_JPEG;
                textview1.setText(pic_path);

                mCameraHelper.capturePicture(pic_path, new AbstractUVCCameraHandler.OnCaptureListener() {
                    @Override
                    public void onCaptureResult(String path) {
                        Log.i(TAG,"save path：" + path);
                    }
                });
                Bitmap bmp = BitmapFactory.decodeFile(pic_path);
                imgview1.setImageBitmap(bmp);
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        // step.2 register USB event broadcast
        if (mCameraHelper != null) {
            mCameraHelper.registerUSB();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // step.3 unregister USB event broadcast
        if (mCameraHelper != null) {
            mCameraHelper.unregisterUSB();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtils.releaseFile();
        // step.4 release uvc camera resources
        if (mCameraHelper != null) {
            mCameraHelper.release();
        }
    }

    private void showShortMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public USBMonitor getUSBMonitor() {
        return mCameraHelper.getUSBMonitor();
    }

    @Override
    public void onDialogResult(boolean canceled) {
        if (canceled) {
            showShortMsg("동작취소");
        }
    }

    @Override
    public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
        if (!isPreview && mCameraHelper.isCameraOpened()) {

//            mCameraHelper.setDefaultPreviewSize(1280, 720); // asdasdasda
//            mCameraHelper.updateResolution(1280, 720);

            mCameraHelper.startPreview(mUVCCameraView);


            isPreview = true;
        }
    }

    @Override
    public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {
        // USB카메라 빼면 팅기는걸 여기서 해결해야 할 듯
    }

    @Override
    public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
        if (isPreview && mCameraHelper.isCameraOpened()) {
            mCameraHelper.stopPreview();
            isPreview = false;
        }
    }
}
