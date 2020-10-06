package com.roomiemain.roomie;

public class User {

    private String uid;

    private String username;

    private String email;

    private String profilePicture;

    private String role;

    public User() {

    }

    public User(String uid, String username, String email, String profilePicture) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.profilePicture = profilePicture;
    }

    public User(String uid, String username, String email, String profilePicture, String role) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.profilePicture = profilePicture;
        this.role = role;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getRole() {
        return role;
    }
}
