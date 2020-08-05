package com.example.roomie.choose_house;

import com.example.roomie.FirestoreJob;
import com.example.roomie.House;

public class CreateNewHouseJob extends FirestoreJob {

    private House house;


    public CreateNewHouseJob() {
        super();
    }

    public CreateNewHouseJob(JobStatus jobStatus) {
        super(jobStatus);
    }

    public CreateNewHouseJob(JobStatus jobStatus, JobErrorCode jobErrorCode) {
        super(jobStatus, jobErrorCode);
    }


    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }
}
