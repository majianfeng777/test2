package com.example.test1.view.display;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.test1.R;
import com.example.test1.util.TextSpeech;

import java.util.Arrays;

public class cameraView extends AppCompatActivity implements View.OnClickListener {
    private SurfaceView surfaceView;
    private CameraManager mCameraManager;
    private SurfaceHolder surfaceHolder;
    private CameraCaptureSession mCameraCaptureSession;
    private CameraDevice mCameraDevice;
    private Handler childHandler, mainHandler;
    private TextView text_cameraview_num;
    private TextSpeech textSpeech;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cameraview);
        initView();
        initTextToSpeech();
    }
    private CameraDevice.StateCallback stateCallback=new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice=camera;
            //开启预览
            takePreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            //关闭摄像头
            if (null != mCameraDevice){
                mCameraDevice.close();
            }
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Toast.makeText(getApplicationContext(), "摄像头开启失败", Toast.LENGTH_SHORT).show();
        }
    };

    private void takePreview() {
        try{
            //创建预览需要的CaptureRequest.Builder
            final CaptureRequest.Builder previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //将SurfceView的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(surfaceHolder.getSurface());
            //创建CameraCaptureSession,该对象负责管理处理预览请求和拍照请求
            mCameraDevice.createCaptureSession(Arrays.asList(surfaceHolder.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    if (null == mCameraDevice)return;
                    //摄像头已经准备好后显示预览
                    mCameraCaptureSession = cameraCaptureSession;
                    try {
                        //自动对焦
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        //打卡闪光灯
//                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                        //显示预览
                        CaptureRequest previewRequest = previewRequestBuilder.build();
                        mCameraCaptureSession.setRepeatingRequest(previewRequest,null,childHandler);// 进行预览
                    }catch (CameraAccessException e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(getApplicationContext(), "配置失败", Toast.LENGTH_SHORT).show();
                }
            },childHandler);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }
    private void initView(){
        //显示当前教室人数
        text_cameraview_num=(TextView)findViewById(R.id.text_cameraview);
        Intent intent=getIntent();
        text_cameraview_num.setText(intent.getStringExtra("studentNum2"));
//        initTextToSpeech();
        Button btn_back=(Button)findViewById(R.id.btn_cameraview_back);
        btn_back.setOnClickListener(this);
        //mSurfaceView
        surfaceView=(SurfaceView)findViewById(R.id.camera_surfaceview);
        surfaceHolder=surfaceView.getHolder();
        surfaceHolder.setKeepScreenOn(true);
        //mSurfaceView添加回调
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                //初始化camera
                initCamera2();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                //释放camera资源
                if (null != mCameraDevice){
                    mCameraDevice.close();
                }
            }
        });
    }

    private void initTextToSpeech() {
        int arrived=50;
        int absences=10;
        final String text="当前教室一共"+text_cameraview_num.getText().toString()+"人已到"+arrived+"人"+"未到"+absences+"人";
        textSpeech=new TextSpeech(this,text);
    }

    private void initCamera2() {
        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        childHandler = new Handler(handlerThread.getLooper());
        mainHandler = new Handler(Looper.getMainLooper());
        mCameraManager = (CameraManager)getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.CAMERA},1);  //*
            }else {
                //打开摄像头
                mCameraManager.openCamera("0",stateCallback,mainHandler);
            }
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                try {
                    mCameraManager.openCamera("0",stateCallback,mainHandler);
                }catch (CameraAccessException e){
                    e.printStackTrace();
                }catch (SecurityException e){
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cameraview_back:
                textSpeech.stop();
                this.finish();
                break;
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        textSpeech.release();
    }
}
