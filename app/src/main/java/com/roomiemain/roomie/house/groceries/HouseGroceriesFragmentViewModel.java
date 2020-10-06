package com.roomiemain.roomie.house.groceries;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.house.groceries.grocery.Grocery;
import com.roomiemain.roomie.house.groceries.grocery.NewGroceryJob;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.roomiemain.roomie.util.FirestoreUtil.CREATION_DATE_FIELD_NAME;
import static com.roomiemain.roomie.util.FirestoreUtil.GROCERIES_COLLECTION_NAME;
import static com.roomiemain.roomie.util.FirestoreUtil.HOUSES_COLLECTION_NAME;

public class HouseGroceriesFragmentViewModel extends ViewModel {

    private static final int VIEWTYPE_GROUP = 0;
    private static final int VIEWTYPE_GROCERY = 1;
    private static ArrayList<String> datesAvailable = new ArrayList<>();
    private FirebaseFirestore db;
    private MutableLiveData<List<Grocery>> groceries;

    public HouseGroceriesFragmentViewModel() {
        db = FirebaseFirestore.getInstance();
        groceries = new MutableLiveData<>(new ArrayList<>());
    }

    public LiveData<NewGroceryJob> deleteGroceryForever(Grocery grocery, String houseId) {

        NewGroceryJob newGroceryJob = new NewGroceryJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<NewGroceryJob> job = new MutableLiveData<>(newGroceryJob);

        groceries.getValue().remove(grocery);
        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(GROCERIES_COLLECTION_NAME)
                .document(grocery.get_id()).delete()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        newGroceryJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                        job.setValue(newGroceryJob);
                    } else {
                        newGroceryJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        newGroceryJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                        job.setValue(newGroceryJob);
                    }
                });
        return job;
    }

    public LiveData<AllGroceriesJob> getAllGroceries(String houseId){
        AllGroceriesJob allGroceriesJob = new AllGroceriesJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<AllGroceriesJob> job = new MutableLiveData<>(allGroceriesJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(GROCERIES_COLLECTION_NAME)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        List<Grocery> fetchedList = new ArrayList<>();
                        for(DocumentSnapshot snapshot: task.getResult()){
                            Grocery grocery = snapshot.toObject(Grocery.class);
                            fetchedList.add(grocery);
                        }
                        groceries.setValue(fetchedList);
                        allGroceriesJob.setGroceryList(fetchedList);
                        allGroceriesJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                        job.setValue(allGroceriesJob);
                    } else {
                        allGroceriesJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        allGroceriesJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                        job.setValue(allGroceriesJob);
                    }
                });
        return job;
    }

    public LiveData<AllGroceriesJob> getAllGroceriesFromLastWeek(String houseId){
        AllGroceriesJob allGroceriesJob = new AllGroceriesJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<AllGroceriesJob> job = new MutableLiveData<>(allGroceriesJob);
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
        c.add(Calendar.DATE, -i - 7);
        Date start = c.getTime();

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(GROCERIES_COLLECTION_NAME).whereGreaterThan(CREATION_DATE_FIELD_NAME,start)
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                List<Grocery> fetchedList = new ArrayList<>();
                for(DocumentSnapshot snapshot: task.getResult()){
                    Grocery grocery = snapshot.toObject(Grocery.class);
                    fetchedList.add(grocery);
                }
                groceries.setValue(fetchedList);
                allGroceriesJob.setGroceryList(fetchedList);
                allGroceriesJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                job.setValue(allGroceriesJob);
            } else {
                allGroceriesJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                allGroceriesJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                job.setValue(allGroceriesJob);
            }
        });
        return job;
    }

    public ArrayList<Grocery> sortGroceries(ArrayList<Grocery> groceries){
        groceries.sort((grocery1, grocery2) -> grocery1.get_creationDate().compareTo(grocery2.get_creationDate()));
        return groceries;
    }

    public static ArrayList<Grocery> addDates(ArrayList<Grocery> list) {
        ArrayList<Grocery> customList = new ArrayList<>();
        String pattern = "dd/MM/yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        String title = df.format(list.get(0).get_creationDate());
        Grocery firstPosition = new Grocery(title,1,VIEWTYPE_GROUP,"");
        datesAvailable.add(title);
        customList.add(firstPosition);
        for(int i=0; i<list.size()-1;i++){
            Grocery grocery = new Grocery();

            String date1 = df.format(list.get(i).get_creationDate());
            String date2 = df.format(list.get(i+1).get_creationDate());
            if (date1.equals(date2)) {
                list.get(i).set_viewType(VIEWTYPE_GROCERY);
                customList.add(list.get(i));
            } else {
                list.get(i).set_viewType(VIEWTYPE_GROCERY);
                customList.add(list.get(i));
                grocery.set_name(date2);
                grocery.set_viewType(VIEWTYPE_GROUP);
                datesAvailable.add(date2);
                customList.add(grocery);
            }
        }
        list.get(list.size()-1).set_viewType(VIEWTYPE_GROCERY);
        customList.add(list.get(list.size()-1));
        return customList;
    }




}
