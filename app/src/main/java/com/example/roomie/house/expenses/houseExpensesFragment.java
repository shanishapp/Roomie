package com.example.roomie.house.expenses;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.roomie.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link houseExpensesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class houseExpensesFragment extends Fragment {

    public houseExpensesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment houseExpensesFragment.
     */
    public static houseExpensesFragment newInstance() {
        return new houseExpensesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_house_expenses, container, false);
    }
}