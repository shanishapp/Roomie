package com.example.roomie.house.feed;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.roomie.R;
import com.example.roomie.house.HouseActivityViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseFeedFragment extends Fragment {

    private HouseActivityViewModel houseActivityViewModel;

    private TextView houseName;

    private TextView houseAddress;

    private TextView houseDesc;

    public HouseFeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HouseFeedFragment.
     */
    public static HouseFeedFragment newInstance() {
        return new HouseFeedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_house_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);

        setUIElements(view);
        setContent();
    }

    private void setUIElements(View rootView) {
        houseName = rootView.findViewById(R.id.house_details_house_name);
        houseAddress = rootView.findViewById(R.id.house_details_house_address);
        houseDesc = rootView.findViewById(R.id.house_details_house_desc);
    }

    private void setContent() {
        houseName.setText(houseActivityViewModel.getHouse().getName());
        houseAddress.setText(houseActivityViewModel.getHouse().getAddress());
        houseDesc.setText(houseActivityViewModel.getHouse().getDesc());
    }
}