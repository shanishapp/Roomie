package com.example.roomie.house.user_profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomie.R;
import com.example.roomie.house.chores.chore.Chore;
import com.example.roomie.house.expenses.Expense;

import java.text.SimpleDateFormat;
import java.util.List;

public class DataListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CHORE_TYPE = 1;

    public static final int EXPENSE_TYPE = 2;


    private LayoutInflater inflater;

    private int adapterType;

    private List<Chore> choreList;

    private List<Expense> expenseList;


    public DataListAdapter(Context context, int adapterType) {
        inflater = LayoutInflater.from(context);
        this.adapterType = adapterType;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (adapterType == CHORE_TYPE) {
            View view = inflater.inflate(R.layout.profile_datalist_chore, parent, false);
            return new ProfileChoreHolder(view);
        } else {
            View view = inflater.inflate(R.layout.profile_datalist_expense, parent, false);
            return new ProfileExpenseHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (adapterType == CHORE_TYPE) {
            if (choreList == null) {
                return;
            }

            Chore chore = choreList.get(position);
            SimpleDateFormat localDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            String stringDueDate = localDateFormat.format(chore.get_dueDate());

            ProfileChoreHolder profileChoreHolder = (ProfileChoreHolder) holder;
            profileChoreHolder.setChoreTitle(chore.get_title());
            profileChoreHolder.setChoreDueDate(stringDueDate + " | " + chore.get_score() + " Brooms");
        } else {
            if (expenseList == null) {
                return;
            }

            Expense expense = expenseList.get(position);
            SimpleDateFormat localDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            String stringCreationDate = localDateFormat.format(expense.get_creationDate());

            ProfileExpenseHolder profileExpenseHolder = (ProfileExpenseHolder) holder;
            profileExpenseHolder.setExpenseTitle(expense.get_title());
            profileExpenseHolder.setExpenseCreationCost(stringCreationDate + " | " + expense.get_cost() + "\u20aa");
        }
    }

    @Override
    public int getItemCount() {
        if (adapterType == CHORE_TYPE) {
            return choreList == null ? 0 : choreList.size();
        }

        if (adapterType == EXPENSE_TYPE) {
            return expenseList == null ? 0 : expenseList.size();
        }

        return 0;
    }

    public void setChoreList(List<Chore> choreList) {
        this.choreList = choreList;
        notifyDataSetChanged();
    }

    public void setExpenseList(List<Expense> expenseList) {
        this.expenseList = expenseList;
        notifyDataSetChanged();
    }
}
