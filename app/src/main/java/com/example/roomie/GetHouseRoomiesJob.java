package com.example.roomie;

import java.util.List;

public class GetHouseRoomiesJob extends FirestoreJob {

    private List<User> roomiesList;


    public GetHouseRoomiesJob() {
        super();
    }

    public GetHouseRoomiesJob(JobStatus jobStatus) {
        super(jobStatus);
    }

    public GetHouseRoomiesJob(JobStatus jobStatus, JobErrorCode jobErrorCode) {
        super(jobStatus, jobErrorCode);
    }

    public void setRoomiesList(List<User> roomiesList) {
        this.roomiesList = roomiesList;
    }

    public List<User> getRoomiesList() {
        return roomiesList;
    }
}
