package com.example.roomie.house.expenses;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.roomie.MovableFloatingActionButton;
import com.example.roomie.R;
import com.example.roomie.house.HouseActivityViewModel;
import com.example.roomie.house.chores.HouseChoresFragmentViewModel;
import com.example.roomie.house.chores.chore.Chore;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link houseExpensesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class houseExpensesFragment extends Fragment implements ExpenseAdapter.OnExpenseListener
{




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
        return inflater.inflate(R.layout.fragment_house_expenses, container, false);
    }

    @Override
    public void onExpenseClick(int pos)
    {

    }

    //TODO: need a view for total
}