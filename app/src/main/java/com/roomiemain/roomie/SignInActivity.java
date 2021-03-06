package com.roomiemain.roomie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.roomiemain.roomie.choose_house.ChooseHouseActivity;
import com.roomiemain.roomie.house.HouseActivity;
import com.roomiemain.roomie.join_house.JoinHouseActivity;
import com.roomiemain.roomie.repositories.TokenRepository;
import com.roomiemain.roomie.repositories.UserRepository;
import com.roomiemain.roomie.splash.GetUserHouseJob;
import com.roomiemain.roomie.splash.SplashScreenActivity;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.List;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SIGN_IN_ACTIVITY";

    private static final int RC_SIGN_IN = 1;

    private FirebaseAuth auth;

    private String invitationId;

    private SignInViewModel signInViewModel;

    private Button signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInViewModel = new ViewModelProvider(this).get(SignInViewModel.class);

        // check if user is already logged in
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // TODO check if the user has house or not
            Intent intent = new Intent(this, ChooseHouseActivity.class);
            startActivity(intent);
            finish();
        }

        invitationId = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(SplashScreenActivity.INVITATION_ID_EXTRA)) {
                invitationId = extras.getString(SplashScreenActivity.INVITATION_ID_EXTRA);
            }
        }

        setUIElements();
        setListeners();
    }

    private void setUIElements() {
        signInBtn = findViewById(R.id.sign_in_btn);
    }

    private void setListeners() {
        signInBtn.setOnClickListener(this::doSignIn);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                LiveData<FirestoreJob> addUserJob = UserRepository.getInstance().addUser(auth.getCurrentUser());
                addUserJob.observe(this, firestoreJob -> {
                    switch (firestoreJob.getJobStatus()) {
                        case SUCCESS:
                            proceedSignIn();
                            break;
                        case ERROR:
                            // TODO retry on error?
                            Log.d(TAG, "Error while adding user.");
                            Toast.makeText(this, "Error adding user, please try again.", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            break;
                    }
                });
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button_bg_grey. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                toggleLoadingLayout(false);
                if (response == null) {
                    Log.d(TAG, "User cancelled sign in using back button_bg_grey.");
                } else {
                    Log.d(TAG, "Sign in failed with error code " + response.getError().getErrorCode());
                }
            }
        }
    }

    private void proceedSignIn() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(task -> {
                    // update user token
                    String token = task.getToken();
                    TokenRepository.getInstance().updateUserToken(token);
                })
                .addOnFailureListener(task -> {
                    Log.d(TAG, "Error getting user token.");
                });

        if (invitationId != null) {
            // we have an invitation id - redirect to join house
            Intent intent = new Intent(SignInActivity.this, JoinHouseActivity.class);
            intent.putExtra(SplashScreenActivity.INVITATION_ID_EXTRA, invitationId);
            startActivity(intent);
            finish();
            return;
        }

        // check if user has house or not
        LiveData<GetUserHouseJob> job = signInViewModel.getUserHouse();
        job.observe(this, getUserHouseJob -> {
            switch (job.getValue().getJobStatus()) {
                case IN_PROGRESS:
                    break;
                case SUCCESS:
                    if (getUserHouseJob.isUserHasHouse()) {
                        Intent intent = new Intent(SignInActivity.this, HouseActivity.class);
                        intent.putExtra("house", getUserHouseJob.getHouse());
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SignInActivity.this, ChooseHouseActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
                case ERROR:
                    // TODO retry the job? meanwhile just toast a tmp message
                    toggleLoadingLayout(false);
                    Toast.makeText(this, "Error querying firestore.", Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void doSignIn(View view) {
        toggleLoadingLayout(true);
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        // add custom sign in layout
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout.Builder(R.layout.firebase_auth_sign_in)
                .setEmailButtonId(R.id.firebase_auth_sign_in_create_account_button)
                .setGoogleButtonId(R.id.firebase_auth_sign_in_google_button)
                .setFacebookButtonId(R.id.firebase_auth_sign_in_facebook_button)
                .build();

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.app_logo)
                        .setTheme(R.style.SignInTheme)
                        .setAuthMethodPickerLayout(customLayout)
                        .build(),
                RC_SIGN_IN);
    }

    private void toggleLoadingLayout(boolean isLoading) {
        if (isLoading) {
            signInBtn.setEnabled(false);
        } else {
            signInBtn.setEnabled(true);
        }
    }
}