package com.roomiemain.roomie.house.groceries.grocery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.House;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.roomiemain.roomie.util.FirestoreUtil.GROCERIES_COLLECTION_NAME;
import static com.roomiemain.roomie.util.FirestoreUtil.HOUSES_COLLECTION_NAME;

public class NewGroceryFragmentViewModel extends ViewModel {

    private static final int VIEWTYPE_GROCERY = 1;
    FirebaseFirestore db;

    public NewGroceryFragmentViewModel(){
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<NewGroceryJob> createNewGrocery (House house, String name, int size,String creator){
            NewGroceryJob newGroceryJob = new NewGroceryJob(FirestoreJob.JobStatus.IN_PROGRESS);
            MutableLiveData<NewGroceryJob> job = new MutableLiveData<>(newGroceryJob);

            Grocery grocery = new Grocery(name,size,VIEWTYPE_GROCERY,creator);

            db.collection(HOUSES_COLLECTION_NAME)
                    .document(house.getId())
                    .collection(GROCERIES_COLLECTION_NAME)
                    .add(grocery)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            newGroceryJob.setGrocery(grocery);
                            setGroceryId(task.getResult().getId(),grocery,house);
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

    private void setGroceryId(String id, Grocery grocery, House house) {
        grocery.set_id(id);
        db.collection(HOUSES_COLLECTION_NAME).
                document(house.getId()).
                collection(GROCERIES_COLLECTION_NAME).document(id).set(grocery);
    }
}
