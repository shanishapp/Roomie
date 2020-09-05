package com.example.roomie.house.house_settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roomie.GetHouseRoomiesJob;
import com.example.roomie.R;
import com.example.roomie.house.HouseActivityViewModel;
import com.example.roomie.splash.GetUserHouseJob;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseHouseSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseHouseSettingsFragment extends Fragment {

    private HouseActivityViewModel houseActivityViewModel;

    private HouseSettingsViewModel houseSettingsViewModel;

    private TextView houseName;

    private TextView houseAddress;

    private TextView houseDesc;

    private Button editHouseInfoButton;

    private RecyclerView roomiesRecylcerView;

    private RecyclerView.LayoutManager layoutManager;

    private RoomieAdapter roomieAdapter;


    public HouseHouseSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HouseHouseSettingsFragment.
     */
    public static HouseHouseSettingsFragment newInstance() {
        return new HouseHouseSettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_house_house_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        houseSettingsViewModel = new ViewModelProvider(requireActivity()).get(HouseSettingsViewModel.class);
        setUIElements(view);
        setListeners();
        setContent();
    }

    private void setUIElements(View view) {
        houseName = view.findViewById(R.id.house_settings_house_name);
        houseAddress = view.findViewById(R.id.house_settings_house_address);
        houseDesc = view.findViewById(R.id.house_settings_house_desc);
        editHouseInfoButton = view.findViewById(R.id.house_settings_edit_house_info_button);

        // init recycler view
        roomiesRecylcerView = view.findViewById(R.id.house_settings_roomies_recycler_view);
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        roomiesRecylcerView.setLayoutManager(layoutManager);
        roomieAdapter = new RoomieAdapter(getContext());
        roomiesRecylcerView.setAdapter(roomieAdapter);
    }

    private void setListeners() {
        editHouseInfoButton.setOnClickListener(this::gotoEditHouseInfo);
    }

    private void setContent() {
        houseName.setText(houseActivityViewModel.getHouse().getName());
        houseAddress.setText(houseActivityViewModel.getHouse().getAddress());
        houseDesc.setText(houseActivityViewModel.getHouse().getDesc());
        houseSettingsViewModel.getRoomiesList(houseActivityViewModel.getHouse().getId())
                .observe(getViewLifecycleOwner(), this::roomiesListObserver);
    }

    private void roomiesListObserver(GetHouseRoomiesJob job) {
        switch (job.getJobStatus()) {
            case SUCCESS:
                roomieAdapter.setRoomiesList(job.getRoomiesList());
                break;
            case ERROR:
                Toast.makeText(getContext(), "There was an error while fetching the roomies.", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    private void gotoEditHouseInfo(View view) {

    }
}