package com.example.test1.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.R;
import com.example.test1.item.listViewChoice_item;
import com.example.test1.view.adapter.listViewChoice_adapter;

import java.util.ArrayList;
import java.util.List;

public class listViewChoice extends AppCompatActivity implements View.OnClickListener {
    private List<listViewChoice_item> list=new ArrayList<>();
    private Button back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_choice);
        initData();
        back=(Button)findViewById(R.id.btn_listview_choice_back);
        back.setOnClickListener(this);
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.gridview_choice);
        GridLayoutManager layoutManager=new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);
        listViewChoice_adapter adapter=new listViewChoice_adapter(list,this);
        recyclerView.setAdapter(adapter);
    }

    private void initData(){
        listViewChoice_item a=new listViewChoice_item("进入教室",R.drawable.classroom_icon);
        list.add(a);
        listViewChoice_item b=new listViewChoice_item("管理员设置",R.drawable.admin_icon);
        list.add(b);



    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_listview_choice_back:
                this.finish();
                break;
        }
    }
}
