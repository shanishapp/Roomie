package com.example.roomie.repositories;

import com.example.roomie.FirestoreJob;
import com.example.roomie.User;

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
