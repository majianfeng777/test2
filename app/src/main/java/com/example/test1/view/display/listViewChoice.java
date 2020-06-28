package com.example.test1.view.display;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.R;
import com.example.test1.item.listViewChoice_item;
import com.example.test1.util.Connect;
import com.example.test1.view.adapter.listViewChoice_adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class listViewChoice extends AppCompatActivity implements View.OnClickListener {
    private List<listViewChoice_item> list=new ArrayList<>();
    private Button back;
    private boolean isAdmin;
    private TextView text_title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_choice);
        initData();
        back=(Button)findViewById(R.id.btn_listview_choice_back);
        text_title=(TextView)findViewById(R.id.text_title_choice);
        text_title.setText("");
        back.setOnClickListener(this);
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.gridview_choice);
        GridLayoutManager layoutManager=new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);
        listViewChoice_adapter adapter=new listViewChoice_adapter(list,this);
        recyclerView.setAdapter(adapter);
    }

    private void initData(){
        Intent intent=getIntent();
        isAdmin=intent.getBooleanExtra("isAdmin",false);
        if (isAdmin){
            listViewChoice_item a=new listViewChoice_item("进入教室",R.drawable.classroom_icon);
            list.add(a);
            listViewChoice_item b=new listViewChoice_item("管理员设置",R.drawable.admin_icon);
            list.add(b);
        }else{
            listViewChoice_item a=new listViewChoice_item("进入教室",R.drawable.classroom_icon);
            list.add(a);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_listview_choice_back:
                sendOutConn();
                this.finish();
                break;
        }
    }
    private void sendOutConn() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Connect.getSocket().isConnected()){
                        Connect.post("out");
                        final String get=Connect.get();
                        Message message=new Message();
                        message.what=1;
                        message.obj=get;
                        handler.sendMessage(message);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(listViewChoice.this,get,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                Toast.makeText(listViewChoice.this,msg.obj.toString(),Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    protected void onPause() {
        super.onPause();

    }
}
