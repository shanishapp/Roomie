package com.roomiemain.roomie.splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.roomiemain.roomie.join_house.JoinHouseActivity;
import com.roomiemain.roomie.SignInActivity;
import com.roomiemain.roomie.choose_house.ChooseHouseActivity;
import com.roomiemain.roomie.house.HouseActivity;
import com.roomiemain.roomie.house.invite.Invite;
import com.roomiemain.roomie.repositories.TokenRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;

public class SplashScreenActivity extends AppCompatActivity {

    private final static String TAG = "SPLASH_SCREEN_ACTIVITY";

    public final static String INVITATION_ID_EXTRA = "invitation_id";

    private FirebaseAuth auth;

    private SplashScreenViewModel splashScreenViewModel;

    private String invitationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        invitationId = null;
        parseInvitationLink();

        auth = FirebaseAuth.getInstance();
        splashScreenViewModel = new ViewModelProvider(this).get(SplashScreenViewModel.class);
        loadApp();
    }

    private void parseInvitationLink() {
        Uri data = getIntent().getData();

        if (data != null && data.getScheme() != null && data.getHost() != null) {
            if (data.getScheme().equals(Invite.LINK_SCHEME) && data.getHost().equals(Invite.LINK_HOST)) {
                List<String> pathSegments = data.getPathSegments();
                if (pathSegments.size() == 1 && pathSegments.get(0).equals(Invite.LINK_PATH_PREFIX)) {
                    invitationId = data.getQueryParameter(Invite.LINK_ID_QUERY_PARAM);
                }
            }
        }
    }

    private void loadApp() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            // user is logged out
            Intent intent = new Intent(SplashScreenActivity.this, SignInActivity.class);
            if (invitationId != null) {
                intent.putExtra(INVITATION_ID_EXTRA, invitationId);
            }
            startActivity(intent);
            finish();
        } else {
            // user is logged in
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
                Intent intent = new Intent(SplashScreenActivity.this, JoinHouseActivity.class);
                intent.putExtra(INVITATION_ID_EXTRA, invitationId);
                startActivity(intent);
                finish();
                return;
            }

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