package com.roomiemain.roomie.repositories;

import android.content.res.Resources;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.R;
import com.roomiemain.roomie.house.chores.AllChoresJob;
import com.roomiemain.roomie.house.chores.chore.Chore;
import com.roomiemain.roomie.house.chores.chore.newChoreJob;
import com.roomiemain.roomie.util.FirestoreUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.roomiemain.roomie.util.FirestoreUtil.CHORES_COLLECTION_NAME;
import static com.roomiemain.roomie.util.FirestoreUtil.CHORE_DONE_FIELD_NAME;
import static com.roomiemain.roomie.util.FirestoreUtil.CREATION_DATE_FIELD_NAME;
import static com.roomiemain.roomie.util.FirestoreUtil.HOUSES_COLLECTION_NAME;
import static com.roomiemain.roomie.util.FirestoreUtil.SIZE_FIELD_NAME;

public class ChoreRepository {

    private static ChoreRepository instance = null;
    private FirebaseFirestore db;

    public static ChoreRepository getInstance() {
        if (instance == null) {
            instance = new ChoreRepository();
        }
        return instance;
    }

    private ChoreRepository(){
        db = FirebaseFirestore.getInstance();
    }

    // *** set chore data *** //
    public LiveData<newChoreJob> setAssignee(String choreId, String assignee, String houseId) {

        newChoreJob choresJob = new newChoreJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<newChoreJob> job = new MutableLiveData<>(choresJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(CHORES_COLLECTION_NAME)
                .document(choreId)
                .get()
                .addOnCompleteListener(task ->  {
                    if(task.isSuccessful()) {
                        Chore chore = task.getResult().toObject(Chore.class);
                        if(chore !=null) {
                            db.collection(HOUSES_COLLECTION_NAME)
                                    .document(houseId).collection(CHORES_COLLECTION_NAME)
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

    public LiveData<newChoreJob>  setTitle(String choreId, String title, String houseId) {
        newChoreJob choresJob = new newChoreJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<newChoreJob> job = new MutableLiveData<>(choresJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(CHORES_COLLECTION_NAME)
                .document(choreId)
                .get()
                .addOnCompleteListener(task ->  {
                    if(task.isSuccessful()) {
                        Chore chore = task.getResult().toObject(Chore.class);
                        if(chore !=null) {
                            db.collection(HOUSES_COLLECTION_NAME)
                                    .document(houseId).collection(CHORES_COLLECTION_NAME)
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

    public LiveData<newChoreJob>  setDone(Chore chore, boolean done, String houseId) {
        newChoreJob choresJob = new newChoreJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<newChoreJob> job = new MutableLiveData<>(choresJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(CHORES_COLLECTION_NAME)
                .document(chore.get_id())
                .get()
                .addOnCompleteListener(task ->  {
                    if(task.isSuccessful()) {
                        db.collection(HOUSES_COLLECTION_NAME)
                                .document(houseId).collection(CHORES_COLLECTION_NAME)
                                .document(chore.get_id()).update("_choreDone",done);
                        choresJob.setChore(task.getResult().toObject(Chore.class));
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

    public LiveData<newChoreJob> setDueDate(String choreId, String dueDate, String houseId) {
        newChoreJob choresJob = new newChoreJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<newChoreJob> job = new MutableLiveData<>(choresJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(CHORES_COLLECTION_NAME)
                .document(choreId)
                .get()
                .addOnCompleteListener(task ->  {
                    if(task.isSuccessful()) {
                        Chore chore = task.getResult().toObject(Chore.class);
                        if(chore !=null) {try {
                            String pattern = "dd/MM/yyyy HH:mm";
                            DateFormat df = new SimpleDateFormat(pattern);
                            db.collection(HOUSES_COLLECTION_NAME)
                                    .document(houseId).collection(CHORES_COLLECTION_NAME)
                                    .document(choreId).update("_dueDate",df.parse(dueDate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

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

    public LiveData<newChoreJob> setContent(String choreId, String content, String houseId) {
        newChoreJob choresJob = new newChoreJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<newChoreJob> job = new MutableLiveData<>(choresJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(CHORES_COLLECTION_NAME)
                .document(choreId)
                .get()
                .addOnCompleteListener(task ->  {
                    if(task.isSuccessful()) {
                        Chore chore = task.getResult().toObject(Chore.class);
                        if(chore !=null) {
                            db.collection(HOUSES_COLLECTION_NAME)
                                    .document(houseId).collection(CHORES_COLLECTION_NAME)
                                    .document(choreId).update("_description",content);

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

    // *** manipulate a

    public LiveData<newChoreJob> deleteChoreForever(Chore chore, String houseId) {

        newChoreJob choresJob = new newChoreJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<newChoreJob> job = new MutableLiveData<>(choresJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(CHORES_COLLECTION_NAME)
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


    public LiveData<AllChoresJob> getAllChores(String houseId) {
        AllChoresJob choresJob = new AllChoresJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<AllChoresJob> job = new MutableLiveData<>(choresJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(CHORES_COLLECTION_NAME)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        List<Chore> fetchedList = new ArrayList<>();
                        for(DocumentSnapshot documentSnapshot :  task.getResult()) {
                            Chore chore = documentSnapshot.toObject(Chore.class);
                            fetchedList.add(chore);
                        }
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

    public LiveData<AllChoresJob> getFilteredChores(String houseId, String field, String value, Resources resources) {
        AllChoresJob choresJob = new AllChoresJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<AllChoresJob> job = new MutableLiveData<>(choresJob);

        Query query;
        if(field.equals(CHORE_DONE_FIELD_NAME)){
            if(value.equals("true")){
                query = db.collection(HOUSES_COLLECTION_NAME)
                        .document(houseId).collection(CHORES_COLLECTION_NAME)
                        .whereEqualTo(field,true);
            } else {
                query = db.collection(HOUSES_COLLECTION_NAME)
                        .document(houseId).collection(CHORES_COLLECTION_NAME)
                        .whereEqualTo(field,false);
            }
        } else if (field.equals(CREATION_DATE_FIELD_NAME)) {
            if (value.equals("week")) {
                Date date = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                int i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
                c.add(Calendar.DATE, -i - 7);
                Date start = c.getTime();

                query = db.collection(HOUSES_COLLECTION_NAME)
                        .document(houseId).collection(CHORES_COLLECTION_NAME)
                        .whereGreaterThan(field, start);
            } else{
                Date date = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.MONTH, -1);
                Date start = c.getTime();

                query = db.collection(HOUSES_COLLECTION_NAME)
                        .document(houseId).collection(CHORES_COLLECTION_NAME)
                        .whereGreaterThan(field, start);
            }
        }
        else if(field.equals(SIZE_FIELD_NAME)){
            int size = FirestoreUtil.SMALL_SCORE;

            if(value.equals(resources.getString(R.string.medium))){
                size = FirestoreUtil.MEDIUM_SCORE;
            }else if(value.equals(resources.getString(R.string.big))){
                size = FirestoreUtil.LARGE_SCORE;
            }

            query = db.collection(HOUSES_COLLECTION_NAME)
                    .document(houseId).collection(CHORES_COLLECTION_NAME)
                    .whereEqualTo(field, size);
        } else {
            query = db.collection(HOUSES_COLLECTION_NAME)
                    .document(houseId).collection(CHORES_COLLECTION_NAME)
                    .whereEqualTo(field, value);
        }

        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                List<Chore> fetchedList = new ArrayList<>();
                for(DocumentSnapshot documentSnapshot :  task.getResult()) {
                    Chore chore = documentSnapshot.toObject(Chore.class);
                    fetchedList.add(chore);
                }
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

}
