package com.roomiemain.roomie.house.groceries;

import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.house.groceries.grocery.Grocery;

import java.util.List;

public class AllGroceriesJob extends FirestoreJob {

    private List<Grocery> groceryList;

    public  AllGroceriesJob(){
        super();
    }

    public AllGroceriesJob(FirestoreJob.JobStatus jobStatus){
        super(jobStatus);
    }

    public AllGroceriesJob(FirestoreJob.JobStatus jobStatus, FirestoreJob.JobErrorCode jobErrorCode) {
        super(jobStatus, jobErrorCode);
    }

    public List<Grocery> getGroceryList() {
        return groceryList;
    }

    public void setGroceryList(List<Grocery> groceryList) {
        this.groceryList = groceryList;
    }
}
