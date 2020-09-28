package com.example.roomie.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.roomie.FirestoreJob;
import com.example.roomie.util.FirestoreUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class TokenRepository {

    private static final String TAG = "TOKEN_REPOSITORY";

    private static TokenRepository instance = null;

    private FirebaseFirestore db;


    public static TokenRepository getInstance() {
        if (instance == null) {
            instance = new TokenRepository();
        }

        return instance;
    }

    private TokenRepository() {
        db = FirebaseFirestore.getInstance();
    }


    public LiveData<FirestoreJob> updateUserToken(String newToken) {
        FirestoreJob firestoreJob = new FirestoreJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<FirestoreJob> job = new MutableLiveData<>(firestoreJob);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            firestoreJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
            firestoreJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
            job.setValue(firestoreJob);
            return job;
        }

        String uid = auth.getCurrentUser().getUid();
        db.collection(FirestoreUtil.TOKENS_COLLECTION_NAME)
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener(task -> {
                    if (task.isEmpty()) {
                        // no user tokens, add a new one
                        HashMap<String, String> tokenMap = new HashMap<>();
                        tokenMap.put("uid", uid);
                        tokenMap.put("token", newToken);
                        db.collection(FirestoreUtil.TOKENS_COLLECTION_NAME)
                                .add(tokenMap)
                                .addOnSuccessListener(task1 -> {
                                    firestoreJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                                    job.setValue(firestoreJob);
                                    Log.d(TAG, "SUCCESS! created new user token.");
                                })
                                .addOnFailureListener(task1 -> {
                                    firestoreJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                    firestoreJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                                    job.setValue(firestoreJob);

                                    Log.d(TAG, "Error creating new user token", task1.getCause());
                                });
                    } else if (task.size() == 1) {
                        // found a single user token, update it
                        String docId = task.getDocuments().get(0).getId();
                        if (newToken.equals(task.getDocuments().get(0).get("token"))) {
                            // tokens are identical, no need to update
                            firestoreJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                            job.setValue(firestoreJob);
                            Log.d(TAG, "SUCCESS! tokens are equal.");
                            return;
                        }

                        // update to new token
                        db.collection(FirestoreUtil.TOKENS_COLLECTION_NAME)
                                .document(docId)
                                .update("token", newToken)
                                .addOnSuccessListener(task1 -> {
                                    firestoreJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                                    job.setValue(firestoreJob);
                                    Log.d(TAG, "SUCCESS! Updated existing user token.");
                                })
                                .addOnFailureListener(task1 -> {
                                    firestoreJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                    firestoreJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                                    job.setValue(firestoreJob);

                                    Log.d(TAG, "Error updating existing user token.", task1.getCause());
                                });
                    } else {
                        firestoreJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        firestoreJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                        job.setValue(firestoreJob);

                        Log.d(TAG, String.format("Error too many user tokens (%d)", task.size()));
                    }
                })
                .addOnFailureListener(task -> {
                    firestoreJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                    firestoreJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                    job.setValue(firestoreJob);

                    Log.d(TAG, "Error getting user token", task.getCause());
                });

        return job;
    }

}
