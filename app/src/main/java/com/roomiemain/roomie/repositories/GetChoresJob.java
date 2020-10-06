package com.roomiemain.roomie.repositories;

import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.house.chores.chore.Chore;

import java.util.List;

public class GetChoresJob extends FirestoreJob {

    private List<Chore> choreList;


    public GetChoresJob() {
        super();
    }

    public GetChoresJob(JobStatus jobStatus) {
        super(jobStatus);
    }

    public GetChoresJob(JobStatus jobStatus, JobErrorCode jobErrorCode) {
        super(jobStatus, jobErrorCode);
    }


    public void setChoreList(List<Chore> choreList) {
        this.choreList = choreList;
    }

    public List<Chore> getChoreList() {
        return choreList;
    }
}
