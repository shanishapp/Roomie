package com.example.roomie.join_house;

import com.example.roomie.FirestoreJob;
import com.example.roomie.House;

public class JoinHouseJob extends FirestoreJob {

    private House house;


    public JoinHouseJob() {
        super();
    }

    public JoinHouseJob(JobStatus jobStatus) {
        super(jobStatus);
    }

    public JoinHouseJob(JobStatus jobStatus, JobErrorCode jobErrorCode) {
        super(jobStatus, jobErrorCode);
    }


    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }
}
