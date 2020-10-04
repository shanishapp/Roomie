package com.example.roomie.repositories;

import com.example.roomie.FirestoreJob;
import com.example.roomie.house.chores.chore.Chore;

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
