package com.example.roomie.house.groceries;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomie.R;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.MyViewHolder> {

    List<String> datesList;

    public DateAdapter(List<String> dateList){
        this.datesList = dateList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_item,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //TextDrawable drawable;
    }

    @Override
    public int getItemCount() {
        return datesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView  date_avatar;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date_avatar = itemView.findViewById(R.id.date_avatar);
        }
    }
}
