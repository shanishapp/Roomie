package com.example.roomie.house.user_profile;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.roomie.FirestoreJob;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditUserProfileViewModel extends ViewModel {

    private static final String TAG = "EDIT_USER_PROFILE_VIEW_MODEL";

    private FirebaseAuth auth;

    private StorageReference storageReference;

    private MutableLiveData<String> username;

    private MutableLiveData<Uri> profilePicture;


    public EditUserProfileViewModel() {
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        username = new MutableLiveData<>();
        profilePicture = new MutableLiveData<>();
    }


    public LiveData<UpdateUserProfileJob> updateUserProfile(String username, Uri profilePicture) {
        UpdateUserProfileJob updateUserProfileJob = new UpdateUserProfileJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<UpdateUserProfileJob> job = new MutableLiveData<>(updateUserProfileJob);

        if (profilePicture == null || profilePicture.compareTo(auth.getCurrentUser().getPhotoUrl()) == 0) {
            // update only username
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build();

            auth.getCurrentUser().updateProfile(userProfileChangeRequest)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            updateUserProfileJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                            job.postValue(updateUserProfileJob);
                            Log.d(TAG, "Updated only username.");
                        } else {
                            updateUserProfileJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                            updateUserProfileJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                            job.setValue(updateUserProfileJob);
                            Log.d(TAG, "Error while updating only username.");
                        }
                    });
        } else {
            // update both username and profile picture
            // upload profile picture
            StorageReference profilePictureRef = storageReference.child("users/" + auth.getUid() + "/profilePicture.jpg");
            profilePictureRef.putFile(profilePicture)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            // get download URL
                            task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    // Update profile
                                    Uri profilePictureUri = task1.getResult();
                                    final UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(username)
                                            .setPhotoUri(profilePictureUri)
                                            .build();

                                    auth.getCurrentUser().updateProfile(userProfileChangeRequest)
                                            .addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    updateUserProfileJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                                                    job.postValue(updateUserProfileJob);
                                                    Log.d(TAG, "Successfully updated profile + picture.");
                                                } else {
                                                    updateUserProfileJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                                    updateUserProfileJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                                                    job.setValue(updateUserProfileJob);
                                                    Log.d(TAG, "Error updating profile.");
                                                }
                                            });
                                } else {
                                    updateUserProfileJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                    updateUserProfileJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                                    job.setValue(updateUserProfileJob);
                                    Log.d(TAG, "Error getting download URL.");
                                }
                            });
                        } else {
                            updateUserProfileJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                            updateUserProfileJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                            job.setValue(updateUserProfileJob);
                            Log.d(TAG, "Error uploading file.");
                        }
                    });
        }

        return job;
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
