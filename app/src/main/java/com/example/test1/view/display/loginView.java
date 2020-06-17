package com.example.test1.view.display;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test1.R;
import com.example.test1.util.Connect;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;

public class loginView extends AppCompatActivity implements View.OnClickListener {
    private Button signIn;
    private Connect connect;
    private CheckBox checkBox;
    private boolean isAdmin=false;
    private EditText account,password;
    private AVLoadingIndicatorView loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginview);
        init();  //初始化
    }

    private void init() {
        signIn=(Button)findViewById(R.id.signIn);
        checkBox=(CheckBox) findViewById(R.id.admin);
        account=(EditText)findViewById(R.id.account);
        password=(EditText)findViewById(R.id.password);
        signIn.setOnClickListener(this);
        loading=findViewById(R.id.loading);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    isAdmin=true;
                }else{
                    isAdmin=false;
                }
            }
        });
    }

    private void verifyToService(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Connect.getSocket().isConnected()){
                        Connect.post(account.getText().toString()+"_"+password.getText().toString());
                        final String response;
                        if ((response=Connect.get())!=null){
                            if (response.equals("true")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast("登入成功");
                                    }
                                });
                                Log.d("loginView:    ","true");
                                startActivity();
                            }
                            else{
                                Log.d("loginView:     ","false");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast(response);
                                    }
                                });
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //跳转界面
    private void startActivity(){
        if (isAdmin){
            startActivity(new Intent(loginView.this, listViewChoice.class));
        }else {
            startActivity(new Intent(loginView.this, monitorView.class));
        }
    }

    //弹出事件
    private void Toast(String text) {
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signIn:
                loadingView(true);  //加载动画显示
                verifyToService();  //判断账号密码
                outTimeLoading(); //超时
                break;
        }
    }

    //加载视图
    private void loadingView(boolean isShow){
        if (isShow){
            signIn.getBackground().mutate().setAlpha(125);
            loading.show();
            loading.setVisibility(View.VISIBLE);
        }else{
            signIn.getBackground().mutate().setAlpha(255);
            loading.hide();
            loading.setVisibility(View.INVISIBLE);
        }
    }

    //超时登入事件
    private void outTimeClick(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingView(false);
                Toast("超时登入,请重试");
            }
        });
    }

    //超时登入处理
    private void outTimeLoading() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(7000);
                    if (!Connect.getSocket().isConnected()){
                        outTimeClick();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    protected void onPause() {
        super.onPause();
        loadingView(false);
        account.setText("");
        password.setText("");
        checkBox.setChecked(false);
        account.requestFocus();
        password.clearFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (Connect.getSocket()!=null){
                Connect.getSocket().close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
