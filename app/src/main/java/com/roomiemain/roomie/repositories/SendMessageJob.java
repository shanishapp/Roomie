package com.roomiemain.roomie.repositories;

import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.house.chat.Message;

public class SendMessageJob extends FirestoreJob {

    private Message message;


    public SendMessageJob() {
        super();
    }

    public SendMessageJob(JobStatus jobStatus) {
        super(jobStatus);
    }

    public SendMessageJob(JobStatus jobStatus, JobErrorCode jobErrorCode) {
        super(jobStatus, jobErrorCode);
    }


    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
