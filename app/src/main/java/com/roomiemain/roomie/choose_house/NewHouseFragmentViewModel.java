package com.roomiemain.roomie.choose_house;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.House;
import com.roomiemain.roomie.repositories.HouseRepository;
import com.roomiemain.roomie.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.roomiemain.roomie.util.FirestoreUtil.HOUSES_COLLECTION_NAME;

public class NewHouseFragmentViewModel extends ViewModel {

    private final static String TAG = "NEW_HOUSE_VIEW_MODEL";

    private FirebaseFirestore db;

    public NewHouseFragmentViewModel() {
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<CreateNewHouseJob> createNewHouse(String houseName, String houseAddress, String houseDesc) {
        CreateNewHouseJob newHouseJob = new CreateNewHouseJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<CreateNewHouseJob> job = new MutableLiveData<>(newHouseJob);

        // TODO check that user is logged in
        // TODO check that the user doesn't already have a house (also can be done in firestore rules)

        // create the new house
        Map<String, House.Roles> roomies = new HashMap<>();
        roomies.put(FirebaseAuth.getInstance().getUid(), House.Roles.OWNER);
        House newHouse = new House(houseName, houseAddress, houseDesc, roomies);

        // add new house to firestore
        db.collection(HOUSES_COLLECTION_NAME)
                .add(newHouse)
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // fetch the new house
                db.collection(HOUSES_COLLECTION_NAME)
                        .whereEqualTo(FieldPath.documentId(), task.getResult().getId())
                        .get()
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                if (task1.getResult().isEmpty()) {
                                    newHouseJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                    newHouseJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);

                                    job.setValue(newHouseJob);

                                    Log.d(TAG, "House ID not found.");
                                    return;
                                }

                                newHouseJob.setHouse(
                                        task1.getResult().getDocuments().get(0).toObject(House.class)
                                );
                                newHouseJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);

                                job.setValue(newHouseJob);
                            } else {
                                newHouseJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                newHouseJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);

                                job.setValue(newHouseJob);

                                Log.d(TAG, "Error while retrieving the new house: ", task1.getException());
                            }
                        });
            } else {
                newHouseJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                newHouseJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);

                job.setValue(newHouseJob);

                Log.d(TAG, "Error while creating a new house: ", task.getException());
            }
        });

        return job;
    }

    public LiveData<FirestoreJob> updateUserRole() {
        return UserRepository.getInstance().updateUserRole(
                FirebaseAuth.getInstance().getCurrentUser().getUid(), House.Roles.OWNER);
    }

}
