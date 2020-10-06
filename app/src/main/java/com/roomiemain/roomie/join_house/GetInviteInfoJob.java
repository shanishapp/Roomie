package com.roomiemain.roomie.join_house;

import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.House;
import com.roomiemain.roomie.house.invite.Invite;

public class GetInviteInfoJob extends FirestoreJob {

    private Invite invite;

    private House house;


    public GetInviteInfoJob() {
        super();
    }

    public GetInviteInfoJob(JobStatus jobStatus) {
        super(jobStatus);
    }

    public GetInviteInfoJob(JobStatus jobStatus, JobErrorCode jobErrorCode) {
        super(jobStatus, jobErrorCode);
    }


    public Invite getInvite() {
        return invite;
    }

    public House getHouse() {
        return house;
    }

    public void setInvite(Invite invite) {
        this.invite = invite;
    }

    public void setHouse(House house) {
        this.house = house;
    }
}
