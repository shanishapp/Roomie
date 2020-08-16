package com.example.roomie.house.invite;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.roomie.FirestoreJob;
import com.example.roomie.House;
import com.example.roomie.util.FirestoreUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Instant;
import java.util.Date;

public class InviteRoomieViewModel extends ViewModel {

    private final static String TAG = "INVITE_ROOMIE_VIEW_MODEL";

    private FirebaseFirestore db;

    private FirebaseAuth auth;


    public InviteRoomieViewModel() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public LiveData<CreateInviteJob> getInviteLink(House house) {
        CreateInviteJob createInviteJob = new CreateInviteJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<CreateInviteJob> job = new MutableLiveData<>(createInviteJob);

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            createInviteJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
            createInviteJob.setJobErrorCode(FirestoreJob.JobErrorCode.USER_NOT_SIGNED_IN);
            job.setValue(createInviteJob);

            return job;
        }

        Invite invite = new Invite(
                house.getId(), Date.from(Instant.now().plusSeconds(Invite.VALIDITY_TIME)));

        db.collection(FirestoreUtil.INVITES_COLLECTION_NAME)
                .add(invite)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        invite.setId(task.getResult().getId());
                        createInviteJob.setInvite(invite);
                        createInviteJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);

                        job.setValue(createInviteJob);
                    } else {
                        createInviteJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        createInviteJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                        job.setValue(createInviteJob);

                        Log.d(TAG, "Error during invite creation.");
                    }
                });

        return job;
    }

}
