package com.example.roomie.house.user_profile;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfileViewModel extends ViewModel {

    private FirebaseAuth auth;

    private FirebaseUser user;

    private MutableLiveData<String> userName;

    private MutableLiveData<String> userEmail;

    private MutableLiveData<Uri> userProfilePicture;

    private MutableLiveData<Integer> userBrooms;

    private MutableLiveData<Integer> userChores;

    private MutableLiveData<Float> userExpenses;


    public UserProfileViewModel() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        userName = new MutableLiveData<>();
        userEmail = new MutableLiveData<>();
        userProfilePicture = new MutableLiveData<>();
        userBrooms = new MutableLiveData<>();
        userChores = new MutableLiveData<>();
        userExpenses = new MutableLiveData<>();
    }


    public LiveData<String> getUserName() {
        userName.setValue(user.getDisplayName());
        return userName;
    }

    public LiveData<String> getUserEmail() {
        userEmail.setValue(user.getEmail());
        return userEmail;
    }

    public LiveData<Uri> getUserProfilePicture() {
        userProfilePicture.setValue(user.getPhotoUrl());
        return userProfilePicture;
    }

    public LiveData<Integer> getUserBrooms() {
        userBrooms.setValue(0);
        return userBrooms;
    }

    public LiveData<Integer> getUserChores() {
        userChores.setValue(0);
        return userChores;
    }

    public LiveData<Float> getUserExpenses() {
        userExpenses.setValue(0.0f);
        return userExpenses;
    }
}
