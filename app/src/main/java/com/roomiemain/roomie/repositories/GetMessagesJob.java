package com.roomiemain.roomie.repositories;

import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.house.chat.Message;

import java.util.List;

public class GetMessagesJob extends FirestoreJob {

    private List<Message> messageList;


    public GetMessagesJob() {
        super();
    }

    public GetMessagesJob(JobStatus jobStatus) {
        super(jobStatus);
    }

    public GetMessagesJob(JobStatus jobStatus, JobErrorCode jobErrorCode) {
        super(jobStatus, jobErrorCode);
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public List<Message> getMessageList() {
        return messageList;
    }
}
