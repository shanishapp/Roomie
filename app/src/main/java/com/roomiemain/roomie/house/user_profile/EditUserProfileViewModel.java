package com.roomiemain.roomie.house.user_profile;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.roomiemain.roomie.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

public class EditUserProfileViewModel extends ViewModel {

    private static final String TAG = "EDIT_USER_PROFILE_VIEW_MODEL";

    private FirebaseAuth auth;

    private MutableLiveData<String> username;

    private MutableLiveData<Uri> profilePicture;


    public EditUserProfileViewModel() {
        auth = FirebaseAuth.getInstance();
        username = new MutableLiveData<>();
        profilePicture = new MutableLiveData<>();
    }


    public LiveData<UpdateUserProfileJob> updateUserProfile(String username, Uri profilePicture) {
        return UserRepository.getInstance().updateUserProfile(username, profilePicture);
    }

    public LiveData<String> getUsername() {
        username.setValue(auth.getCurrentUser().getDisplayName());
        return username;
    }

    public LiveData<Uri> getProfilePicture() {
        profilePicture.setValue(auth.getCurrentUser().getPhotoUrl());
        return profilePicture;
    }
}
