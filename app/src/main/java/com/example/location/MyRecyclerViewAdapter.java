package com.example.location;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
    String data1[];
    Context context;
    public MyRecyclerViewAdapter(String s1[]){
        data1=s1;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.recyclerview_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.myText1.setText(data1[position]);


    }

    @Override
    public int getItemCount() {
        return data1.length;
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        TextView myText1;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myText1=itemView.findViewById(R.id.stops_txt);
        }
    }
}