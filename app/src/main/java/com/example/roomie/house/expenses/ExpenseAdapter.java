package com.example.roomie.house.expenses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomie.R;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder>
{
    List<Expense> _expenses;
    private OnExpenseListener _myExpenseListener;

    public ExpenseAdapter(List<Expense> expenses, OnExpenseListener onExpenseListener)
    {
        _expenses = expenses;
        _myExpenseListener = onExpenseListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View expenseView = inflater.inflate(R.layout.one_expense_item,parent,false);
        return new ViewHolder(expenseView,_myExpenseListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {

    }

    @Override
    public int getItemCount()
    {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        public ImageView locked;
        public ImageView unlocked;
        public TextView title;
        public TextView dueDate;
        public TextView assignee;
        ExpenseAdapter.OnExpenseListener onExpenseListener;

        public ViewHolder(View view, ExpenseAdapter.OnExpenseListener onExpenseListener)
        {
            super(view);
            locked = view.findViewById(R.id.lockedView);
            unlocked = view.findViewById(R.id.unlockedView);
            title = view.findViewById(R.id.choreTitleHolderView);
            dueDate = view.findViewById(R.id.choreDueDateHolderView);
            assignee = view.findViewById(R.id.choreAssigneeHolderView);
            this.onExpenseListener = onExpenseListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {

        }
    }

    public interface OnExpenseListener
    {
        void onExpenseClick(int pos);
    }
}
