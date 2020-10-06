package com.roomiemain.roomie.house.user_profile;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roomiemain.roomie.R;

public class ProfileExpenseHolder extends RecyclerView.ViewHolder {

    private TextView expenseTitle;

    private TextView expenseCreationCost;


    public ProfileExpenseHolder(@NonNull View itemView) {
        super(itemView);

        expenseTitle = itemView.findViewById(R.id.profile_datalist_expense_title);
        expenseCreationCost = itemView.findViewById(R.id.profile_datalist_expense_creation_cost);
    }


    public void setExpenseTitle(String expenseTitle) {
        this.expenseTitle.setText(expenseTitle);
    }

    public void setExpenseCreationCost(String creationCost) {
        this.expenseCreationCost.setText(creationCost);
    }
}
