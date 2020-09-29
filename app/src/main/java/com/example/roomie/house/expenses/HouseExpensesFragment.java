package com.example.roomie.house.expenses;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomie.FirestoreJob;
import com.example.roomie.MovableFloatingActionButton;
import com.example.roomie.R;
import com.example.roomie.User;
import com.example.roomie.house.HouseActivityViewModel;
import com.example.roomie.repositories.GetHouseRoomiesJob;
import com.example.roomie.repositories.HouseRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseExpensesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseExpensesFragment extends Fragment implements ExpenseAdapter.OnExpenseListener,
        ExpenseAdapter.OnReceiptListener
{
    private RecyclerView expensesERecyclerView, balancesRecyclerView;
    private TextView myBalanceTextView, houseBalanceTextView;
    private RecyclerView.Adapter<ExpenseAdapter.ViewHolder> expenseAdapter = null;
    private MovableFloatingActionButton addExpenseButton;
    private View balanceBubble;
    private Button settleExpensesButton;
    private BalanceDialogFragment balanceDialogFragment;


    private HouseActivityViewModel houseActivityViewModel;
    private HouseExpensesViewModel viewModel;
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
        getRoommateNumber();
        LiveData<AllExpensesJob> job =
                viewModel.getExpenses(houseActivityViewModel.getHouse().getId());
        job.observe(getViewLifecycleOwner(), allExpensesJob -> {
            if (allExpensesJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS)
            {
                setUpUI(allExpensesJob, v);
            }
        });
        balanceDialogFragment = BalanceDialogFragment.newInstance(this);
        return v;
    }

    private void setUpUI(AllExpensesJob allExpensesJob, View view)
    {
        expenses = (ArrayList<Expense>) allExpensesJob.getExpenses();
        ExpenseByNewComparator s = new ExpenseByNewComparator();
        Collections.sort(expenses, s);
        expenseAdapter = new ExpenseAdapter(expenses, HouseExpensesFragment.this,
                HouseExpensesFragment.this, this.getContext());
        expensesERecyclerView = view.findViewById(R.id.expensesRecyclerView);
        expensesERecyclerView.setAdapter(expenseAdapter);
        expensesERecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        houseBalanceTextView = view.findViewById(R.id.houseBalanceTextView);
        myBalanceTextView = view.findViewById(R.id.myBalanceAmountTextView);
        balanceBubble = view.findViewById(R.id.balanceBubble);


        HouseExpensesFragment houseExpensesFragment = this;
        balanceBubble.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                balanceDialogFragment.showDialog();
            }
        });
    }

    private void handleBalances()
    {
        String houseSpendingAmountString = String.valueOf(getHouseSpending());
        String houseSpendingString =
                getString(R.string.total_house_spending)
                        .concat(" ").concat(houseSpendingAmountString)
                        .concat(getString(R.string.currency_sign));
        houseBalanceTextView.setText(houseSpendingString);
        double myBalance = getBalanceByUid(auth.getUid());
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
        setUpAddExpenseButton(view);
        setUpSettleExpensesButton(view);
    }

    private void setUpSettleExpensesButton(View view)
    {
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
                            //TODO: check if this is unnecessary
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
                        expenseAdapter.notifyDataSetChanged();
                        handleBalances();
                    });
                }
            }
        });
    }

    private void setUpAddExpenseButton(View view)
    {
        addExpenseButton = view.findViewById(R.id.expensesFab);
        addExpenseButton.setOnClickListener(view1 -> {
            if (view1 != null)
            {
                navController.navigate(R.id.action_house_expenses_fragment_dest_to_newExpenseFragment);
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


    public double getSpendingByUid(String uid)
    {
        double spending = 0;
        for (Expense expense : expenses)
        {
            if (uid.equals(expense.get_payerID()) && !expense.is_isSettled())
            {
                spending += expense.get_cost();
            }
        }
        return spending;

    }

    public double getBalanceByUid(String uid)
    {
        double balance = getSpendingByUid(uid) - getHouseSpending() / numberOfRoommates;
        return Math.floor(balance * 100) / 100;
    }

    private void getRoommateNumber()
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

//    private void showSortDialog()
//    {
//        ArrayList<Pair<String, Double>> namesAndBalances = new ArrayList<>();
////        RecyclerView.Adapter<BalanceAdapter.ViewHolder> adapter = null;
//        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_all_user_balances, null);
//        LiveData<GetHouseRoomiesJob> job =
//                HouseRepository.getInstance().getHouseRoomies(houseActivityViewModel.getHouse().getId());
//        job.observe(getViewLifecycleOwner(), getHouseRoomiesJob -> {
//            switch (getHouseRoomiesJob.getJobStatus())
//            {
//                case SUCCESS:
//                    for (User user : getHouseRoomiesJob.getRoomiesList())
//                    {
//                        String username = user.getUsername();
//                        double userBalance = getBalanceByUid(user.getUid());
//                        Pair<String, Double> usernameAndBalance = new Pair<>(username, userBalance);
//                        namesAndBalances.add(usernameAndBalance);
//                    }
//                    BalanceAdapter balanceAdapter = new BalanceAdapter(namesAndBalances);
//                    balancesRecyclerView = customLayout.findViewById(R.id.balances_recycler_view);
//                    balancesRecyclerView.setAdapter(balanceAdapter);
//                    balancesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.setView(customLayout);
//                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                    alertDialog.show();
//                case ERROR:
//                    //TODO: implement
//            }
//        });
//
//    }
//
//    private void getNamesAndBalancesPairs(ArrayList<Pair<String, Double>> namesAndBalances)
//    {
//        LiveData<GetHouseRoomiesJob> job =
//                HouseRepository.getInstance().getHouseRoomies(houseActivityViewModel.getHouse().getId());
//        job.observe(getViewLifecycleOwner(), getHouseRoomiesJob -> {
//            switch (getHouseRoomiesJob.getJobStatus())
//            {
//                case SUCCESS:
//                    for (User user : getHouseRoomiesJob.getRoomiesList())
//                    {
//                        String username = user.getUsername();
//                        double userBalance = getBalanceByUid(user.getUid());
//                        Pair<String, Double> usernameAndBalance = new Pair<>(username, userBalance);
//                        namesAndBalances.add(usernameAndBalance);
//                    }
//                case ERROR:
//                    //TODO: implement
//            }
//        });
//    }

    public static class BalanceDialogFragment extends DialogFragment
    {
        private RecyclerView mRecyclerView;
        private BalanceAdapter adapter;
        private HouseExpensesFragment parent;

        public static BalanceDialogFragment newInstance(HouseExpensesFragment parent)
        {
            BalanceDialogFragment f = new BalanceDialogFragment();
            f.setParent(parent);
            return f;
        }

        private void setParent(HouseExpensesFragment parent)
        {
            this.parent = parent;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View v = inflater.inflate(R.layout.dialog_all_user_balances, container, false);
            mRecyclerView = v.findViewById(R.id.balances_recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
            setUpDialog();
            return v;
        }

        private void setUpDialog()
        {
            ArrayList<Pair<String, Double>> namesAndBalances = new ArrayList<>();
            LiveData<GetHouseRoomiesJob> job =
                    HouseRepository.getInstance().getHouseRoomies(parent.houseActivityViewModel.getHouse().getId());
            job.observe(getViewLifecycleOwner(), getHouseRoomiesJob -> {
                switch (getHouseRoomiesJob.getJobStatus())
                {
                    case SUCCESS:
                        for (User user : getHouseRoomiesJob.getRoomiesList())
                        {
                            String username = user.getUsername();
                            double userBalance = parent.getBalanceByUid(user.getUid());
                            Pair<String, Double> usernameAndBalance = new Pair<>(username, userBalance);
                            namesAndBalances.add(usernameAndBalance);
                        }
                        adapter = new BalanceAdapter(namesAndBalances);
                        mRecyclerView.setAdapter(adapter);
                    case ERROR:
                        //TODO: implement
                }
            });
        }

        void showDialog()
        {
            FragmentManager fm = parent.getParentFragmentManager();
            this.show(fm, "dialog");
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
        {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            return dialog;
        }
    }


}