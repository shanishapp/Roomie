package com.example.roomie.house.user_profile;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.BidiFormatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roomie.R;
import com.example.roomie.house.HouseActivityViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseUserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseUserProfileFragment extends Fragment {

    NavController navController;

    private HouseActivityViewModel houseActivityViewModel;

    private UserProfileViewModel userProfileViewModel;

    private TextView userName;

    private TextView userEmail;

    private ImageView userProfilePicture;

    private TextView userHouse;

    private TextView userBrooms;

    private TextView userChores;

    private TextView userExpenses;

    private Button editProfileButton;

    private FrameLayout loadingOverlay;


    public HouseUserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HouseUserProfileFragment.
     */
    public static HouseUserProfileFragment newInstance() {
        return new HouseUserProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_house_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);

        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);
        setUIElements(view);
        setListeners();
        setContent();
    }

    private void setUIElements(View view) {
        userName = view.findViewById(R.id.user_profile_name);
        userEmail = view.findViewById(R.id.user_profile_email);
        userProfilePicture = view.findViewById(R.id.user_profile_profile_picture);
        userHouse = view.findViewById(R.id.user_profile_house_text);
        userBrooms = view.findViewById(R.id.user_profile_brooms_value);
        userChores = view.findViewById(R.id.user_profile_chores_value);
        userExpenses = view.findViewById(R.id.user_profile_expenses_value);
        editProfileButton = view.findViewById(R.id.user_profile_edit_profile_button);
        loadingOverlay = view.findViewById(R.id.user_profile_loading_overlay);
    }

    private void setListeners() {
        editProfileButton.setOnClickListener(this::gotoEditProfile);
    }

    private void setContent() {
        toggleLoadingOverlay(true);
        userProfileViewModel.getUserName().observe(getViewLifecycleOwner(), username -> {
            userName.setText(username);
        });
        userProfileViewModel.getUserEmail().observe(getViewLifecycleOwner(), email -> {
            userEmail.setText(email);
        });
        userProfileViewModel.getUserProfilePicture().observe(getViewLifecycleOwner(), profilePicture -> {
            if (profilePicture == null) {
                loadDefaultProfilePicture();
                return;
            }

            loadProfilePicture(profilePicture);
        });

        BidiFormatter bidiFormatter = BidiFormatter.getInstance();
        String houseName = houseActivityViewModel.getHouse().getName();
        userHouse.setText(String.format(
                getString(R.string.house_user_profile_fragment_roomie_in), bidiFormatter.unicodeWrap(houseName)
        ));

        userProfileViewModel.getUserBrooms().observe(getViewLifecycleOwner(), brooms -> {
            userBrooms.setText(String.valueOf(brooms));
        });
        userProfileViewModel.getUserChores().observe(getViewLifecycleOwner(), chores -> {
            userChores.setText(String.valueOf(chores));
        });
        userProfileViewModel.getUserExpenses().observe(getViewLifecycleOwner(), expenses -> {
            userExpenses.setText(String.valueOf(expenses));
        });
    }

    private void gotoEditProfile(View view) {
        navController.navigate(R.id.action_house_user_profile_fragmnet_dest_to_house_edit_user_profile_fragment);
    }

    private void toggleLoadingOverlay(boolean isVisible) {
        if (isVisible) {
            loadingOverlay.setVisibility(View.VISIBLE);
            editProfileButton.setEnabled(false);
        } else {
            loadingOverlay.setVisibility(View.GONE);
            editProfileButton.setEnabled(true);
        }
    }

    private void loadDefaultProfilePicture() {
        Picasso.get().load(R.drawable.avatar_1).into(this.userProfilePicture, new Callback() {
            @Override
            public void onSuccess() {
                toggleLoadingOverlay(false);
            }

            @Override
            public void onError(Exception e) {
                toggleLoadingOverlay(false);
            }
        });
    }

    private void loadProfilePicture(Uri profilePicture) {
        Picasso.get().load(profilePicture)
                .resize(256, 256)
                .centerCrop()
                .into(this.userProfilePicture, new Callback() {
                    @Override
                    public void onSuccess() {
                        toggleLoadingOverlay(false);
                    }

                    @Override
                    public void onError(Exception e) {
                        loadDefaultProfilePicture();
                    }
                });
    }
}