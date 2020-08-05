package com.example.roomie.splash;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.roomie.FirestoreJob;
import com.example.roomie.House;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.roomie.util.FirestoreUtil.HOUSES_COLLECTION_NAME;

public class SplashScreenViewModel extends ViewModel {

    private final static String TAG = "SPLASH_SCREEN_VIEW_MODEL";

    private FirebaseFirestore db;

    public SplashScreenViewModel() {
        db = FirebaseFirestore.getInstance();
    }

    LiveData<GetUserHouseJob> getkUserHouse() {
        GetUserHouseJob getUserHouseJob = new GetUserHouseJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<GetUserHouseJob> job = new MutableLiveData<>(getUserHouseJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .whereGreaterThan("roomies." + FirebaseAuth.getInstance().getUid(), "")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() == 1) {
                            getUserHouseJob.setUserHasHouse(true);
                            getUserHouseJob.setHouse(
                                    task.getResult().getDocuments().get(0).toObject(House.class)
                            );
                            getUserHouseJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                        } else if (task.getResult().size() == 0) {
                            getUserHouseJob.setUserHasHouse(false);
                            getUserHouseJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                        } else {
                            // found multiple houses - this should not happen
                            getUserHouseJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                            // TODO change to a specific error
                            getUserHouseJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                        }

                        job.setValue(getUserHouseJob);
                    } else {
                        getUserHouseJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        getUserHouseJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);

                        job.setValue(getUserHouseJob);

                        Log.d(TAG, "Error while creating a new house: ", task.getException());
                    }
                });

        return job;
    }

}
