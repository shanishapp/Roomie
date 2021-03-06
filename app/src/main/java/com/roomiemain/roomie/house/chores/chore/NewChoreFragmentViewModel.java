package com.roomiemain.roomie.house.chores.chore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.House;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import static com.roomiemain.roomie.util.FirestoreUtil.CHORES_COLLECTION_NAME;
import static com.roomiemain.roomie.util.FirestoreUtil.HOUSES_COLLECTION_NAME;

public class NewChoreFragmentViewModel extends ViewModel {

    private FirebaseFirestore db;

    public NewChoreFragmentViewModel() {
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<newChoreJob> createNewChore(House house,
                                                String title,
                                                String description, Date dueDate,
                                                String assignee,
                                                Date snoozeDate,
                                                int score) {
        newChoreJob newChoreJob = new newChoreJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<com.roomiemain.roomie.house.chores.chore.newChoreJob> job = new MutableLiveData<>(newChoreJob);

        //create new chore
        Chore chore = new Chore(title, description, assignee, dueDate,false, snoozeDate, score);

        //add new chore to firestore
        db.collection(HOUSES_COLLECTION_NAME)
                .document(house.getId())
                .collection(CHORES_COLLECTION_NAME)
                .add(chore)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.collection(HOUSES_COLLECTION_NAME)
                            .document(house.getId()).collection(CHORES_COLLECTION_NAME)
                            .whereEqualTo(FieldPath.documentId(),task.getResult().getId())
                            .get()
                            .addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()) {
                                    completeNewChoreJob(house, newChoreJob, job, chore, task, task1);
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

    private void completeNewChoreJob(House house, newChoreJob newChoreJob,
                                     MutableLiveData<newChoreJob> job,
                                     Chore chore, Task<DocumentReference> task,
                                     Task<QuerySnapshot> task1) {
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
    }

    private void setChoreId(String id, Chore chore, House house) {
        chore.set_id(id);
        db.collection(HOUSES_COLLECTION_NAME).
                document(house.getId()).
                collection(CHORES_COLLECTION_NAME).document(id).set(chore);
    }

}
