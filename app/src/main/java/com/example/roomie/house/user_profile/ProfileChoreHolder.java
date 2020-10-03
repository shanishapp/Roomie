package com.example.roomie.house.user_profile;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomie.R;

public class ProfileChoreHolder extends RecyclerView.ViewHolder {

    private TextView choreTitle;

    private TextView choreDueDate;


    public ProfileChoreHolder(@NonNull View itemView) {
        super(itemView);

        choreTitle = itemView.findViewById(R.id.profile_datalist_chore_title);
        choreDueDate = itemView.findViewById(R.id.profile_datalist_chore_due_date_brooms);
    }


    public void setChoreTitle(String choreTitle) {
        this.choreTitle.setText(choreTitle);
    }

    public void setChoreDueDate(String choreDueDate) {
        this.choreDueDate.setText(choreDueDate);
    }
}
