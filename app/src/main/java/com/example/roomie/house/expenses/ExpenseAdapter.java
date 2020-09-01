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
        View expenseView = inflater.inflate(R.layout.single_expense, parent, false);
        return new ViewHolder(expenseView, _myExpenseListener);
    }

    public void setExpenseIcon(ImageView image, Expense.ExpenseType expenseType)
    {
        int iconCode = 0;
        switch (expenseType)
        {
            case PROFESSIONAL:
                iconCode = R.drawable.ic_professional;
                break;
            case BILL:
                iconCode = R.drawable.ic_baseline_bill_24;
                break;
            case GROCERIES:
                iconCode = R.drawable.ic_baseline_shopping_cart_24;
                break;
            default:
                iconCode = R.drawable.ic_baseline_attach_money_24;
                break;
        }
        image.setImageResource(iconCode);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Expense expense = _expenses.get(position);
        TextView titleView = holder.title;
        TextView costView = holder.cost;
        TextView payerView = holder.payer;

        String costString = String.valueOf(expense.get_cost());
        String payerName = expense.get_payer().get_name();

        titleView.setText(expense.get_name());
        if (expense.is_hasReceipt())
        {
            holder.viewReceiptIcon.setVisibility(View.VISIBLE);
        }
        setExpenseIcon(holder.expenseTypeIcon, expense.get_type());
        costView.setText(costString);
        payerView.setText(payerName);

    }

    @Override
    public int getItemCount()
    {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        public ImageView viewReceiptIcon;
        public ImageView expenseTypeIcon;
        public TextView title;
        public TextView cost;
        public TextView payer;
        ExpenseAdapter.OnExpenseListener onExpenseListener;

        public ViewHolder(View view, ExpenseAdapter.OnExpenseListener onExpenseListener)
        {
            super(view);
            viewReceiptIcon = view.findViewById(R.id.viewReceiptIcon);
            expenseTypeIcon = view.findViewById(R.id.expenseTypeIcon);
            title = view.findViewById(R.id.choreTitleHolderView);
            cost = view.findViewById(R.id.expenseCostHolderView);
            payer = view.findViewById(R.id.expensePayerHolderView);
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
