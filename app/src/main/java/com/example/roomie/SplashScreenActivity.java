package com.example.roomie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                loadApp();
            }
        };

        Handler handler = new Handler(Looper.getMainLooper());
        long millisecondsInTheFuture = 1000;
        handler.postDelayed(runnable, millisecondsInTheFuture);


    }

    private void loadApp() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            // user is logged out
            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            // user is logged in
            Intent intent = new Intent(SplashScreenActivity.this, ChooseHouseActivity.class);
            startActivity(intent);
            finish();
        }
    }
}