package com.example.test1.view.display;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.R;
import com.example.test1.item.gridViewChoice_item;
import com.example.test1.item.listViewChoice_item;
import com.example.test1.util.Connect;
import com.example.test1.view.adapter.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class listViewChoice extends AppCompatActivity implements View.OnClickListener {
    private List<gridViewChoice_item> list=new ArrayList<>();
    private List<listViewChoice_item> list_choice=new ArrayList<>();
    private Button back;
    private boolean isAdmin;
    private String account,password;
    private TextView text_title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_choice);
        initData();  //初始化数据
        initView();  //初始化视图

    }

    private void initView() {
        back=(Button)findViewById(R.id.btn_listview_choice_back);
        text_title=(TextView)findViewById(R.id.text_title_choice);
        text_title.setText("主界面");
        back.setOnClickListener(this);
        ListView listView=(ListView)findViewById(R.id.listview_account);
        listViewChoice_adapter listViewChoice_adapter=new listViewChoice_adapter(listViewChoice.this,R.layout.list_item_choice,list_choice);
        listView.setAdapter(listViewChoice_adapter);
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.gridview_choice);
        GridLayoutManager layoutManager=new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);
        gridViewChoice_adapter adapter=new gridViewChoice_adapter(list,this);
        recyclerView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listViewChoice_item item=list_choice.get(position);
                switch (position){
                    case 0:
                        Intent intent=new Intent(listViewChoice.this,informationView.class);
                        intent.putExtra("account",item.getText_account());
                        intent.putExtra("isAdmin",String.valueOf(isAdmin));
                        intent.putExtra("password",password);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void initData(){
        Intent intent=getIntent();
        isAdmin=intent.getBooleanExtra("isAdmin",false);
        account=intent.getStringExtra("account");
        password=intent.getStringExtra("password");
        if (isAdmin){
            listViewChoice_item one=new listViewChoice_item(R.drawable.people,account,R.drawable.vip);
            list_choice.add(one);
            gridViewChoice_item a=new gridViewChoice_item("进入教室",R.drawable.classroom_icon);
            list.add(a);
            gridViewChoice_item b=new gridViewChoice_item("管理员设置",R.drawable.admin_icon);
            list.add(b);

        }else{
            listViewChoice_item one=new listViewChoice_item(R.drawable.people,account,R.drawable.vip); //不是管理员图片还未设置
            list_choice.add(one);
            gridViewChoice_item a=new gridViewChoice_item("进入教室",R.drawable.classroom_icon);
            list.add(a);
        }
        gridViewChoice_item c=new gridViewChoice_item("key",R.drawable.key_icon);
        list.add(c);
        gridViewChoice_item d=new gridViewChoice_item("home",R.drawable.home);
        list.add(d);
        gridViewChoice_item e=new gridViewChoice_item("photo",R.drawable.photo);
        list.add(e);
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
