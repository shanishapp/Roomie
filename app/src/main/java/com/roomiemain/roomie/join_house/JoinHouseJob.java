package com.roomiemain.roomie.join_house;

import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.House;

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
