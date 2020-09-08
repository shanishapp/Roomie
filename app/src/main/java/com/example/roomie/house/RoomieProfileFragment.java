package com.example.roomie.house;

import android.net.Uri;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roomie.R;
import com.example.roomie.User;
import com.example.roomie.repositories.GetUserJob;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomieProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomieProfileFragment extends Fragment {

    private HouseActivityViewModel houseActivityViewModel;

    private RoomieProfileViewModel roomieProfileViewModel;

    private TextView roomieName;

    private TextView roomieEmail;

    private ImageView roomieProfilePicture;

    private TextView roomieHouse;

    private TextView roomieBrooms;

    private TextView roomieChores;

    private TextView roomieExpenses;

    private FrameLayout loadingOverlay;


    public RoomieProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RoomieProfileFragment.
     */
    public static RoomieProfileFragment newInstance() {
        return new RoomieProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_roomie_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        roomieProfileViewModel = new ViewModelProvider(this).get(RoomieProfileViewModel.class);

        setUIElements(view);
        setContent();
    }

    private void setUIElements(View view) {
        roomieName = view.findViewById(R.id.roomie_profile_name);
        roomieEmail = view.findViewById(R.id.roomie_profile_email);
        roomieProfilePicture = view.findViewById(R.id.roomie_profile_profile_picture);
        roomieHouse = view.findViewById(R.id.roomie_profile_house_text);
        roomieBrooms = view.findViewById(R.id.roomie_profile_brooms_value);
        roomieChores = view.findViewById(R.id.roomie_profile_chores_value);
        roomieExpenses = view.findViewById(R.id.roomie_profile_expenses_value);
        loadingOverlay = view.findViewById(R.id.roomie_profile_loading_overlay);
    }

    private void setContent() {
        String userId = getArguments().getString("userId");
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(getContext(), "Invalid user id.", Toast.LENGTH_LONG).show();
            return;
        }

        toggleLoadingOverlay(true);
        LiveData<GetUserJob> job = roomieProfileViewModel.getRoomie(userId);
        job.observe(getViewLifecycleOwner(), getUserJob -> {
            switch (getUserJob.getJobStatus()) {
                case SUCCESS:
                    User user = getUserJob.getUser();
                    roomieName.setText(user.getUsername());
                    roomieEmail.setText(user.getEmail());
                    BidiFormatter bidiFormatter = BidiFormatter.getInstance();
                    String houseName = houseActivityViewModel.getHouse().getName();
                    roomieHouse.setText(String.format(
                            getString(R.string.roomie_profile_fragment_roomie_in), bidiFormatter.unicodeWrap(houseName)
                    ));
                    roomieBrooms.setText("0");
                    roomieChores.setText("0");
                    roomieExpenses.setText("0");
                    loadProfilePicture(user.getProfilePicture());
                    break;
                case ERROR:
                    Toast.makeText(getContext(), "Error fetching roomie data.", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        });

    }

    private void toggleLoadingOverlay(boolean isVisible) {
        if (isVisible) {
            loadingOverlay.setVisibility(View.VISIBLE);
        } else {
            loadingOverlay.setVisibility(View.GONE);
        }
    }

    private void loadProfilePicture(String profilePicture) {
        if (profilePicture == null || profilePicture.isEmpty()) {
            toggleLoadingOverlay(false);
            return;
        }

        Picasso.get().load(profilePicture)
                .resize(256, 256)
                .centerCrop()
                .into(roomieProfilePicture, new Callback() {
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
}