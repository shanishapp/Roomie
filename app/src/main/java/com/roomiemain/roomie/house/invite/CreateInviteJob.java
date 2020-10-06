package com.roomiemain.roomie.house.invite;

import com.roomiemain.roomie.FirestoreJob;

public class CreateInviteJob extends FirestoreJob {

    private Invite invite;


    public CreateInviteJob() {
        super();
    }

    public CreateInviteJob(JobStatus jobStatus) {
        super(jobStatus);
    }

    public CreateInviteJob(JobStatus jobStatus, JobErrorCode errorCode) {
        super(jobStatus, errorCode);
    }


    public Invite getInvite() {
        return invite;
    }

    public void setInvite(Invite invite) {
        this.invite = invite;
    }
}
