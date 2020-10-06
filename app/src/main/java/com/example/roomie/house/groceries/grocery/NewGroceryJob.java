package com.example.roomie.house.groceries.grocery;

import com.example.roomie.FirestoreJob;
import com.example.roomie.house.groceries.grocery.Grocery;

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
