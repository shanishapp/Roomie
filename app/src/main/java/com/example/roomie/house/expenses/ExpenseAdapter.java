package com.example.roomie.house.expenses;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.roomie.R;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder>
{

    static final int REQUEST_IMAGE_CAPTURE = 1;
    List<Expense> _expenses;
    private OnExpenseListener _myExpenseListener;
    private OnReceiptListener _myReceiptListener;

    public ExpenseAdapter(List<Expense> expenses, OnExpenseListener onExpenseListener,
                          OnReceiptListener onReceiptListener)
    {
        _expenses = expenses;
        _myExpenseListener = onExpenseListener;
        _myReceiptListener = onReceiptListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View expenseView = inflater.inflate(R.layout.single_expense, parent, false);
        return new ViewHolder(expenseView, _myExpenseListener, _myReceiptListener);
    }

    public void setExpenseIcon(ImageView image, Expense.ExpenseType expenseType)
    {
        int iconCode;
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
        ImageView receiptIcon = holder.receiptIcon;
        ImageView expenseTypeIcon = holder.expenseTypeIcon;
        LottieAnimationView checkMarkAnimation = holder.checkMarkAnimation;

        String costString = String.valueOf(expense.get_cost());
        String payerName = expense.get_payerName();
//        if (expense.get_type() == Expense.ExpenseType.GENERAL)
//        {
            titleView.setText(expense.get_title());
//        }
//        else
//        {
//            titleView.setText(expense.get_description());
//        }
        setExpenseIcon(expenseTypeIcon, expense.get_type());
        //TODO: use resource for currency symbol
        costView.setText(costString.concat("₪"));
        payerView.setText(payerName);
        if (expense.is_isSettled())
        {
            //TODO: animate only first time?
            blurUI(titleView, costView, payerView, receiptIcon, expenseTypeIcon);
            checkMarkAnimation.setVisibility(View.VISIBLE);
            checkMarkAnimation.animate();
        }

        receiptIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (expense.is_hasReceipt())
                {
                    //TODO: popup of receipt image + replace option
                } else
                {

                }
            }
        });
    }

    private void blurUI(TextView titleView, TextView costView, TextView payerView,
                        ImageView receiptIcon, ImageView expenseTypeIcon)
    {
        titleView.setAlpha((float) 0.5);
        costView.setAlpha((float) 0.5);
        payerView.setAlpha((float) 0.5);
        receiptIcon.setAlpha((float) 0.5);
        expenseTypeIcon.setAlpha((float) 0.5);
    }

    @Override
    public int getItemCount()
    {
        return _expenses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public ImageView receiptIcon;
        public ImageView expenseTypeIcon;
        public TextView title;
        public TextView cost;
        public TextView payer;
        OnExpenseListener onExpenseListener;
        OnReceiptListener onReceiptListener;
        public LottieAnimationView checkMarkAnimation;


        public ViewHolder(View view, OnExpenseListener onExpenseListener,
                          OnReceiptListener onReceiptListener)
        {
            super(view);
            receiptIcon = view.findViewById(R.id.ReceiptIcon);
            expenseTypeIcon = view.findViewById(R.id.expenseTypeIcon);
            title = view.findViewById(R.id.expenseTitleHolderView);
            cost = view.findViewById(R.id.expenseCostHolderView);
            payer = view.findViewById(R.id.expensePayerHolderView);
            checkMarkAnimation = view.findViewById(R.id.checkMarkAnimation);
            this.onExpenseListener = onExpenseListener;
            this.onReceiptListener = onReceiptListener;

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            onExpenseListener.onExpenseClick(getAdapterPosition());
        }
    }
//
//    private void dispatchTakePictureIntent()
//    {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
//        {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }

    public interface OnExpenseListener
    {
        void onExpenseClick(int pos);
    }

    public interface OnReceiptListener
    {
        void onReceiptClick();
    }
}
