package com.example.roomie.splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.roomie.SignInActivity;
import com.example.roomie.choose_house.ChooseHouseActivity;
import com.example.roomie.house.HouseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private SplashScreenViewModel splashScreenViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        splashScreenViewModel = new ViewModelProvider(this).get(SplashScreenViewModel.class);
        loadApp();
    }

    private void loadApp() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            // user is logged out
            Intent intent = new Intent(SplashScreenActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        } else {
            // user is logged in
            LiveData<GetUserHouseJob> job = splashScreenViewModel.getUserHouse();
            job.observe(this, getUserHouseJob -> {
                switch (job.getValue().getJobStatus()) {
                    case IN_PROGRESS:
                        break;
                    case SUCCESS:
                        if (getUserHouseJob.isUserHasHouse()) {
                            Intent intent = new Intent(SplashScreenActivity.this, HouseActivity.class);
                            intent.putExtra("house", getUserHouseJob.getHouse());
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(SplashScreenActivity.this, ChooseHouseActivity.class);
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
        }
    }
}