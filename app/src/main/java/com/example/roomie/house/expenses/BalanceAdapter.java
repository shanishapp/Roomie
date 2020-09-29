package com.example.roomie.house.expenses;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomie.R;
import com.example.roomie.house.HouseActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.ViewHolder>
{
    private List<Pair<String, Double>> _namesAndBalances;
    public BalanceAdapter()
    {
        _namesAndBalances = new ArrayList<>();
    }

    public BalanceAdapter(ArrayList<Pair<String, Double>> namesAndBalances)
    {
        _namesAndBalances = namesAndBalances;
    }

    @NonNull
    @Override
    public BalanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View roommateView = inflater.inflate(R.layout.single_roommate_balance, parent, false);
        return new ViewHolder(roommateView, context);
    }

    @Override
    public void onBindViewHolder(@NonNull BalanceAdapter.ViewHolder holder, int position)
    {
        String username = _namesAndBalances.get(position).first;
        double balance = _namesAndBalances.get(position).second;
        TextView usernameTextView = holder.usernameTextView;
        TextView userBalanceTextView = holder.balanceTextView;
        Context context = holder.context;
        String balanceValueString = String.valueOf(balance);
        String owesString;
        if (balance < 0)
        {
            owesString = context.getString(R.string.owes);
            userBalanceTextView.setTextColor(context.getColor(R.color.red));
        } else
        {
            owesString = context.getString(R.string.is_owed);
            userBalanceTextView.setTextColor(context.getColor(R.color.green));
        }
        usernameTextView.setText(username);
        String balanceString = owesString
                .concat(" ")
                .concat(balanceValueString)
                .concat(context.getString(R.string.currency_sign));
        userBalanceTextView.setText(balanceString);
    }

    @Override
    public int getItemCount()
    {
        return _namesAndBalances.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView usernameTextView, balanceTextView;
        public Context context;

        public ViewHolder(View view, Context context)
        {
            super(view);
            usernameTextView = view.findViewById(R.id.singleUsernameTextView);
            balanceTextView = view.findViewById(R.id.singleUserBalanceTextView);
            this.context = context;
        }
    }
}
