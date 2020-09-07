package com.example.roomie.repositories;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.roomie.FirestoreJob;
import com.example.roomie.House;
import com.example.roomie.User;
import com.example.roomie.house.user_profile.UpdateUserProfileJob;
import com.example.roomie.util.FirestoreUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * This class functions as the single source of truth of all user info.
 */
public class UserRepository {

    private static final String TAG = "USER_REPOSITORY";

    private static UserRepository instance = null;

    private FirebaseFirestore db;

    private StorageReference storageReference;


    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }

        return instance;
    }

    private UserRepository() {
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }


    public LiveData<FirestoreJob> addUser(FirebaseUser user) {
        FirestoreJob firestoreJob = new FirestoreJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<FirestoreJob> job = new MutableLiveData<>(firestoreJob);

        db.collection(FirestoreUtil.USERS_COLLECTION_NAME)
                .whereEqualTo("uid", user.getUid())
                .get()
                .addOnSuccessListener(task -> {
                    if (task.isEmpty()) {
                        // need to add the user to the users collection
                        String profilePicture = (user.getPhotoUrl() == null) ? "" : user.getPhotoUrl().toString();
                        User newUser = new User(user.getUid(), user.getDisplayName(), user.getEmail(), profilePicture);
                        db.collection(FirestoreUtil.USERS_COLLECTION_NAME)
                                .add(newUser)
                                .addOnSuccessListener(task1 -> {
                                    firestoreJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                                    job.setValue(firestoreJob);
                                })
                                .addOnFailureListener(task1 -> {
                                    firestoreJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                    firestoreJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                                    job.setValue(firestoreJob);
                                    Log.d(TAG, "Error adding the user in addUser.", task1.getCause());
                                });
                    } else {
                        // user document in users collection already present
                        firestoreJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                        job.setValue(firestoreJob);
                    }
                })
                .addOnFailureListener(task -> {
                    firestoreJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                    firestoreJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                    job.setValue(firestoreJob);
                    Log.d(TAG, "Error fetching the user in addUser.", task.getCause());
                });

        return job;
    }

    public LiveData<FirestoreJob> updateUserRole(String uid, House.Roles role) {
        FirestoreJob firestoreJob = new FirestoreJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<FirestoreJob> job = new MutableLiveData<>(firestoreJob);

        db.collection(FirestoreUtil.USERS_COLLECTION_NAME)
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener(task -> {
                    String docId = task.getDocuments().get(0).getId();
                    db.collection(FirestoreUtil.USERS_COLLECTION_NAME)
                            .document(docId)
                            .update("role", role)
                            .addOnSuccessListener(task1 -> {
                                firestoreJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                                job.setValue(firestoreJob);
                            })
                            .addOnFailureListener(task1 -> {
                                firestoreJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                firestoreJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                                job.setValue(firestoreJob);
                                Log.d(TAG, "Error updating user role in updateUserRole.", task1.getCause());
                            });
                })
                .addOnFailureListener(task -> {
                    firestoreJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                    firestoreJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                    job.setValue(firestoreJob);
                    Log.d(TAG, "Error fetching the user in updateUserRole.", task.getCause());
                });

        return job;
    }

    public LiveData<UpdateUserProfileJob> updateUserProfile(String username, Uri profilePicture) {
        UpdateUserProfileJob updateUserProfileJob = new UpdateUserProfileJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<UpdateUserProfileJob> job = new MutableLiveData<>(updateUserProfileJob);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (profilePicture == null || profilePicture.compareTo(auth.getCurrentUser().getPhotoUrl()) == 0) {
            // update only username
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build();

            auth.getCurrentUser().updateProfile(userProfileChangeRequest)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            // get user document in users collection
                            db.collection(FirestoreUtil.USERS_COLLECTION_NAME)
                                    .whereEqualTo("uid", auth.getCurrentUser().getUid())
                                    .get()
                                    .addOnSuccessListener(task1 -> {

                                        // update user document in users collection
                                        db.collection(FirestoreUtil.USERS_COLLECTION_NAME)
                                                .document(task1.getDocuments().get(0).getId())
                                                .update("username", username)
                                                .addOnSuccessListener(task2 -> {
                                                    updateUserProfileJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                                                    job.setValue(updateUserProfileJob);
                                                    Log.d(TAG, "Updated only username.");
                                                })
                                                .addOnFailureListener(task2 -> {
                                                    updateUserProfileJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                                    updateUserProfileJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                                                    job.setValue(updateUserProfileJob);
                                                    Log.d(TAG, "Error while updating username in users collection.", task2.getCause());
                                                });
                                    })
                                    .addOnFailureListener(task1 -> {
                                        updateUserProfileJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                        updateUserProfileJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                                        job.setValue(updateUserProfileJob);
                                        Log.d(TAG, "Error while fetching user.", task1.getCause());
                                    });
                        } else {
                            updateUserProfileJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                            updateUserProfileJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                            job.setValue(updateUserProfileJob);
                            Log.d(TAG, "Error while updating only username.", task.getException());
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

                                                    // get user document in users collection
                                                    db.collection(FirestoreUtil.USERS_COLLECTION_NAME)
                                                            .whereEqualTo("uid", auth.getCurrentUser().getUid())
                                                            .get()
                                                            .addOnSuccessListener(task3 -> {

                                                                // update user document in users collection
                                                                db.collection(FirestoreUtil.USERS_COLLECTION_NAME)
                                                                        .document(task3.getDocuments().get(0).getId())
                                                                        .update("username", username,
                                                                                "profilePicture", profilePictureUri.toString())
                                                                        .addOnSuccessListener(task4 -> {
                                                                            updateUserProfileJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                                                                            job.setValue(updateUserProfileJob);
                                                                            Log.d(TAG, "Successfully updated profile + picture.");
                                                                        })
                                                                        .addOnFailureListener(task4 -> {
                                                                            updateUserProfileJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                                                            updateUserProfileJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                                                                            job.setValue(updateUserProfileJob);
                                                                            Log.d(TAG, "Error updating user profile in user collection.", task4.getCause());
                                                                        });
                                                            })
                                                            .addOnFailureListener(task3 -> {
                                                                updateUserProfileJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                                                updateUserProfileJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                                                                job.setValue(updateUserProfileJob);
                                                                Log.d(TAG, "Error fetching user.", task3.getCause());
                                                            });
                                                } else {
                                                    updateUserProfileJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                                    updateUserProfileJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                                                    job.setValue(updateUserProfileJob);
                                                    Log.d(TAG, "Error updating profile.", task2.getException());
                                                }
                                            });
                                } else {
                                    updateUserProfileJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                    updateUserProfileJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                                    job.setValue(updateUserProfileJob);
                                    Log.d(TAG, "Error getting download URL.", task1.getException());
                                }
                            });
                        } else {
                            updateUserProfileJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                            updateUserProfileJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                            job.setValue(updateUserProfileJob);
                            Log.d(TAG, "Error uploading file.", task.getException());
                        }
                    });
        }

        return job;
    }

}
