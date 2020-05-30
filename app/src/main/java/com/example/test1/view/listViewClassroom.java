package com.example.test1.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test1.R;
import com.example.test1.item.listViewClassroom_item;
import com.example.test1.util.Connect;
import com.example.test1.view.adapter.listViewClassroom_adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class listViewClassroom extends AppCompatActivity implements View.OnClickListener{
    private List<listViewClassroom_item> list=new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_classroom);
        initData();
        Button btn=findViewById(R.id.btn_listview_classroom_back);
        btn.setOnClickListener(this);
        listViewClassroom_adapter adapter=new listViewClassroom_adapter(listViewClassroom.this,R.layout.list_item_classroom,list);
        final ListView listView=(ListView) findViewById(R.id.listView_classroom);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               listViewClassroom_item item=list.get(position);
               switch (position){
                   case 0:
                       Intent intent=new Intent(listViewClassroom.this, monitorView.class);
                       intent.putExtra("studentNum",String.valueOf(item.getStudentNum()));
                       startActivity(intent);   //监控摄像头
                       break;
                   case 1:
                       Intent intent1=new Intent(listViewClassroom.this, secondaryCameraView.class);
                       intent1.putExtra("studentNum1",String.valueOf(item.getStudentNum()));
                       startActivity(intent1);   //手机摄像头
                       break;
                   case 2:
                       Intent intent2=new Intent(listViewClassroom.this, cameraView.class);
                       intent2.putExtra("studentNum2",String.valueOf(item.getStudentNum()));
                       startActivity(intent2);    //另一个手机摄像头
                       break;
               }
            }
        });
    }

    private void initData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connect connect=new Connect();
                    connect.post("进入教室");
                    String[] data=connect.get().split(",");
                    for (int i=0;i<data.length;i++){
                       String[] mdata=data[i].split("_");
                       listViewClassroom_item mndata=new listViewClassroom_item(Integer.valueOf(mdata[0]),Integer.valueOf(mdata[1]),Integer.valueOf(mdata[2]));
                       list.add(mndata);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        for (int i=0;i<5;i++){
            listViewClassroom_item b=new listViewClassroom_item(6502,48,30);
            list.add(b);
            listViewClassroom_item c=new listViewClassroom_item(6503,60,60);
            list.add(c);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_listview_classroom_back:
                this.finish();
                break;
        }
    }
}
