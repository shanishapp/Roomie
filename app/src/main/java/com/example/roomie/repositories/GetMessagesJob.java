package com.example.roomie.repositories;

import com.example.roomie.FirestoreJob;
import com.example.roomie.house.chat.Message;

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
