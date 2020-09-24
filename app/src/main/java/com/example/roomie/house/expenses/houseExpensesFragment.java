package com.example.roomie.house.expenses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.roomie.User;
import com.example.roomie.house.HouseActivityViewModel;
import com.example.roomie.repositories.GetHouseRoomiesJob;
import com.example.roomie.repositories.HouseRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link houseExpensesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class houseExpensesFragment extends Fragment implements ExpenseAdapter.OnExpenseListener,
        ExpenseAdapter.OnReceiptListener
{
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<ExpenseAdapter.ViewHolder> adapter = null;
    private HouseActivityViewModel houseActivityViewModel;
    private houseExpensesViewModel viewModel;
    private ArrayList<Expense> expenses;
    private int numberOfRoommates;
    private MovableFloatingActionButton addExpenseButton;
    private NavController navController;
    private FirebaseAuth auth;

    public houseExpensesFragment()
    {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment houseExpensesFragment.
     */
    public static houseExpensesFragment newInstance()
    {
        return new houseExpensesFragment();
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
        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        viewModel = new ViewModelProvider(this).get(houseExpensesViewModel.class);
        getRoomateNumber();
        LiveData<allExpensesJob> job = viewModel.getExpenses(houseActivityViewModel.getHouse().getId());
        job.observe(getViewLifecycleOwner(), allExpensesJob -> {
            if (allExpensesJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS)
            {
                expenses = (ArrayList<Expense>) allExpensesJob.getExpenses();
                adapter = new ExpenseAdapter(expenses, houseExpensesFragment.this, houseExpensesFragment.this);
                RecyclerView recyclerView = v.findViewById(R.id.expensesRecyclerView);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });
        return v;
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

    public double getTotalHouseExpenses()
    {
        double totalCost = 0;
        for (Expense expense : expenses)
        {
            totalCost += expense.get_cost();
        }
        return totalCost;
    }

    public double getMySpending()
    {
        String myID = auth.getUid();
        AtomicReference<Double> mySpending = new AtomicReference<>((double) 0);
        LiveData<allExpensesJob> job = viewModel.getExpenses(houseActivityViewModel.getHouse().getId());
        job.observe(getViewLifecycleOwner(), allExpensesJob -> {
            if (allExpensesJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS)
            {
                expenses = (ArrayList<Expense>) allExpensesJob.getExpenses();
                for (Expense expense : expenses)
                {
                    if (myID.equals(expense.get_payerID()))
                    {
                        mySpending.updateAndGet(v -> v + expense.get_cost());
                    }
                }
            }
        });
        return mySpending.get();
    }

    public double getMyBalance()
    {
        return getTotalHouseExpenses() / numberOfRoommates - getMySpending();
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
                case ERROR:
                    //TODO: implement
            }
        });
    }

    //TODO: add a view for total debts
}