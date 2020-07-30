package com.example.roomie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.BidiFormatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChooseHouseActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private TextView welcomeMessage;

    private Button signOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_house);

        auth = FirebaseAuth.getInstance();

        setUIElements();
        setListeners();

        setContent();
    }

    private void setUIElements() {
        welcomeMessage = findViewById(R.id.welcome_msg);
        signOutBtn = findViewById(R.id.sign_out_btn);
    }

    private void setListeners() {
        signOutBtn.setOnClickListener(this::doSignOut);
    }

    private void doSignOut(View view) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
    }

    private void setContent() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            // user is logged out
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // user is logged in
        BidiFormatter myBidiFormatter = BidiFormatter.getInstance();
        String displayName = user.getDisplayName();
        welcomeMessage.setText(
                String.format(getString(R.string.choose_house_welcome_msg),
                        myBidiFormatter.unicodeWrap(displayName))
        );
    }

}