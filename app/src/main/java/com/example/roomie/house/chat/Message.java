package com.example.roomie.house.chat;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {

    private @DocumentId String id;

    private String sender;

    private @ServerTimestamp Date sendTimestamp;

    private String content;


    public void setId(String id) {
        this.id = id;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setSendTimestamp(Date sendTimestamp) {
        this.sendTimestamp = sendTimestamp;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public Date getSendTimestamp() {
        return sendTimestamp;
    }

    public String getContent() {
        return content;
    }
}
