package com.example.roomie.house.chores;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.roomie.FirestoreJob;
import com.example.roomie.house.chores.chore.Chore;
import com.example.roomie.house.chores.chore.newChoreJob;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.roomie.util.FirestoreUtil.CHORESS_COLLECTION_NAME;
import static com.example.roomie.util.FirestoreUtil.HOUSES_COLLECTION_NAME;

public class HouseChoresFragmentViewModel extends ViewModel implements ChoreAdapter.OnChoreListener {

    private FirebaseFirestore db;
    private MutableLiveData<List<Chore>> chores;
    public ChoreAdapter adapter = null;

    public HouseChoresFragmentViewModel() {
        db = FirebaseFirestore.getInstance();
        chores = new MutableLiveData<>(new ArrayList<>());
    }

    public LiveData<newChoreJob> setAssignee(String choreId, String assignee, String houseId) {

        newChoreJob choresJob = new newChoreJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<newChoreJob> job = new MutableLiveData<>(choresJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(CHORESS_COLLECTION_NAME)
                .document(choreId)
                .get()
                .addOnCompleteListener(task ->  {
                    if(task.isSuccessful()) {
                        Chore chore = task.getResult().toObject(Chore.class);
                        if(chore !=null) {
                            List<Chore> choreList = chores.getValue();
                            choreList.remove(chore);
                            chore.set_assignee(assignee);
                            choreList.add(chore);
                            chores.setValue(choreList);
                            db.collection(HOUSES_COLLECTION_NAME)
                                    .document(houseId).collection(CHORESS_COLLECTION_NAME)
                                    .document(choreId).update("_assignee",assignee);

                            choresJob.setChore(task.getResult().toObject(Chore.class));
                            choresJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                            job.setValue(choresJob);
                        }
                    } else {
                        choresJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        choresJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                        job.setValue(choresJob);
                    }
                });

        return job;
    }

    public LiveData<newChoreJob> deleteChoreForever(Chore chore, String houseId) {

        newChoreJob choresJob = new newChoreJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<newChoreJob> job = new MutableLiveData<>(choresJob);

        chores.getValue().remove(chore); // TODO does it work ?
        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(CHORESS_COLLECTION_NAME)
                .document(chore.get_id()).delete()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        choresJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                        job.setValue(choresJob);
                    } else {
                        choresJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        choresJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                        job.setValue(choresJob);
                    }
                });
        return job;
    }

    public LiveData<allChoresJob> getAllChores(String houseId) {
        allChoresJob choresJob = new allChoresJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<allChoresJob> job = new MutableLiveData<>(choresJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(CHORESS_COLLECTION_NAME)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        List<Chore> fetchedList = new ArrayList<>();
                        for(DocumentSnapshot documentSnapshot :  task.getResult()) {
                            Chore chore = documentSnapshot.toObject(Chore.class);
                            fetchedList.add(chore);
                        }
                        chores.setValue(fetchedList);
                        choresJob.setChoreList(fetchedList);
                        choresJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                        job.setValue(choresJob);
                    } else {
                        choresJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        choresJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                        job.setValue(choresJob);
                    }
                });
        return job;
    }

    @Override
    public void onChoreClick(int pos) {
        //TODO implement
    }

    @Override
    public void onDeleteClick(int pos) {
        //TODO implement
    }

    @Override
    public void onEditClick(int pos) {
        //TODO implement
    }

    public LiveData<newChoreJob>  setTitle(String choreId, String title, String houseId) {
        newChoreJob choresJob = new newChoreJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<newChoreJob> job = new MutableLiveData<>(choresJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(CHORESS_COLLECTION_NAME)
                .document(choreId)
                .get()
                .addOnCompleteListener(task ->  {
                    if(task.isSuccessful()) {
                        Chore chore = task.getResult().toObject(Chore.class);
                        if(chore !=null) {
                            List<Chore> choreList = chores.getValue();
                            choreList.remove(chore);
                            chore.set_title(title);
                            choreList.add(chore);
                            chores.setValue(choreList);
                            db.collection(HOUSES_COLLECTION_NAME)
                                    .document(houseId).collection(CHORESS_COLLECTION_NAME)
                                    .document(choreId).update("_title",title);

                            choresJob.setChore(task.getResult().toObject(Chore.class));
                            choresJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                            job.setValue(choresJob);
                        }
                    } else {
                        choresJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        choresJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                        job.setValue(choresJob);
                    }
                });

        return job;
    }

    public LiveData<newChoreJob> setDueDate(String choreId, String dueDate, String houseId) {
        newChoreJob choresJob = new newChoreJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<newChoreJob> job = new MutableLiveData<>(choresJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(CHORESS_COLLECTION_NAME)
                .document(choreId)
                .get()
                .addOnCompleteListener(task ->  {
                    if(task.isSuccessful()) {
                        Chore chore = task.getResult().toObject(Chore.class);
                        if(chore !=null) {
                            List<Chore> choreList = chores.getValue();
                            choreList.remove(chore);
                            String pattern = "dd/MM/yyyy HH:mm";
                            DateFormat df = new SimpleDateFormat(pattern);
                            try {
                                chore.set_dueDate(df.parse(dueDate));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            choreList.add(chore);
                            chores.setValue(choreList);
                            db.collection(HOUSES_COLLECTION_NAME)
                                    .document(houseId).collection(CHORESS_COLLECTION_NAME)
                                    .document(choreId).update("_dueDate",chore.get_dueDate());

                            choresJob.setChore(task.getResult().toObject(Chore.class));
                            choresJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                            job.setValue(choresJob);
                        }
                    } else {
                        choresJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        choresJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                        job.setValue(choresJob);
                    }

    });
        return job;
    }
}
