package com.example.test1.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.test1.R;
import com.example.test1.item.listViewClassroom_item;

import java.util.List;

public class listViewClassroom_adapter extends ArrayAdapter<listViewClassroom_item> {
    private int resourceId;
    public listViewClassroom_adapter(@NonNull Context context, int resource, List<listViewClassroom_item> list) {
        super(context, resource,list);
        resourceId=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        listViewClassroom_item item=getItem(position);
        View view=null;
        ViewHolder viewHolder;
        if (convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.classNum=(TextView)view.findViewById(R.id.classNum);
            viewHolder.studentNum=(TextView)view.findViewById(R.id.studentNum);
            viewHolder.arrivedNum=(TextView)view.findViewById(R.id.arrivedNum);
        }else{
            view=convertView;
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.classNum.setText(String.valueOf(item.getClassNum()));
        viewHolder.studentNum.setText(String.valueOf(item.getStudentNum()));
        viewHolder.arrivedNum.setText(String.valueOf(item.getArrivedNum()));
        return view;
    }

    public class ViewHolder {
        TextView classNum,studentNum,arrivedNum;
    }
}
