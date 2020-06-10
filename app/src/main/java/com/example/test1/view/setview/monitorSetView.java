package com.example.test1.view.setview;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test1.R;

public class monitorSetView extends AppCompatActivity implements View.OnClickListener{
    private Button btn_set_back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitorsetview);
        init();
    }
    private void init() {
        btn_set_back=(Button)findViewById(R.id.btn_set_back);
        btn_set_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_set_back:
                this.finish();
                break;
        }
    }
}
