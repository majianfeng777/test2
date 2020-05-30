package com.example.test1.view;

import android.content.Intent;
import android.os.Bundle;
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

import java.io.IOException;

public class loginView extends AppCompatActivity implements View.OnClickListener {
    private Button signIn;
    private CheckBox checkBox;
    private boolean isAdmin=false;
    private boolean isLogin=false;
    private EditText account,password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginview);
        init();
//        verifyToService();  //验证账号密码是否正确
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

    }
    private void verifyToService(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connect connect=new Connect();
                    connect.post(account.getText().toString()+"_"+password.getText().toString());
                    if (connect.get()=="true"){
                        isLogin=true;
                    }
                    else{
                        isLogin=false;
                        Toast(connect.get());
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

    private void checkIn(){
        if (account.getText().toString()!=null){
            Toast("账号不能为空");
        }
        if (password.getText().toString()!=null){
            Toast("密码不能为空");
        }
        if (isLogin){
            //登入的加载画面...
            if (isAdmin){
                startActivity(new Intent(loginView.this, listViewChoice.class));
            }else {
                startActivity(new Intent(loginView.this, monitorView.class));
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signIn:
//                checkIn();  //判断账号密码
                if (isAdmin){
                    startActivity(new Intent(loginView.this, listViewChoice.class));
                }else {
                    startActivity(new Intent(loginView.this, monitorView.class));
                }
                break;

        }

    }
}
