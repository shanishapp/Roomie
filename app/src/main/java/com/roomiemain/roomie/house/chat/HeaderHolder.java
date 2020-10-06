package com.roomiemain.roomie.house.chat;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roomiemain.roomie.R;

public class HeaderHolder extends RecyclerView.ViewHolder {

    private TextView headerTitle;


    public HeaderHolder(@NonNull View itemView) {
        super(itemView);

        headerTitle = itemView.findViewById(R.id.header_title);
    }


    public void setHeaderTitle(String headerTitle) {
        this.headerTitle.setText(headerTitle);
    }
}
