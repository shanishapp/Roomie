package com.roomiemain.roomie.house.groceries.grocery;

import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.house.groceries.grocery.Grocery;

public class NewGroceryJob extends FirestoreJob {

    private Grocery grocery;

    public  NewGroceryJob(){
        super();
    }

    public NewGroceryJob(FirestoreJob.JobStatus jobStatus){
        super(jobStatus);
    }

    public NewGroceryJob(FirestoreJob.JobStatus jobStatus, FirestoreJob.JobErrorCode jobErrorCode) {
        super(jobStatus, jobErrorCode);
    }

    public Grocery getGrocery() {
        return grocery;
    }

    public void setGrocery(Grocery grocery) {
        this.grocery = grocery;
    }
}
