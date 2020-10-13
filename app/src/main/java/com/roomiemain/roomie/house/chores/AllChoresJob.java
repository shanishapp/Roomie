package com.roomiemain.roomie.house.chores;

import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.house.chores.chore.Chore;

import java.util.List;

/*

 */
public class AllChoresJob extends FirestoreJob {
    private List<Chore> choreList;

    public AllChoresJob() {
        super();
    }

    public AllChoresJob(FirestoreJob.JobStatus jobStatus) {
        super(jobStatus);
    }

    public AllChoresJob(FirestoreJob.JobStatus jobStatus, FirestoreJob.JobErrorCode jobErrorCode) {
        super(jobStatus, jobErrorCode);
    }

    public List<Chore> getChoreList() {
        return  choreList;
    }

    public void setChoreList(List<Chore> choreList) {
        this.choreList = choreList;
    }
}
