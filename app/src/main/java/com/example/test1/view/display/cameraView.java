package com.example.test1.view.display;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.Face;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.test1.R;
import com.example.test1.util.Connect;
import com.example.test1.util.TextSpeech;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class cameraView extends AppCompatActivity {
    private static void Log(String message) {
        Log.i(cameraView.class.getName(), message);
    }
    //为了使照片竖直显示
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private TextureView cView;//用于相机预览
    private TextureView rView;//用于标注人脸
    private ImageView imageView;//拍照照片显示
    private TextView textView;
    private Button btnOpen;
    private Button btnFront;
    private Button btnBack;
    private Button btnClose;
    private Button btnCapture;
    private Surface previewSurface;//预览Surface
    private ImageReader cImageReader;
    private Surface captureSurface;//拍照Surface
    HandlerThread cHandlerThread;//相机处理线程
    Handler cHandler;//相机处理
    CameraDevice cDevice;
    CameraCaptureSession cSession;
    CameraDevice.StateCallback cDeviceOpenCallback = null;//相机开启回调
    CaptureRequest.Builder previewRequestBuilder;//预览请求构建
    CaptureRequest previewRequest;//预览请求
    CameraCaptureSession.CaptureCallback previewCallback;//预览回调
    CaptureRequest.Builder captureRequestBuilder;
    CaptureRequest captureRequest;
    CameraCaptureSession.CaptureCallback captureCallback;
    TextSpeech textSpeech;
    int[] faceDetectModes;
    Size captureSize;
    boolean isFront;
    Paint pb;
    Bitmap bitmap,imgToShow;
    boolean isFace,isPhoto=false;
    String base64Pitcure;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cameraview);
        //GlobalExceptionHandler catchHandler = GlobalExceptionHandler.getInstance();
        //catchHandler.init(this.getApplication());
        initVIew();
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera(true);
            }
        });
        timer();
    }

    /**
     * 初始化界面
     */
    private void initVIew() {
        btnOpen=findViewById(R.id.btn_openCamera);
        cView = findViewById(R.id.cView);
        rView = findViewById(R.id.rView);
        //隐藏背景色，以免标注人脸时挡住预览画面
        rView.setAlpha(0.9f);

    }
    Size cPixelSize;
    int cOrientation;

    //TODO 摄像头静音尝试

    //打开摄像头
    private void openCamera(boolean isFront) {
        closeCamera();
        this.isFront = isFront;
        String cId = null;
        if (isFront) {
            cId = CameraCharacteristics.LENS_FACING_BACK + "";
        } else {
            cId = CameraCharacteristics.LENS_FACING_FRONT + "";
        }
        CameraManager cManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            //获取开启相机的相关参数
            CameraCharacteristics characteristics = cManager.getCameraCharacteristics(cId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] previewSizes = map.getOutputSizes(SurfaceTexture.class);//获取预览尺寸
            Size[] captureSizes = map.getOutputSizes(ImageFormat.JPEG);//获取拍照尺寸
            cOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);//获取相机角度
            Rect cRect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);//获取成像区域
            cPixelSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);//获取成像尺寸，同上
            //可用于判断是否支持人脸检测，以及支持到哪种程度
            faceDetectModes = characteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES);//支持的人脸检测模式
            int maxFaceCount = characteristics.get(CameraCharacteristics.STATISTICS_INFO_MAX_FACE_COUNT);//支持的最大检测人脸数量
            //此处写死640*480，实际从预览尺寸列表选择
            Size sSize = new Size(640, 480);//previewSizes[0];
            //设置预览尺寸（避免控件尺寸与预览画面尺寸不一致时画面变形）
            cView.getSurfaceTexture().setDefaultBufferSize(sSize.getWidth(), sSize.getHeight());
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
//                 here to request the missing permissions, and then overriding
//                   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                                          int[] grantResults)
//                 to handle the case where the user grants the permission. See the documentation
//                 for ActivityCompat#requestPermissions for more details.
                Toast.makeText(this, "请授予摄像头权限", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
                return;
            }
            //根据摄像头ID，开启摄像头
            try {
                cManager.openCamera(cId, getCDeviceOpenCallback(), getCHandler());
            } catch (CameraAccessException e) {
                Log(Log.getStackTraceString(e));
            }
        } catch (CameraAccessException e) {
            Log(Log.getStackTraceString(e));
        }
    }

    private void closeCamera() {
        if (cSession != null) {
            cSession.close();
            cSession = null;
        }
        if (cDevice != null) {
            cDevice.close();
            cDevice = null;
        }
        if (cImageReader != null) {
            cImageReader.close();
            cImageReader = null;
            captureRequestBuilder = null;
        }
        if (cHandlerThread != null) {
            cHandlerThread.quitSafely();
            try {
                cHandlerThread.join();
                cHandlerThread = null;
                cHandler = null;
            } catch (InterruptedException e) {
                Log(Log.getStackTraceString(e));
            }
        }
    }

    /**
     * 初始化并获取相机开启回调对象。当准备就绪后，发起预览请求
     */
    private CameraDevice.StateCallback getCDeviceOpenCallback() {
        if (cDeviceOpenCallback == null) {
            cDeviceOpenCallback = new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cDevice = camera;
                    try {
                        //创建Session，需先完成画面呈现目标（此处为预览和拍照Surface）的初始化
                        camera.createCaptureSession(Arrays.asList(getPreviewSurface(), getCaptureSurface()), new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession session) {
                                cSession = session;
                                //构建预览请求，并发起请求
                                Log("[发出预览请求]");
                                try {
                                    session.setRepeatingRequest(getPreviewRequest(), getPreviewCallback(), getCHandler());
                                } catch (CameraAccessException e) {
                                    Log(Log.getStackTraceString(e));
                                }
                            }

                            @Override
                            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                                session.close();
                            }
                        }, getCHandler());
                    } catch (CameraAccessException e) {
                        Log(Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    camera.close();
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    camera.close();
                    Log.d("Error:",String.valueOf(error));
                }
            };
        }
        return cDeviceOpenCallback;
    }

    /**
     * 初始化并获取相机线程处理
     *
     * @return
     */
    private Handler getCHandler() {
        if (cHandler == null) {
            //单独开一个线程给相机使用
            cHandlerThread = new HandlerThread("cHandlerThread");
            cHandlerThread.start();
            cHandler = new Handler(cHandlerThread.getLooper());
        }
        return cHandler;
    }

    /**
     * 获取支持的最高人脸检测级别
     *
     * @return
     */
    private int getFaceDetectMode() {
        if (faceDetectModes == null) {
            return CaptureRequest.STATISTICS_FACE_DETECT_MODE_FULL;
        } else {
            return faceDetectModes[faceDetectModes.length - 1];
        }
    }
    /*---------------------------------预览相关---------------------------------*/

    /**
     * 初始化并获取预览回调对象
     *
     * @return
     */
    private CameraCaptureSession.CaptureCallback getPreviewCallback() {
        if (previewCallback == null) {
            previewCallback = new CameraCaptureSession.CaptureCallback() {
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    cameraView.this.onCameraImagePreviewed(result);
                }
            };
        }
        return previewCallback;
    }

    /**
     * 生成并获取预览请求
     *
     * @return
     */
    private CaptureRequest getPreviewRequest() {
        previewRequest = getPreviewRequestBuilder().build();
        return previewRequest;
    }

    /**
     * 初始化并获取预览请求构建对象，进行通用配置，并每次获取时进行人脸检测级别配置
     *
     * @return
     */
    private CaptureRequest.Builder getPreviewRequestBuilder() {
        if (previewRequestBuilder == null) {
            try {
                previewRequestBuilder = cSession.getDevice().createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                previewRequestBuilder.addTarget(getPreviewSurface());
                previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);//自动曝光、白平衡、对焦
            } catch (CameraAccessException e) {
                Log(Log.getStackTraceString(e));
            }
        }
        previewRequestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, getFaceDetectMode());//设置人脸检测级别
        return previewRequestBuilder;
    }

    /**
     * 获取预览Surface
     *
     * @return
     */
    private Surface getPreviewSurface() {
        if (previewSurface == null) {
            previewSurface = new Surface(cView.getSurfaceTexture());
        }
        return previewSurface;
    }

    /**
     * 处理相机画面处理完成事件，获取检测到的人脸坐标，换算并绘制方框
     *
     * @param result
     */


    private void onCameraImagePreviewed(CaptureResult result) {
        Face faces[]= result.get(CaptureResult.STATISTICS_FACES);
        //检测人脸个数 faces.length
        if (isPhoto!=true){
            if (faces.length>0){
                isFace=true;
                showMessage(false, "检测到有人");
            }else{
                isFace=false;
            }
        }

//        showMessage(false,String.valueOf(isFace));
//            showMessage(false, "人脸个数:[" + faces.length + "]");
        Canvas canvas = rView.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//旧画面清理覆盖
        if (faces.length > 0) {
            for (int i = 0; i < faces.length; i++) {
                Rect fRect = faces[i].getBounds();
                Log("[R" + i + "]:[left:" + fRect.left + ",top:" + fRect.top + ",right:" + fRect.right + ",bottom:" + fRect.bottom + "]");
                showMessage(true, "[R" + i + "]:[left:" + fRect.left + ",top:" + fRect.top + ",right:" + fRect.right + ",bottom:" + fRect.bottom + "]");
                //人脸检测坐标基于相机成像画面尺寸以及坐标原点。此处进行比例换算
                //成像画面与方框绘制画布长宽比比例（同画面角度情况下的长宽比例（此处前后摄像头成像画面相对预览画面倒置（±90°），计算比例时长宽互换））
                float scaleWidth = canvas.getHeight() * 1.0f / cPixelSize.getWidth();
                float scaleHeight = canvas.getWidth() * 1.0f / cPixelSize.getHeight();
                //坐标缩放
                int l = (int) (fRect.left * scaleWidth);
                int t = (int) (fRect.top * scaleHeight);
                int r = (int) (fRect.right * scaleWidth);
                int b = (int) (fRect.bottom * scaleHeight);
                Log("[T" + i + "]:[left:" + l + ",top:" + t + ",right:" + r + ",bottom:" + b + "]");
                showMessage(true, "[T" + i + "]:[left:" + l + ",top:" + t + ",right:" + r + ",bottom:" + b + "]");
                if (isFront) {
                    //此处前置摄像头成像画面相对于预览画面顺时针90°+翻转。left、top、bottom、right变为bottom、right、top、left，并且由于坐标原点由左上角变为右下角，X,Y方向都要进行坐标换算
                    canvas.drawRect(canvas.getWidth() - b, canvas.getHeight() - r, canvas.getWidth() - t, canvas.getHeight() - l, getPaint());
                } else {
                    //此处后置摄像头成像画面相对于预览画面顺时针270°，left、top、bottom、right变为bottom、left、top、right，并且由于坐标原点由左上角变为左下角，Y方向需要进行坐标换算
                    canvas.drawRect(canvas.getWidth() - b, l, canvas.getWidth() - t, r, getPaint());
                }
//                    canvas.drawRect(canvas.getWidth()-b, canvas.getHeight()-r,canvas.getWidth()-t, canvas.getHeight()-l,getPaint());
            }
        }
        rView.unlockCanvasAndPost(canvas);
    }

    private void timer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        if (isFace){
                            Thread.sleep(3000);
                            if (isFace){
                                isPhoto=true;
                                isFace=false;
                                executeCapture(); //拍照
                                showMessage(false,"获取人像:"+getCaptureSize());
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Paint getPaint() {
        if (pb == null) {
            pb = new Paint();
            pb.setColor(Color.BLUE);
            pb.setStrokeWidth(10);
            pb.setStyle(Paint.Style.STROKE);//使绘制的矩形中空
        }
        return pb;
    }
    /**
     * 执行拍照
     */
    private void executeCapture() {
        try {
            Log.i(this.getClass().getName(), "发出请求");
            cSession.capture(getCaptureRequest(), getCaptureCallback(), getCHandler());
        } catch (CameraAccessException e) {
            Log(Log.getStackTraceString(e));
        }
    }

    /*---------------------------------拍照相关---------------------------------*/

    /**
     * 初始化拍照相关
     */
    private Surface getCaptureSurface() {
        if (cImageReader == null) {
            isFace=false;
            cImageReader = ImageReader.newInstance(getCaptureSize().getWidth(), getCaptureSize().getHeight(), ImageFormat.JPEG, 2);
            cImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    onCaptureFinished(reader);
                }
            }, getCHandler());
            captureSurface = cImageReader.getSurface();
        }
        return captureSurface;
    }

    public void SetCaptureSize(Size captureSize) {
        this.captureSize = captureSize;
    }

    /**
     * 获取拍照尺寸
     *
     * @return
     */
    private Size getCaptureSize() {
        if (captureSize != null) {
            return captureSize;
        } else {
            return cPixelSize;
        }
    }



    private CaptureRequest getCaptureRequest() {
        captureRequest = getCaptureRequestBuilder().build();
        return captureRequest;
    }

    private CaptureRequest.Builder getCaptureRequestBuilder() {
        if (captureRequestBuilder == null) {
            try {
                captureRequestBuilder = cSession.getDevice().createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                captureRequestBuilder.addTarget(getCaptureSurface());
                //TODO 1 照片旋转
//                int rotation = getWindowManager().getDefaultDisplay().getRotation();
//                int rotationTo = getOrientation(rotation);
//                captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, rotationTo);
            } catch (CameraAccessException e) {
                Log(Log.getStackTraceString(e));
            }
        }
        return captureRequestBuilder;
    }
    private CameraCaptureSession.CaptureCallback getCaptureCallback(){
        if(captureCallback == null){
            captureCallback = new CameraCaptureSession.CaptureCallback(){
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    cameraView.this.onCameraImagePreviewed(result);
                }
            };
        }
        return captureCallback;
    }

//https://github.com/googlesamples/android-Camera2Basic
    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     *
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    private int getOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (ORIENTATIONS.get(rotation) + cOrientation + 270) % 360;
    }

    /**
     * 处理相机拍照完成的数据
     * @param reader
     */
    //处理照片
    private void onCaptureFinished(ImageReader reader){
        Image image = reader.acquireLatestImage();
        byte[] data = new byte[]{};
        if (image!=null){
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            data = new byte[buffer.remaining()];
            buffer.get(data);
            image.close();
            buffer.clear();
            showMessage(false,"image!=null");
        }else showMessage(false,"image==nul");

        if (bitmap!=null){
            bitmap.recycle();
            bitmap=null;
        }
        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        Matrix matrix=new Matrix();
        matrix.setRotate(-90,bitmap.getWidth(),bitmap.getHeight());
        imgToShow = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);
        imgToShow=scaleBitmap(imgToShow,0.05f);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (bitmap!=null){
                   base64Pitcure=bitmaptoString(imgToShow);
                    showMessage(false,base64Pitcure);
                    isPhoto=false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(cameraView.this,"获取照片",Toast.LENGTH_SHORT).show();
                        }
                    });
                    getResponse();   //发送POST请求 得到返回值
                }else showMessage(false,"bitmap=null");

            }
        }).start();
        data=null;
        Runtime.getRuntime().gc();
    }
    private Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }
    public String bitmaptoString(Bitmap bitmap) {
        // 将Bitmap转换成字符串
        String string = null;
        boolean isNull;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        int len=string.length();
        showMessage(false,String.valueOf(len));
        return string;
    }


    /*---------------------------------拍照相关---------------------------------*/
    //显示数据
    private void showMessage(final boolean add, final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(add){
                    Log.d("ShowMessage:","\n"+message);
//                    textView.setText(textView.getText()+"\n"+message);
                }else{
                    Log.d("ShowMessage:",message);
//                    textView.setText(message);
                }
            }
        });
    }

    //发送请求 得到识别结果
    private void getResponse() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client=new OkHttpClient();
                    RequestBody requestBody=new FormBody.Builder().add("image_data",base64Pitcure)
                            .add("img_num","1").build();
                    Request request=new Request.Builder()
                            .url("http://192.168.1.115:8001/facerecg")
                            .post(requestBody)
                            .build();
                    Response response=client.newCall(request).execute();
                    String responseData=response.body().string();
                    parseJSONWithJSONObject(responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void parseJSONWithJSONObject(String responseData) {
        try {
            JSONObject jsonObject1=new JSONObject(responseData);
            final String status=jsonObject1.getString("status");  //status为状态码,非零表示异常
//             String errmsg=jsonObject1.getString("errmsg");
//            showMessage(false,"status:"+status+"errmsg:"+errmsg);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (status.equals("0")){
                        textSpeech=new TextSpeech(cameraView.this,"识别成功");
                    }else{
                        textSpeech=new TextSpeech(cameraView.this,"识别失败");
                    }
                    Toast.makeText(cameraView.this,"status:"+status,Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        if (textSpeech!=null){
            textSpeech.stop();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeCamera();
        if (textSpeech!=null){
            textSpeech.release();
        }
    }
}
