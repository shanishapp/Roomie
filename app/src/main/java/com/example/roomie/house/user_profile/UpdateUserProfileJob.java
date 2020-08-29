package com.example.roomie.house.user_profile;

import android.net.Uri;

import com.example.roomie.FirestoreJob;

public class UpdateUserProfileJob extends FirestoreJob {

    private Uri profilePictureUri;


    public UpdateUserProfileJob() {
        super();
    }

    public UpdateUserProfileJob(JobStatus jobStatus) {
        super(jobStatus);
    }

    public UpdateUserProfileJob(JobStatus jobStatus, JobErrorCode errorCode) {
        super(jobStatus, errorCode);
    }

    public void setProfilePictureUri(Uri profilePictureUri) {
        this.profilePictureUri = profilePictureUri;
    }

    public Uri getProfilePictureUri() {
        return profilePictureUri;
    }
}
