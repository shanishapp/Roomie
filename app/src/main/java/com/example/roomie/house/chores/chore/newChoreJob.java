package com.example.roomie.house.chores.chore;

import com.example.roomie.FirestoreJob;
import com.example.roomie.house.chores.chore.Chore;

public class newChoreJob extends  FirestoreJob {
    private Chore chore;

    public newChoreJob() {
        super();
    }

    public newChoreJob(FirestoreJob.JobStatus jobStatus) {
        super(jobStatus);
    }

    public newChoreJob(FirestoreJob.JobStatus jobStatus, FirestoreJob.JobErrorCode jobErrorCode) {
        super(jobStatus, jobErrorCode);
    }


    public Chore getChore() {
        return chore;
    }

    public void setChore(Chore chore) {
        this.chore = chore;
    }
}
