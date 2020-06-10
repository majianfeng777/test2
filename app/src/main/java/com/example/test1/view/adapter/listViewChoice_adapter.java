package com.example.test1.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.R;
import com.example.test1.item.listViewChoice_item;
import com.example.test1.item.listViewClassroom_item;
import com.example.test1.util.Connect;
import com.example.test1.view.setview.adminSetView;
import com.example.test1.view.display.listViewClassroom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class listViewChoice_adapter extends RecyclerView.Adapter<listViewChoice_adapter.ViewHolder> {
    private List<listViewChoice_item> itemList;
    public static List<listViewClassroom_item> list;
    private Context mContext;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View gridview;
        ImageView imageView;
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gridview=itemView;
            imageView=(ImageView)itemView.findViewById(R.id.image_item_choice);
            textView=(TextView)itemView.findViewById(R.id.text_choice);
        }
    }
    public listViewChoice_adapter(List<listViewChoice_item> items, Context context){
        itemList=items;
        mContext=context;
    }
    public listViewChoice_adapter(){

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_choice,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.gridview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                switch (position){
                    case 0:
                        init();
                        break;
                    case 1:
                        mContext.startActivity(new Intent(mContext, adminSetView.class));
                        break;
                }
//                listViewChoice_item listViewChoice_item=itemList.get(position);
//                listViewChoice_item.getText();

            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                switch (position){
                    case 0:
                        init();
                        break;
                    case 1:
                        mContext.startActivity(new Intent(mContext,adminSetView.class));
                        break;
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        listViewChoice_item item=itemList.get(position);
        holder.textView.setText(item.getText());
        holder.imageView.setImageResource(item.getImageId());
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private void init(){
        list=new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Connect.getSocket().isConnected()){
                        Connect.post("into");
                        String[] data;
                        if ((data=Connect.get().split(","))!=null){
                            for (int i=0;i<data.length;i++){
                                String[] mdata=data[i].split("_");
                                for (int m=0;m<4;m++){
                                    if (mdata[m].equals("")||mdata[m].equals(" ")){
                                        mdata[m]="0";
                                    }
                                }
                                listViewClassroom_item ndata=new listViewClassroom_item(Integer.valueOf(mdata[0]),Integer.valueOf(mdata[1]),Integer.valueOf(mdata[2]),mdata[3]);
                                list.add(ndata);
                            }
                            mContext.startActivity(new Intent(mContext, listViewClassroom.class));
                        }
                    }
//                    Connect connect=new Connect();
//                    connect.post("into");
//                    String[] data;
//                    if ((data=connect.get().split(","))!=null){
//                        for (int i=0;i<data.length;i++){
//                            String[] mdata=data[i].split("_");
//                            for (int m=0;m<4;m++){
//                                if (mdata[m].equals("")||mdata[m].equals(" ")){
//                                    mdata[m]="0";
//                                }
//                            }
//                            listViewClassroom_item ndata=new listViewClassroom_item(Integer.valueOf(mdata[0]),Integer.valueOf(mdata[1]),Integer.valueOf(mdata[2]),mdata[3]);
//                            list.add(ndata);
//                        }
//                        mContext.startActivity(new Intent(mContext, listViewClassroom.class));
//                        connect.closeInput();
//                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
