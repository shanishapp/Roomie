package com.roomiemain.roomie.house.expenses;

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
import com.roomiemain.roomie.R;

import java.util.List;

/**
 * A recyclerView adapter holding expenses.
 */
public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder>
{

    List<Expense> _expenses;
    private final OnExpenseListener _myExpenseListener;
    private final OnReceiptListener _myReceiptListener;
    private final HouseExpensesFragment houseExpensesFragment;
    private final SharedPreferences sp;


    public ExpenseAdapter(List<Expense> expenses, OnExpenseListener onExpenseListener,
                          OnReceiptListener onReceiptListener, HouseExpensesFragment houseExpensesFragment)
    {
        _expenses = expenses;
        _myExpenseListener = onExpenseListener;
        _myReceiptListener = onReceiptListener;
        this.houseExpensesFragment = houseExpensesFragment;
        sp = houseExpensesFragment.getContext().getSharedPreferences("trackAnimation", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View expenseView = inflater.inflate(R.layout.adapter_expense, parent, false);
        return new ViewHolder(expenseView, _myExpenseListener, _myReceiptListener);
    }

    /**
     * sets an icon matching the expense typ in the UI
     * @param image - an ImageView to bet set with a matching source.
     * @param expenseType - an enum of the expense type.
     */

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
        checkMarkAnimation.setMaxProgress(1);
//        LottieAnimationView checkMarkFinalState = holder.checkMarkFinalState;
        String costString = String.valueOf(expense.get_cost());
        String payerName = expense.get_payerName();
        if (expense.get_type() == Expense.ExpenseType.GENERAL)
        {
            titleView.setText(expense.get_title());
        } else
        {
            titleView.setText(expense.get_description());
        }
        setExpenseIcon(expenseTypeIcon, expense.get_type());
        costView.setText(costString.concat(houseExpensesFragment.getContext().getString(R.string.currency_sign)));
        payerView.setText(payerName);
        if (expense.is_isSettled())
        {
            //Handle settled expenses animation and graphics
//            markSettledAnimations();
            String expenseID = expense.get_id();
            boolean wasAnimated = sp.getBoolean(expenseID, false);
            blurUI(titleView, costView, payerView, receiptIcon, expenseTypeIcon);
            if (wasAnimated)
            {
//                checkMarkAnimation.setVisibility(View.INVISIBLE);
//                checkMarkFinalState.setProgress(1);
//                checkMarkFinalState.setVisibility(View.VISIBLE);
                checkMarkAnimation.setProgress(1);
                checkMarkAnimation.setVisibility(View.VISIBLE);
            } else
            {
//                checkMarkFinalState.setVisibility(View.INVISIBLE);
                checkMarkAnimation.setVisibility(View.VISIBLE);
                checkMarkAnimation.playAnimation();
                sp.edit().putBoolean(expenseID, true).apply();
//                checkMarkAnimation.animate();
            }

            if (expense.is_hasReceipt())
            {
                //TODO: change receipt color
            } else
            {


            }

        }
        receiptIcon.setOnClickListener(v -> houseExpensesFragment.onReceiptClick(expense));
    }

//    private void markSettledAnimations()
//    {
//    }


    /**
     * blurs the UI of a given expense. to be used when it is marked done.
     * @param titleView - a textView of the title.
     * @param costView - a textView of the price.
     * @param payerView - a textView of the paying roommate.
     * @param receiptIcon - an ImageView.
     * @param expenseTypeIcon - an ImageView.
     */
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
//        public LottieAnimationView checkMarkFinalState;


        public ViewHolder(View view, OnExpenseListener onExpenseListener,
                          OnReceiptListener onReceiptListener)
        {
            super(view);
            receiptIcon = view.findViewById(R.id.receipt_icon);
            expenseTypeIcon = view.findViewById(R.id.expense_type_icon);
            title = view.findViewById(R.id.expense_title_text);
            cost = view.findViewById(R.id.expense_cost_text);
            payer = view.findViewById(R.id.expense_payer_text);
            checkMarkAnimation = view.findViewById(R.id.check_mark_animation);
//            checkMarkFinalState = view.findViewById(R.id.check_mark_animation_final_state);
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

    public interface OnExpenseListener
    {
        void onExpenseClick(int pos);
    }

    public interface OnReceiptListener
    {
        void onReceiptClick(Expense expense);
    }
}
