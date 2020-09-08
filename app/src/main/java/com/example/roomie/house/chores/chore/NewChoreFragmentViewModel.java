package com.example.roomie.house.chores.chore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.roomie.FirestoreJob;
import com.example.roomie.House;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import static com.example.roomie.util.FirestoreUtil.CHORESS_COLLECTION_NAME;
import static com.example.roomie.util.FirestoreUtil.HOUSES_COLLECTION_NAME;

public class NewChoreFragmentViewModel extends ViewModel {

    private FirebaseFirestore db;

    public NewChoreFragmentViewModel() {
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<newChoreJob> createNewChore(House house,
                                                String title,
                                                Date dueDate,
                                                String assignee) {
        newChoreJob newChoreJob = new newChoreJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<com.example.roomie.house.chores.chore.newChoreJob> job = new MutableLiveData<>(newChoreJob);

        //create new chore
        Chore chore = new Chore(title, assignee, dueDate);

        //add new chore to firestore
        db.collection(HOUSES_COLLECTION_NAME).
                document(house.getId()).
                collection(CHORESS_COLLECTION_NAME).add(chore).addOnCompleteListener( task -> {
                    if (task.isSuccessful()) {
                        db.collection(HOUSES_COLLECTION_NAME)
                            .document(house.getId()).collection(CHORESS_COLLECTION_NAME)
                            .whereEqualTo(FieldPath.documentId(),task.getResult().getId())
                            .get()
                            .addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()) {
                                    if (task1.getResult().isEmpty()) {
                                        newChoreJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                        newChoreJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);

                                        job.setValue(newChoreJob);
                                        return;
                                    }

                                    newChoreJob.setChore(
                                            task1.getResult().getDocuments().get(0).toObject(Chore.class)

                                    );
                                    setChoreId(task.getResult().getId(), chore,house);
                                    newChoreJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                                    job.setValue(newChoreJob);
                                } else {
                                    newChoreJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                    newChoreJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);

                                    job.setValue(newChoreJob);
                                }
                            });
                    } else {
                        newChoreJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        newChoreJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);

                        job.setValue(newChoreJob);
                    }
        });
        return job;

    }

    private void setChoreId(String id, Chore chore, House house) {
        chore.set_id(id);
        db.collection(HOUSES_COLLECTION_NAME).
                document(house.getId()).
                collection(CHORESS_COLLECTION_NAME).document(id).set(chore);
    }

}
