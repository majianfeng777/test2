package com.example.test1.view;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test1.R;
import com.example.test1.util.TextSpeech;
import com.example.test1.view.setview.monitorSetView;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class monitorView extends AppCompatActivity implements View.OnClickListener {
    private Button btn_back, btn_set;
    private TextView text_monitor;
    private EZPlayer player;
    private SurfaceView surfaceView;
    private String accessToken;  //
    private long expireTime;     //到期时间
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private TextSpeech textSpeech;
    private boolean isAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moniorview);
        init();
        displayView();
        initTextToSpeech();
    }
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_SUCCESS:
                    //播放成功
                    Log.d("播放","成功");
                    break;
                case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_FAIL:
                    //播放失败,得到失败信息
                    ErrorInfo errorinfo = (ErrorInfo) msg.obj;
                    //得到播放失败错误码
                    int code = errorinfo.errorCode;
                    //得到播放失败模块错误码
                    String codeStr = errorinfo.moduleCode;
                    //得到播放失败描述
                    String description = errorinfo.description;
                    //得到播放失败解决方方案
                    String descriptionway = errorinfo.sulution;
                    Log.d("播放失败错误码:", String.valueOf(code));
                    Log.d("播放失败模块错误码:",codeStr);
                    Log.d("播放失败描述",description);
                    Log.d("播放失败解决方方案",descriptionway);
                    break;
                case EZConstants.MSG_VIDEO_SIZE_CHANGED:
                    //解析出视频画面分辨率回调
                    try {
                        String temp = (String) msg.obj;
                        String[] strings = temp.split(":");
                        int mVideoWidth = Integer.parseInt(strings[0]);
                        int mVideoHeight = Integer.parseInt(strings[1]);
                        Log.d("视频分辨率：",String.valueOf(mVideoWidth)+String.valueOf(mVideoHeight));
                        //解析出视频分辨率
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    SurfaceHolder.Callback callback=new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try{
                player.setSurfaceHold(holder);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };
    private void displayView() {
        player = EZOpenSDK.getInstance().createPlayer("C48003242", 1);
        //设置Handler, 该handler将被用于从播放器向handler传递消息
        player.setHandler(mHandler);
        /**
         * 设备加密的需要传入密码
         * 传入视频加密密码，用于加密视频的解码，该接口可以在收到ERROR_INNER_VERIFYCODE_NEED或ERROR_INNER_VERIFYCODE_ERROR错误回调时调用
         * @param verifyCode 视频加密密码，默认为设备的6位验证码
         */
        player.setPlayVerifyCode("KPXZPS");
    }


    private void init() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_set = (Button) findViewById(R.id.btn_set);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        text_monitor=(TextView)findViewById(R.id.text_moniorview);
        btn_back.setOnClickListener(this);
        btn_set.setOnClickListener(this);
        Intent intent=getIntent();
        text_monitor.setText(intent.getStringExtra("studentNum"));
        preferences=getSharedPreferences("data",MODE_PRIVATE);
        editor=preferences.edit();
        //初始化设置
        EZOpenSDK.showSDKLog(true);
        EZOpenSDK.enableP2P(false);
        EZOpenSDK.initLib(this.getApplication(), "43806636d1ec4c348d784427674511cf");
//        getResponse();    //发送POST请求获取返回数据 用JSONObject解析
        surfaceView.getHolder().addCallback(callback);
    }


    private void getResponse() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client=new OkHttpClient();
                    RequestBody requestBody=new FormBody.Builder().add("appKey","43806636d1ec4c348d784427674511cf")
                            .add("appSecret","1615f9ae6b00a901f2ee652ecd9e6cce").build();
                    Request request=new Request.Builder()
                            .url("https://open.ys7.com/api/lapp/token/get")
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
            JSONObject jsonObject=new JSONObject(responseData);
            String data=jsonObject.getString("data");
            JSONObject jsonObject1=new JSONObject(data);    //为什么要用两次JSONObject  直接getString（accessToken）就不行 要先获取data:{...}
            accessToken=jsonObject1.getString("accessToken");
            expireTime=Long.valueOf(jsonObject1.getString("expireTime"));
            EZOpenSDK.getInstance().setAccessToken(accessToken);
//        if (expireTime<3600000){  //过期时间小于1h
//            EZOpenSDK.getInstance().setAccessToken(accessToken);
//            editor.putString("accessToken",accessToken);
//            editor.commit();
//        }else{
//            accessToken=preferences.getString("accessToken","at.dbbtbh6f23vkpj5b5mft3ld45bxb8yep-8abhaftin4-1kc0bfs-2v75forih");
//            EZOpenSDK.getInstance().setAccessToken(accessToken);
//         }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void initTextToSpeech() {
        int arrived=50;
        int absences=10;
        final String text="当前教室一共"+text_monitor.getText().toString()+"人已到"+arrived+"人"+"未到"+absences+"人";
        textSpeech=new TextSpeech(this,text);
    }
    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.btn_back:
                  textSpeech.stop();
                  this.finish();
                break;
            case R.id.btn_set:
                textSpeech.stop();
                startActivity(new Intent(monitorView.this, monitorSetView.class));
                break;

        }
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player!=null){
            //释放资源
            player.release();
            //停止直播
            // player.stopRealPlay();
        }
        textSpeech.release();
    }
}
