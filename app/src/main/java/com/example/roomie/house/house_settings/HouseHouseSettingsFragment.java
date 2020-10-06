package com.example.roomie.house.house_settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roomie.repositories.GetHouseRoomiesJob;
import com.example.roomie.R;
import com.example.roomie.house.HouseActivityViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseHouseSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseHouseSettingsFragment extends Fragment {

    private NavController navController;

    private HouseActivityViewModel houseActivityViewModel;

    private HouseSettingsViewModel houseSettingsViewModel;

    private TextView houseName;

    private TextView houseAddress;

    private TextView houseDesc;

    private Button editHouseInfoButton;

    private FrameLayout loadingOverlay;

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

        navController = NavHostFragment.findNavController(this);

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
        loadingOverlay = view.findViewById(R.id.house_settings_loading_overlay);

        // init recycler view
        roomiesRecylcerView = view.findViewById(R.id.house_settings_roomies_recycler_view);
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        roomiesRecylcerView.setLayoutManager(layoutManager);
        roomieAdapter = new RoomieAdapter(getContext());
        roomiesRecylcerView.setAdapter(roomieAdapter);
    }

    private void setListeners() {
        editHouseInfoButton.setOnClickListener(this::gotoEditHouseInfo);
        roomieAdapter.setItemClickCallback(this::gotoRoomieProfile);
    }

    private void setContent() {
        toggleLoadingOverlay(true);
        houseName.setText(houseActivityViewModel.getHouse().getName());
        houseAddress.setText(houseActivityViewModel.getHouse().getAddress());
        houseDesc.setText(houseActivityViewModel.getHouse().getDesc());
        houseSettingsViewModel.getRoomiesList(houseActivityViewModel.getHouse().getId())
                .observe(getViewLifecycleOwner(), this::roomiesListObserver);
    }

    private void roomiesListObserver(GetHouseRoomiesJob job) {
        switch (job.getJobStatus()) {
            case SUCCESS:
                toggleLoadingOverlay(false);
                roomieAdapter.setRoomiesList(job.getRoomiesList());
                break;
            case ERROR:
                toggleLoadingOverlay(false);
                Toast.makeText(getContext(), "There was an error while fetching the roomies.", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    private void gotoEditHouseInfo(View view) {
        navController.navigate(R.id.action_house_settings_fragment_dest_to_house_edit_house_settings_fragment_dest);
    }

    /**
     * This function is used as a callback for roomie list item click.
     * @param userId The user id to pass to the roomie profile fragment.
     */
    public void gotoRoomieProfile(String userId) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("userId", userId);
        navController.navigate(R.id.action_house_settings_fragment_dest_to_roomie_profile_fragment_dest, bundle);
    }

    private void toggleLoadingOverlay(boolean isVisible) {
        if (isVisible) {
            loadingOverlay.setVisibility(View.VISIBLE);
            editHouseInfoButton.setEnabled(false);
        } else {
            loadingOverlay.setVisibility(View.GONE);
            editHouseInfoButton.setEnabled(true);
        }
    }
}