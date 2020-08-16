package com.example.roomie.join_house;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.roomie.FirestoreJob;
import com.example.roomie.House;
import com.example.roomie.house.invite.Invite;
import com.example.roomie.splash.GetUserHouseJob;
import com.example.roomie.util.FirestoreUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.roomie.util.FirestoreUtil.HOUSES_COLLECTION_NAME;

public class JoinHouseViewModel extends ViewModel {

    private final static String TAG = "JOIN_HOUSE_VIEW_MODEL";

    private FirebaseAuth auth;

    private FirebaseFirestore db;

    private String invitationId;

    private House house;


    public JoinHouseViewModel() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }


    public LiveData<GetUserHouseJob> getUserHouse() {
        // TODO this code is repeated a lot of times.
        //   maybe extract it to some House repository which can be shared?
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
                            getUserHouseJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                        }

                        job.setValue(getUserHouseJob);
                    } else {
                        getUserHouseJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        getUserHouseJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);

                        job.setValue(getUserHouseJob);

                        Log.d(TAG, "Error while fetching the user house: ", task.getException());
                    }
                });

        return job;
    }

    /**
     * Fetches the invitation and the house from the database.
     * @return LiveData of the GetInviteInfoJob object.
     */
    public LiveData<GetInviteInfoJob> getInviteInfo() {
        GetInviteInfoJob getInviteInfoJob = new GetInviteInfoJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<GetInviteInfoJob> job = new MutableLiveData<>(getInviteInfoJob);

        db.collection(FirestoreUtil.INVITES_COLLECTION_NAME)
                .whereEqualTo(FieldPath.documentId(), invitationId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        // check that the invitation exists
                        if (task.getResult().isEmpty()) {
                            getInviteInfoJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                            getInviteInfoJob.setJobErrorCode(FirestoreJob.JobErrorCode.DOCUMENT_NOT_FOUND);
                            job.setValue(getInviteInfoJob);

                            Log.d(TAG, String.format("No invitation with id %s was found.", invitationId));
                            return;
                        }

                        // check that the invitation is still valid
                        Invite invite = task.getResult().getDocuments().get(0).toObject(Invite.class);
                        if (new Date().after(invite.getExpirationDate())) {
                            getInviteInfoJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                            getInviteInfoJob.setJobErrorCode(FirestoreJob.JobErrorCode.INVITATION_EXPIRED);
                            job.setValue(getInviteInfoJob);

                            Log.d(TAG, "Error invitation is expired.");
                            return;
                        }

                        // get the house to join
                        db.collection(FirestoreUtil.HOUSES_COLLECTION_NAME)
                                .whereEqualTo(FieldPath.documentId(), invite.getHouseId())
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        // check that the house exists
                                        if (task1.getResult().isEmpty()) {
                                            getInviteInfoJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                            getInviteInfoJob.setJobErrorCode(FirestoreJob.JobErrorCode.DOCUMENT_NOT_FOUND);
                                            job.setValue(getInviteInfoJob);

                                            Log.d(TAG, String.format("No house with id %s was found.", invite.getHouseId()));
                                            return;
                                        }

                                        house = task1.getResult().getDocuments().get(0).toObject(House.class);
                                        getInviteInfoJob.setHouse(house);
                                        getInviteInfoJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                                        job.setValue(getInviteInfoJob);
                                    } else {
                                        getInviteInfoJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                        getInviteInfoJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                                        job.setValue(getInviteInfoJob);

                                        Log.d(TAG, "Error while fetching house.", task1.getException());
                                    }
                                });
                    } else {
                        getInviteInfoJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        getInviteInfoJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                        job.setValue(getInviteInfoJob);

                        Log.d(TAG, "Error while fetching invite.", task.getException());
                    }
                });

        return job;
    }

    /**
     * Adds the current user to the house, deleted the invitation and fetches the updated house.
     * @return LiveData of the JoinHouseJob object.
     */
    public LiveData<JoinHouseJob> joinHouse() {
        JoinHouseJob joinHouseJob = new JoinHouseJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<JoinHouseJob> job = new MutableLiveData<>(joinHouseJob);

        Map<String, Object> newRoomie = new HashMap<>();
        newRoomie.put("roomies." + auth.getUid(), House.Roles.ROOMIE);

        db.collection(FirestoreUtil.HOUSES_COLLECTION_NAME)
                .document(house.getId())
                .update(newRoomie)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // delete the invitation
                        // TODO check if deleted successfully?
                        db.collection(FirestoreUtil.INVITES_COLLECTION_NAME)
                            .document(invitationId)
                            .delete();

                        // get the updated house
                        db.collection(FirestoreUtil.HOUSES_COLLECTION_NAME)
                                .whereEqualTo(FieldPath.documentId(), house.getId())
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        if (task1.getResult().isEmpty()) {
                                            joinHouseJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                            joinHouseJob.setJobErrorCode(FirestoreJob.JobErrorCode.DOCUMENT_NOT_FOUND);
                                            job.setValue(joinHouseJob);

                                            Log.d(TAG, String.format("No house with id %s was found.", house.getId()));
                                            return;
                                        }

                                        house = task1.getResult().getDocuments().get(0).toObject(House.class);
                                        joinHouseJob.setHouse(house);
                                        joinHouseJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                                        job.setValue(joinHouseJob);
                                    } else {
                                        joinHouseJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                        joinHouseJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                                        job.setValue(joinHouseJob);

                                        Log.d(TAG, "Error while fetching the house.", task1.getException());
                                    }
                                });
                    } else {
                        joinHouseJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        joinHouseJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                        job.setValue(joinHouseJob);

                        Log.d(TAG, "Error while joining the house.", task.getException());
                    }
                });

        return job;
    }

    public void setInvitationId(String invitationId) {
        this.invitationId = invitationId;
    }

    public House getHouse() {
        return house;
    }

    public String getInvitationId() {
        return invitationId;
    }
}
