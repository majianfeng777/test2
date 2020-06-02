package com.example.test1.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test1.R;
import com.example.test1.util.TextSpeech;

import java.io.IOException;
import java.util.Locale;

public class secondaryCameraView extends AppCompatActivity implements View.OnClickListener {
    private WebView webView;
    private SurfaceView surfaceView;
    private WebSettings mWebSettings;
    private String urlWeb = "http://192.168.43.1:8080/browserfs.html";
    private TextView text_num_secondarycv;
    private EditText editText;
    private Button btn_back,btn_set;
    private AlertDialog.Builder alterdialog;
    private MediaPlayer mediaPlayer;
    private TextSpeech textSpeech;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondarycameraview);
        btn_back=(Button)findViewById(R.id.btn_secondarycv_back);
        btn_set=(Button)findViewById(R.id.btn_secondarycv_title_set);
        btn_back.setOnClickListener(this);
        btn_set.setOnClickListener(this);
        text_num_secondarycv=(TextView)findViewById(R.id.text_secondarycv);
        Intent intent=getIntent();
        text_num_secondarycv.setText(intent.getStringExtra("studentNum1"));
        alterdialog=new AlertDialog.Builder(secondaryCameraView.this);
//        surfaceView=(SurfaceView)findViewById(R.id.surfaceView_sencondarycv);
        connect();
//        display();
//        surfaceView.getHolder().addCallback(callback);

        btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText=new EditText(getApplicationContext());
                alterdialog.setTitle("设置IP摄像头地址");
                alterdialog.setView(editText);
                alterdialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.length()!=0){
                            urlWeb=editText.getText().toString();
                        }else{
                            Toast.makeText(secondaryCameraView.this,"ip不能为空",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alterdialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alterdialog.create().show();
            }
        });
        initTextToSpeech();
    }
    SurfaceHolder.Callback callback=new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
             mediaPlayer.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };
    private void connect() {
        webView = (WebView) findViewById(R.id.webView);
        mWebSettings = webView.getSettings();
//        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
//        mWebSettings.setJavaScriptEnabled(true);//是否允许JavaScript脚本运行，默认为false。设置true时，会提醒可能造成XSS漏洞
//        mWebSettings.setSupportZoom(true);//是否可以缩放，默认true

//        mWebSettings.setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
//        mWebSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
//        mWebSettings.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
//        mWebSettings.setAppCacheEnabled(true);//是否使用缓存
//        mWebSettings.setDomStorageEnabled(true);//开启本地DOM存储
//        mWebSettings.setLoadsImagesAutomatically(true); // 加载图片
//        mWebSettings.setMediaPlaybackRequiresUserGesture(false);//播放音频，多媒体需要用户手动？设置为false为可自动播放
//        webView.setWebChromeClient(new WebChromeClient());//这行最好不要丢掉
        //该方法解决的问题是打开浏览器不调用系统浏览器，直接用webview打开
//        webView.loadData();
        webView.loadUrl(urlWeb);   //本地局域网分配ip
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                return true;
            }


            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); //表示等待证书响应
                // handler.cancel(); //表示挂起连接，为默认方式

                // handler.handleMessage(null); //可做其他处理
            }
        });
    }
    private void initTextToSpeech() {
        int arrived=30;
        int absences=2;
        final String text="当前教室一共"+text_num_secondarycv.getText().toString()+"人已到"+arrived+"人"+"未到"+absences+"人";
        textSpeech=new TextSpeech(this,text);
    }
    /** 视频播放全屏 **/
//    private void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
//        // if a view already exists then immediately terminate the new one
//        if (customView != null) {
//            callback.onCustomViewHidden();
//            return;
//        }
//
//        WebVideoActivity.this.getWindow().getDecorView();
//
//        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
//        fullscreenContainer = new FullscreenHolder(this);
//        fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
//        decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
//        customView = view;
//        setStatusBarVisibility(false);
//        customViewCallback = callback;
//    }
    private void display(){
        mediaPlayer=new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this,Uri.parse(urlWeb));
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_secondarycv_back:
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
