package com.example.roomie.choose_house;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.BidiFormatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.roomie.LoginActivity;
import com.example.roomie.R;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChooseHouseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseHouseFragment extends Fragment {

    NavController navController;

    private FirebaseAuth auth;

    private TextView welcomeMessage;

    private Button createHouseBtn;

    private Button signOutBtn;

    public ChooseHouseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChooseHouse.
     */
    public static ChooseHouseFragment newInstance() {
        return new ChooseHouseFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_house, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);

        setUIElements();
        setListeners();

        setContent();
    }

    private void setUIElements() {
        welcomeMessage = getView().findViewById(R.id.welcome_msg);
        createHouseBtn = getView().findViewById(R.id.create_house_button);
        signOutBtn = getView().findViewById(R.id.sign_out_btn);
    }

    private void setListeners() {
        createHouseBtn.setOnClickListener(this::gotoCreateHouse);
        signOutBtn.setOnClickListener(this::doSignOut);
    }

    private void setContent() {
        // user should not be null, since we already checked in activity
        FirebaseUser user = auth.getCurrentUser();

        // user is logged in
        BidiFormatter myBidiFormatter = BidiFormatter.getInstance();
        String displayName = user.getDisplayName();
        welcomeMessage.setText(
                String.format(getString(R.string.choose_house_welcome_msg),
                        myBidiFormatter.unicodeWrap(displayName))
        );
    }

    private void doSignOut(View view) {
        // TODO use ViewModel and LiveData to notify activity that sign out was pressed
        AuthUI.getInstance()
                .signOut(getActivity())
                .addOnCompleteListener(task -> {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                });
    }

    private void gotoCreateHouse(View view) {
        navController.navigate(R.id.action_chooseHouse_to_chooseHouseNewHouse);
    }
}