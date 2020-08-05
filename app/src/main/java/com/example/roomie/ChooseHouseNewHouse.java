package com.example.roomie;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.text.BidiFormatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChooseHouseNewHouse#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseHouseNewHouse extends Fragment {

    private NewHouseViewModel newHouseViewModel;

    private EditText houseNameInput;

    private EditText houseAddressInput;

    private EditText houseDescInput;

    private Button createNewHouseBtn;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUIElements(view);
        setListeners();
    }

    private void setUIElements(View view) {
        this.houseNameInput = view.findViewById(R.id.house_name_input);
        this.houseAddressInput = view.findViewById(R.id.house_address_input);
        this.houseDescInput = view.findViewById(R.id.house_description_input);
        this.createNewHouseBtn = view.findViewById(R.id.create_new_house_btn);
    }

    private void setListeners() {
        createNewHouseBtn.setOnClickListener(this::doCreateNewHouse);
    }

    private void doCreateNewHouse(View view) {
        String houseName = houseNameInput.getText().toString();
        String houseAddress = houseAddressInput.getText().toString();
        String houseDesc = houseDescInput.getText().toString();

        if (!FormValidator.isValidHouseName(houseName)) {
            Toast.makeText(getContext(), getString(R.string.create_new_house_err_invalid_house_name),
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (!FormValidator.isValidHouseAddress(houseAddress)) {
            Toast.makeText(getContext(), getString(R.string.create_new_house_err_invalid_house_address),
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (!FormValidator.isValidHouseDesc(houseDesc)) {
            Toast.makeText(getContext(), getString(R.string.create_new_house_err_invalid_house_desc),
                    Toast.LENGTH_LONG).show();
            return;
        }

        LiveData<CreateNewHouseJob> job = newHouseViewModel.createNewHouse(houseName, houseAddress, houseDesc);
        // TODO check if this is the right arg to put in observe (and not getViewLifecycleOwner())
        // TODO do we need to remove the observer after we get the result?
        job.observe(this, createNewHouseJob -> {
            switch (createNewHouseJob.getJobStatus()) {
                case IN_PROGRESS:
                    createNewHouseBtn.setEnabled(false);
                    break;
                case SUCCESS:
                    BidiFormatter myBidiFormatter = BidiFormatter.getInstance();
                    String successMsg = String.format(getString(R.string.create_new_house_success),
                            myBidiFormatter.unicodeWrap(houseName));
                    Toast.makeText(getContext(), successMsg, Toast.LENGTH_LONG).show();

                    // TODO notify activity and let the activity switch
                    Intent intent = new Intent(getActivity(), HouseActivity.class);
                    intent.putExtra("house", createNewHouseJob.getHouse());
                    getActivity().startActivity(intent);
                    getActivity().finish();
                    break;
                case ERROR:
                    createNewHouseBtn.setEnabled(true);
                    Toast.makeText(getContext(), getString(R.string.create_new_house_err_create_house),
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        });
    }
}