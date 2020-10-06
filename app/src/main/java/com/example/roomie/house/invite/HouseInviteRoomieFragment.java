package com.example.roomie.house.invite;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.roomie.R;
import com.example.roomie.house.HouseActivityViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseInviteRoomieFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseInviteRoomieFragment extends Fragment {

    private HouseActivityViewModel houseActivityViewModel;

    private InviteRoomieViewModel inviteRoomieViewModel;

    private Button inviteRoomieButton;

    public HouseInviteRoomieFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment InviteRoomieFragment.
     */
    public static HouseInviteRoomieFragment newInstance() {
        return new HouseInviteRoomieFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inviteRoomieViewModel = new ViewModelProvider(this).get(InviteRoomieViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_house_invite_roomie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        setUIElements(view);
        setListeners();
    }

    private void setUIElements(View view) {
        inviteRoomieButton = view.findViewById(R.id.house_invite_roomie_fragment_invite_roomie_button);
    }

    private void setListeners() {
        inviteRoomieButton.setOnClickListener(this::doInviteRoomie);
    }

    private void doInviteRoomie(View view) {
        toggleLoadingLayout(true);
        LiveData<CreateInviteJob> job = inviteRoomieViewModel.getInviteLink(houseActivityViewModel.getHouse());

        job.observe(getViewLifecycleOwner(), createInviteJob -> {
            switch (createInviteJob.getJobStatus()) {
                case SUCCESS:
                    toggleLoadingLayout(false);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(
                            Intent.EXTRA_TEXT,
                            String.format(getString(R.string.house_invite_roomie_invite_message),
                                    createInviteJob.getInvite().returnInviteLink())
                    );
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.house_invite_roomie_chooser_title));
                    startActivity(shareIntent);
                    break;
                case ERROR:
                    toggleLoadingLayout(false);
                    Toast.makeText(getContext(),getString(R.string.house_invite_roomie_error), Toast.LENGTH_LONG).show();
                    break;
                case IN_PROGRESS:
                default:
                    break;
            }
        });
    }

    private void toggleLoadingLayout(boolean isLoading) {
        if (isLoading) {
            inviteRoomieButton.setEnabled(false);
        } else {
            inviteRoomieButton.setEnabled(true);
        }
    }
}