package com.roomiemain.roomie.splash;

import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.House;

public class GetUserHouseJob extends FirestoreJob {

    private boolean userHasHouse;

    private House house;


    public GetUserHouseJob() {
        super();
    }

    public GetUserHouseJob(JobStatus jobStatus) {
        super(jobStatus);
    }

    public GetUserHouseJob(JobStatus jobStatus, JobErrorCode jobErrorCode) {
        super(jobStatus, jobErrorCode);
    }


    public boolean isUserHasHouse() {
        return userHasHouse;
    }

    public House getHouse() {
        return house;
    }

    public void setUserHasHouse(boolean userHasHouse) {
        this.userHasHouse = userHasHouse;
    }

    public void setHouse(House house) {
        this.house = house;
    }
}
