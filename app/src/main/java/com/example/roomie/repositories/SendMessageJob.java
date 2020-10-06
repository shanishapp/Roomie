package com.example.roomie.repositories;

import com.example.roomie.FirestoreJob;
import com.example.roomie.house.chat.Message;

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
