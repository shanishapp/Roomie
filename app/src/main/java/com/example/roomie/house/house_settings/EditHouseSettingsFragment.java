package com.example.roomie.house.house_settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.roomie.FirestoreJob;
import com.example.roomie.R;
import com.example.roomie.house.HouseActivityViewModel;
import com.example.roomie.util.FormValidator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditHouseSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditHouseSettingsFragment extends Fragment {

    private NavController navController;

    private HouseActivityViewModel houseActivityViewModel;

    private EditHouseSettingsViewModel editHouseSettingsViewModel;

    private EditText houseName;

    private EditText houseAddress;

    private EditText houseDesc;

    private Button saveChangesButton;

    private FrameLayout loadingOverlay;


    public EditHouseSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EditHouseSettingsFragment.
     */
    public static EditHouseSettingsFragment newInstance() {
        return new EditHouseSettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_house_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);

        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        editHouseSettingsViewModel = new ViewModelProvider(this).get(EditHouseSettingsViewModel.class);

        setUIElements(view);
        setListeners();
        setContent();
    }

    private void setUIElements(View view) {
        houseName = view.findViewById(R.id.edit_house_settings_house_name_value);
        houseAddress = view.findViewById(R.id.edit_house_settings_house_address_value);
        houseDesc = view.findViewById(R.id.edit_house_settings_house_desc_value);
        saveChangesButton = view.findViewById(R.id.edit_house_settings_save_changes_btn);
        loadingOverlay = view.findViewById(R.id.edit_house_settings_loading_overlay);
    }

    private void setListeners() {
        saveChangesButton.setOnClickListener(this::saveChanges);
    }

    private void setContent() {
        houseName.setText(houseActivityViewModel.getHouse().getName());
        houseAddress.setText(houseActivityViewModel.getHouse().getAddress());
        houseDesc.setText(houseActivityViewModel.getHouse().getDesc());
    }

    private void saveChanges(View view) {
        toggleLoadingOverlay(true);
        if (!FormValidator.isValidHouseName(houseName.getText().toString())) {
            toggleLoadingOverlay(false);
            Toast.makeText(getContext(), getString(R.string.edit_house_settings_fragment_invalid_house_name), Toast.LENGTH_LONG).show();
            return;
        }

        if (!FormValidator.isValidHouseAddress(houseAddress.getText().toString())) {
            toggleLoadingOverlay(false);
            Toast.makeText(getContext(), getString(R.string.edit_house_settings_fragment_invalid_house_address), Toast.LENGTH_LONG).show();
            return;
        }

        if (!FormValidator.isValidHouseDesc(houseDesc.getText().toString())) {
            toggleLoadingOverlay(false);
            Toast.makeText(getContext(), getString(R.string.edit_house_settings_fragment_invalid_house_desc), Toast.LENGTH_LONG).show();
            return;
        }

        LiveData<FirestoreJob> job = editHouseSettingsViewModel.updateHouseInfo(
                houseActivityViewModel.getHouse().getId(),
                houseName.getText().toString(),
                houseAddress.getText().toString(),
                houseDesc.getText().toString()
        );
        job.observe(getViewLifecycleOwner(), firestoreJob -> {
            switch (firestoreJob.getJobStatus()) {
                case SUCCESS:
                    // update house activity view model
                    houseActivityViewModel.getHouse().setName(houseName.getText().toString());
                    houseActivityViewModel.getHouse().setAddress(houseAddress.getText().toString());
                    houseActivityViewModel.getHouse().setDesc(houseDesc.getText().toString());

                    Toast.makeText(getContext(), getString(R.string.edit_house_settings_fragment_saved_successfully), Toast.LENGTH_LONG)
                            .show();
                    navController.navigate(R.id.action_house_edit_house_settings_fragment_dest_to_house_settings_fragment_dest);
                    break;
                case ERROR:
                    toggleLoadingOverlay(false);
                    Toast.makeText(getContext(), "Error while saving changes, please try again.", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        });
    }

    private void toggleLoadingOverlay(boolean isVisible) {
        if (isVisible) {
            loadingOverlay.setVisibility(View.VISIBLE);
            saveChangesButton.setEnabled(false);
        } else {
            loadingOverlay.setVisibility(View.GONE);
            saveChangesButton.setEnabled(true);
        }
    }
}