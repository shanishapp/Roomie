package com.example.roomie.house.expenses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomie.FirestoreJob;
import com.example.roomie.MovableFloatingActionButton;
import com.example.roomie.R;
import com.example.roomie.house.HouseActivityViewModel;
import com.example.roomie.repositories.GetHouseRoomiesJob;
import com.example.roomie.repositories.HouseRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseExpensesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseExpensesFragment extends Fragment implements ExpenseAdapter.OnExpenseListener,
        ExpenseAdapter.OnReceiptListener
{
    private RecyclerView recyclerView;
    private TextView myBalanceTextView;
    private TextView houseBalanceTextView;
    private RecyclerView.Adapter<ExpenseAdapter.ViewHolder> adapter = null;
    private HouseActivityViewModel houseActivityViewModel;
    private HouseExpensesViewModel viewModel;
    private MovableFloatingActionButton addExpenseButton;
    private Button settleExpensesButton;
    private ArrayList<Expense> expenses;
    private int numberOfRoommates;
    private NavController navController;
    private FirebaseAuth auth;

    public HouseExpensesFragment()
    {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment houseExpensesFragment.
     */
    public static HouseExpensesFragment newInstance()
    {
        return new HouseExpensesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        auth = FirebaseAuth.getInstance();
        View v = inflater.inflate(R.layout.fragment_house_expenses, container, false);
        houseActivityViewModel =
                new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        viewModel = new ViewModelProvider(this).get(HouseExpensesViewModel.class);
        getRoomateNumber();
        LiveData<AllExpensesJob> job =
                viewModel.getExpenses(houseActivityViewModel.getHouse().getId());
        job.observe(getViewLifecycleOwner(), allExpensesJob -> {
            if (allExpensesJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS)
            {
                expenses = (ArrayList<Expense>) allExpensesJob.getExpenses();
                adapter = new ExpenseAdapter(expenses, HouseExpensesFragment.this,
                        HouseExpensesFragment.this);
                recyclerView = v.findViewById(R.id.expensesRecyclerView);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                houseBalanceTextView = v.findViewById(R.id.houseBalanceTextView);
                myBalanceTextView = v.findViewById(R.id.myBalanceAmountTextView);

            }
        });
        return v;
    }

    private void handleBalances()
    {
        String houseSpendingAmountString = String.valueOf(getHouseSpending());
        String houseSpendingString =
                getString(R.string.total_house_spending)
                        .concat(" ").concat(houseSpendingAmountString)
                        .concat(getString(R.string.currency_sign));
        houseBalanceTextView.setText(houseSpendingString);
        double myBalance = getMyBalance();
        String myBalanceAmountString = String.valueOf(Math.abs(myBalance));
        String myBalancePrefixString;
        int color;
        if (myBalance < 0)
        {
            color = ContextCompat.getColor(getContext(), R.color.red);
            myBalancePrefixString = getString(R.string.you_owe);
        } else
        {
            color = ContextCompat.getColor(getContext(), R.color.green);
            myBalancePrefixString = getString(R.string.you_are_owed);
        }
        String myBalanceString = myBalancePrefixString
                .concat(" ")
                .concat(myBalanceAmountString)
                .concat(getString(R.string.currency_sign));
        myBalanceTextView.setTextColor(color);
        myBalanceTextView.setText(myBalanceString);
        myBalanceTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        addExpenseButton = view.findViewById(R.id.expensesFab);
        addExpenseButton.setOnClickListener(view1 -> {
            if (view1 != null)
            {
                navController.navigate(R.id.action_house_expenses_fragment_dest_to_newExpenseFragment);
            }
        });
        settleExpensesButton = view.findViewById(R.id.settleUpButton);
        settleExpensesButton.setOnClickListener(view2 ->
        {
            if (view2 != null)
            {
                for (Expense expense : expenses)
                {
                    LiveData<CreateNewExpenseJob> settleJob = viewModel.settleExpense(expense,
                            houseActivityViewModel.getHouse().getId());
                    settleJob.observe(getViewLifecycleOwner(), CreateNewExpenseJob -> {
                        if (CreateNewExpenseJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS)
                        {
                            //TODO: check if this is surplus
                            LiveData<AllExpensesJob> updateExpensesJob =
                                    viewModel.getExpenses(houseActivityViewModel.getHouse().getId());
                            viewModel.getExpenses(houseActivityViewModel.getHouse().getId());
                            updateExpensesJob.observe(getViewLifecycleOwner(), allExpensesJob -> {
                                if (allExpensesJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS)
                                {
                                    expenses = (ArrayList<Expense>) allExpensesJob.getExpenses();
                                }
                            });
                        }
                        adapter.notifyDataSetChanged();
                        handleBalances();
                    });
                }
            }
        });
    }

    private void settleExpenses()
    {

    }

    @Override
    public void onExpenseClick(int pos)
    {
    }

    @Override
    public void onReceiptClick()
    {
        //TODO: Implement
    }

    public double getHouseSpending()
    {
        double totalCost = 0;
        for (Expense expense : expenses)
        {
            if (!expense.is_isSettled())
            {
                totalCost += expense.get_cost();
            }
        }
        return totalCost;
    }

    public double getMySpending()
    {
        String myID = auth.getUid();
        double mySpending = 0;
        for (Expense expense : expenses)
        {
            if (myID.equals(expense.get_payerID()) && !expense.is_isSettled())
            {
                mySpending += expense.get_cost();
            }
        }
        return mySpending;
    }

    public double getMyBalance()
    {
        double balance = getMySpending() - getHouseSpending() / numberOfRoommates;
        return Math.floor(balance * 100) / 100;
    }

    private void getRoomateNumber()
    {
        LiveData<GetHouseRoomiesJob> job =
                HouseRepository.getInstance().getHouseRoomies(houseActivityViewModel.getHouse().getId());
        job.observe(getViewLifecycleOwner(), getHouseRoomiesJob -> {
            switch (getHouseRoomiesJob.getJobStatus())
            {
                case SUCCESS:
                    numberOfRoommates = getHouseRoomiesJob.getRoomiesList().size();
                    handleBalances();
                case ERROR:
                    //TODO: implement
            }
        });
    }
}