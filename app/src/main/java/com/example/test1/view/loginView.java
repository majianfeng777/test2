package com.example.test1.view;

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
    private String isLogin;
    private EditText account,password;
    private AVLoadingIndicatorView loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginview);
        init();
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


    private void init() {
        signIn=(Button)findViewById(R.id.signIn);
        checkBox=(CheckBox) findViewById(R.id.admin);
        account=(EditText)findViewById(R.id.account);
        password=(EditText)findViewById(R.id.password);
        signIn.setOnClickListener(this);
        loading=findViewById(R.id.loading);
    }
    private void verifyToService(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connect=new Connect();
                    connect.post(account.getText().toString()+"_"+password.getText().toString());
                    if ((isLogin=connect.get())!=null){
                        if (isLogin.equals("true")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast("登入成功");
                                }
                            });
                            Log.d("loginView:    ","true");
                            if (isAdmin){
                                startActivity(new Intent(loginView.this, listViewChoice.class));
                            }else {
                                startActivity(new Intent(loginView.this, monitorView.class));
                            }
                        }
                        else{
                            Log.d("loginView:     ","false");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast(isLogin);
                                }
                            });

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void Toast(String text) {
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signIn:
                setAlpha(signIn,125);
                loading.show();
                verifyToService();  //判断账号密码
                break;

        }
    }
    private void setAlpha(Button button,int num){
        button.getBackground().mutate().setAlpha(num);
    }
    @Override
    protected void onPause() {
        super.onPause();
        setAlpha(signIn,255);
        loading.hide();
        try {
            connect.closeInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            connect.closeSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
