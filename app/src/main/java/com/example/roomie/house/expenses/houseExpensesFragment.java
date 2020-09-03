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
import com.example.roomie.house.HouseActivityViewModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link houseExpensesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class houseExpensesFragment extends Fragment implements ExpenseAdapter.OnExpenseListener
{
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter = null;
    private HouseActivityViewModel houseActivityViewModel;
    private houseExpensesViewModel viewModel;
    private ArrayList<Expense> expenses;
    private MovableFloatingActionButton button;
    private NavController navController;

    public houseExpensesFragment()
    {
        // Required empty public constructor

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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_house_expenses, container, false);
        //init variables
        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        viewModel = new ViewModelProvider(this).get(houseExpensesViewModel.class);
        LiveData<allExpensesJob> job = viewModel.getExpenses(houseActivityViewModel.getHouse().getId());
        job.observe(getViewLifecycleOwner(), allExpensesJob -> {
            if (allExpensesJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS)
            {
                expenses = (ArrayList<Expense>) allExpensesJob.getExpenses();
                adapter = new ExpenseAdapter(expenses, houseExpensesFragment.this);
                RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        button = view.findViewById(R.id.fab);
        //set up add button
        button.setOnClickListener(view1 -> {
            if(view1 != null){
                navController.navigate(R.id.action_house_chores_fragment_dest_to_expenseFragment);
            }
        });
    }

    @Override
    public void onExpenseClick(int pos)
    {
    }

    //TODO: need a view for total
}