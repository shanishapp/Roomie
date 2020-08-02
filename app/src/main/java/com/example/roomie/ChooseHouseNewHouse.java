package com.example.roomie;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChooseHouseNewHouse#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseHouseNewHouse extends Fragment {

    private NewHouseViewModel newHouseViewModel;

    public ChooseHouseNewHouse() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChooseHouseNewHouse.
     */
    public static ChooseHouseNewHouse newInstance() {
        return new ChooseHouseNewHouse();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newHouseViewModel = new ViewModelProvider(this).get(NewHouseViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_house_new_house, container, false);
    }
}