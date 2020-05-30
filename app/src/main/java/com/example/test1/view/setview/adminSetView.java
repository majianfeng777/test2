package com.example.test1.view.setview;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test1.R;

public class adminSetView extends AppCompatActivity implements View.OnClickListener{
    private TextView text_account;
    private Button btn_admin;
    private Button btn_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_setview);
        init();
    }

    private void init() {
        text_account=(TextView)findViewById(R.id.admin_setview_account);
        btn_admin=(Button)findViewById(R.id.btn_admin);
        btn_back=(Button)findViewById(R.id.btn_admin_setview_back);
        btn_back.setOnClickListener(this);
        btn_admin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_admin_setview_back:
                this.finish();
                break;
            case R.id.btn_admin:
                if (text_account.length()==0){
                    Toast.makeText(adminSetView.this,"账号不能为空",Toast.LENGTH_SHORT).show();
                }else{
                    //判断数据库里是否存在此账号if else
                    //判断账号是否已经是管理员if else
                    Toast.makeText(adminSetView.this,"修改成功",Toast.LENGTH_SHORT).show();
                }
        }
    }
}
