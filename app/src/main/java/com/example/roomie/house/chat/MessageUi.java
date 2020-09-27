package com.example.roomie.house.chat;

public class MessageUi {

    public static final int OUTGOING_MESSAGE = 0;

    public static final int INCOMING_MESSAGE = 1;

    public static final int HEADER = 2;


    private int messageType;

    private String headerTitle;

    private String senderName;

    private String senderColor;

    private Message message;


    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setSenderColor(String senderColor) {
        this.senderColor = senderColor;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public int getMessageType() {
        return messageType;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderColor() {
        return senderColor;
    }

    public Message getMessage() {
        return message;
    }
}
