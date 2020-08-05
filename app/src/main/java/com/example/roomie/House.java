package com.example.roomie;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Represents a house entity.
 * Each house has its own properties, and a list of its roomies.
 * Each roomy is represented as a mapping of its UID to its role in house.
 */
public class House implements Serializable {

    /**
     * Represents the possible roles the roomies can have.
     * Only the owner can invite / kick roomies, and only he can close the house.
     */
    public enum Roles {
        OWNER("owner"),
        ROOMY("roomy");

        public final String role;

        Roles(String role) {
            this.role = role;
        }
    }

    private @DocumentId String id;

    private String name;

    private String address;

    private String desc;

    private @ServerTimestamp Date creationTimestamp;

    private Map<String, Roles> roomies;

    public House() {

    }

    public House(String name, String address, String desc, Map<String, Roles> roomies) {
        this.name = name;
        this.address = address;
        this.desc = desc;
        this.roomies = roomies;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getDesc() {
        return desc;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public Map<String, Roles> getRoomies() {
        return roomies;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public void setRoomies(Map<String, Roles> roomies) {
        this.roomies = roomies;
    }
}
