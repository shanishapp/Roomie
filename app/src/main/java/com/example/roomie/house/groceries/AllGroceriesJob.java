package com.example.roomie.house.groceries;

import com.example.roomie.FirestoreJob;
import com.example.roomie.house.groceries.grocery.Grocery;

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
