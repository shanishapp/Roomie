package com.roomiemain.roomie.splash;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.roomiemain.roomie.repositories.HouseRepository;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenViewModel extends ViewModel {

    private final static String TAG = "SPLASH_SCREEN_VIEW_MODEL";

    public LiveData<GetUserHouseJob> getUserHouse() {
        return HouseRepository.getInstance().getUserHouse(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

}
