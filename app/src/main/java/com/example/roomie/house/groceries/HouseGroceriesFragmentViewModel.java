package com.example.roomie.house.groceries;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.roomie.FirestoreJob;
import com.example.roomie.house.groceries.grocery.Grocery;
import com.example.roomie.house.groceries.grocery.NewGroceryJob;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import static com.example.roomie.util.FirestoreUtil.GROCERIES_COLLECTION_NAME;
import static com.example.roomie.util.FirestoreUtil.HOUSES_COLLECTION_NAME;

public class HouseGroceriesFragmentViewModel extends ViewModel implements GroceryAdapter.OnGroceryListener{

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

    @Override
    public void onGroceryClick(int pos) {

    }

    @Override
    public void onGroceryPicked(Grocery grocery) {

    }

    @Override
    public void onGroceryUnPicked(Grocery grocery) {

    }
}
