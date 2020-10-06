package com.roomiemain.roomie.repositories;

import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.User;

public class GetUserJob extends FirestoreJob {

    private User user;


    public GetUserJob() {
        super();
    }

    public GetUserJob(JobStatus jobStatus) {
        super(jobStatus);
    }

    public GetUserJob(JobStatus jobStatus, JobErrorCode jobErrorCode) {
        super(jobStatus, jobErrorCode);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
