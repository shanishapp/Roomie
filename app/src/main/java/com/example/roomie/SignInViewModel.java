package com.example.roomie;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.roomie.repositories.HouseRepository;
import com.example.roomie.splash.GetUserHouseJob;
import com.google.firebase.auth.FirebaseAuth;

public class SignInViewModel extends ViewModel {

    public LiveData<GetUserHouseJob> getUserHouse() {
        return HouseRepository.getInstance().getUserHouse(FirebaseAuth.getInstance().getUid());
    }

}
