package com.example.roomie.join_house;

import com.example.roomie.FirestoreJob;
import com.example.roomie.House;
import com.example.roomie.house.invite.Invite;

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
