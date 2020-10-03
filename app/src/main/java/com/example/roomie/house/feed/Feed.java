package com.example.roomie.house.feed;

import com.example.roomie.User;

import java.util.Date;

public class Feed {

    private int type;
    private String roommieName;
    private String description;
    private Date dateCreated;

    public Feed(int type, String roommieName, String description, Date dateCreated){
        this.type = type;
        this.roommieName = roommieName;
        this.description = description;
        this.dateCreated = dateCreated;
    }

    public int getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getRoommieName() {
        return roommieName;
    }

}
