package com.roomiemain.roomie.join_house;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.BidiFormatter;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.House;
import com.roomiemain.roomie.R;
import com.roomiemain.roomie.SignInActivity;
import com.roomiemain.roomie.house.HouseActivity;
import com.roomiemain.roomie.repositories.UserRepository;
import com.roomiemain.roomie.splash.GetUserHouseJob;
import com.roomiemain.roomie.splash.SplashScreenActivity;
import com.google.firebase.auth.FirebaseAuth;

public class JoinHouseActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private JoinHouseViewModel joinHouseViewModel;

    private ProgressBar progressBar;

    private TextView joinExplanation;

    private TextView houseName;

    private TextView joinConfirm;

    private Button joinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_house);

        auth = FirebaseAuth.getInstance();
        // if the user is not signed in - redirect him to sign in
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(JoinHouseActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        joinHouseViewModel = new ViewModelProvider(this).get(JoinHouseViewModel.class);

        joinHouseViewModel.setInvitationId(null);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(SplashScreenActivity.INVITATION_ID_EXTRA)) {
                joinHouseViewModel.setInvitationId(extras.getString(SplashScreenActivity.INVITATION_ID_EXTRA));
            }
        }

        // if we don't have an invitation id - redirect to splash screen
        if (joinHouseViewModel.getInvitationId() == null) {
            Intent intent = new Intent(JoinHouseActivity.this, SplashScreenActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setUIElements();
        setListeners();
        checkUserHasHouse();
    }

    private void setUIElements() {
        progressBar = findViewById(R.id.join_house_activity_progress_bar);
        joinExplanation = findViewById(R.id.join_house_activity_explanation);
        houseName = findViewById(R.id.join_house_activity_house_name);
        joinConfirm = findViewById(R.id.join_house_activity_confirm);
        joinButton = findViewById(R.id.join_house_activity_join_button);
    }

    private void setListeners() {
        joinButton.setOnClickListener(this::doJoinHouse);
    }

    private void checkUserHasHouse() {
        LiveData<GetUserHouseJob> job = joinHouseViewModel.getUserHouse();
        job.observe(this, getUserHouseJob -> {
            switch (getUserHouseJob.getJobStatus()) {
                case SUCCESS:
                    if (getUserHouseJob.isUserHasHouse()) {
                        progressBar.setVisibility(View.GONE);
                        joinExplanation.setText(R.string.join_house_activity_already_user_has_house);
                        joinExplanation.setVisibility(View.VISIBLE);
//                        setContent();
                    } else {
                        setContent();
                    }
                    break;
                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    joinExplanation.setText("An error occurred, please try again.");
                    joinExplanation.setVisibility(View.VISIBLE);
                    break;
                case IN_PROGRESS:
                default:
                    break;
            }
        });
    }

    private void setContent() {
        toggleJoinButton(false);

        LiveData<GetInviteInfoJob> job = joinHouseViewModel.getInviteInfo();
        job.observe(this, getInviteInfoJob -> {
            switch (getInviteInfoJob.getJobStatus()) {
                case SUCCESS:
                    houseName.setText(getInviteInfoJob.getHouse().getName());
                    progressBar.setVisibility(View.GONE);
                    joinExplanation.setVisibility(View.VISIBLE);
                    houseName.setVisibility(View.VISIBLE);
                    joinConfirm.setVisibility(View.VISIBLE);
                    joinButton.setVisibility(View.VISIBLE);
                    toggleJoinButton(true);
                    break;
                case ERROR:
                    toggleJoinButton(false);
                    if (getInviteInfoJob.getJobErrorCode() == FirestoreJob.JobErrorCode.DOCUMENT_NOT_FOUND) {
                        progressBar.setVisibility(View.GONE);
                        joinExplanation.setText(getString(R.string.join_house_activity_invitation_or_house_dont_exist));
                        joinExplanation.setVisibility(View.VISIBLE);
                    } else if (getInviteInfoJob.getJobErrorCode() == FirestoreJob.JobErrorCode.INVITATION_EXPIRED) {
                        progressBar.setVisibility(View.GONE);
                        joinExplanation.setText(getString(R.string.join_house_activity_invitation_expired));
                        joinExplanation.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        joinExplanation.setText("An error occurred while fetching data, please try again.");
                        joinExplanation.setVisibility(View.VISIBLE);
                    }
                    break;
                case IN_PROGRESS:
                default:
                    break;
            }
        });
    }

    private void doJoinHouse(View view) {
        toggleJoinButton(false);

        LiveData<JoinHouseJob> job = joinHouseViewModel.joinHouse();
        job.observe(this, joinHouseJob -> {
            switch (joinHouseJob.getJobStatus()) {
                case SUCCESS:
                    // update user role
                    LiveData<FirestoreJob> updateUserRoleJob = UserRepository.getInstance().updateUserRole(
                            auth.getCurrentUser().getUid(), House.Roles.ROOMIE);
                    updateUserRoleJob.observe(this, firestoreJob -> {
                        switch (firestoreJob.getJobStatus()) {
                            case SUCCESS:
                                Toast.makeText(this, getString(R.string.join_house_activity_welcome_msg), Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(JoinHouseActivity.this, HouseActivity.class);
                                intent.putExtra("house", joinHouseJob.getHouse());
                                startActivity(intent);
                                finish();
                                break;
                            case ERROR:
                                Toast.makeText(this, "Error while updating user.", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                break;
                        }
                    });
                    break;
                case ERROR:
                    toggleJoinButton(true);
                    Toast.makeText(this, getString(R.string.join_house_activity_join_error), Toast.LENGTH_LONG).show();
                    break;
                case IN_PROGRESS:
                default:
                    break;
            }
        });
    }

    private void toggleJoinButton(boolean isEnabled) {
        joinButton.setEnabled(isEnabled);
    }
}