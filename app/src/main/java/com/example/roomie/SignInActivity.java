package com.example.roomie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.roomie.choose_house.ChooseHouseActivity;
import com.example.roomie.house.HouseActivity;
import com.example.roomie.splash.GetUserHouseJob;
import com.example.roomie.splash.SplashScreenViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SIGN_IN_ACTIVITY";

    private static final int RC_SIGN_IN = 1;

    private FirebaseAuth auth;

    // TODO currently we use splash screen view model function to get the user house to avoid
    //      code duplication. Maybe create a unified view model for both of them (initViewModel)?
    //      Or maybe later we will add more functionality to splash / sign in so we will need to
    //      separate them anyway.
    private SplashScreenViewModel splashScreenViewModel;

    private Button signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        splashScreenViewModel = new ViewModelProvider(this).get(SplashScreenViewModel.class);

        // check if user is already logged in
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(this, ChooseHouseActivity.class);
            startActivity(intent);
            finish();
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
                // check if user has house or not
                LiveData<GetUserHouseJob> job = splashScreenViewModel.getUserHouse();
                job.observe(this, getUserHouseJob -> {
                    switch (job.getValue().getJobStatus()) {
                        case IN_PROGRESS:
                            // TODO add loading indication to user (overlay, loader etc).
                            //      The indication should be added upon clicking sign in button and
                            //      updated here.
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
                            Toast.makeText(this, "Error querying firestore.", Toast.LENGTH_LONG).show();
                            break;
                    }
                });
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                if (response == null) {
                    Log.d(TAG, "User cancelled sign in using back button.");
                } else {
                    Log.d(TAG, "Sign in failed with error code " + response.getError().getErrorCode());
                }
            }
        }
    }

    private void doSignIn(View view) {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.app_logo)
                        .setTheme(R.style.SignInTheme)
                        .build(),
                RC_SIGN_IN);
    }
}