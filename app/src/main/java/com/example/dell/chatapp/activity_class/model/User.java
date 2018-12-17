package com.example.dell.chatapp.activity_class.model;

import java.io.Serializable;

public class User implements Serializable {
    String userID;
    String username;
    String email;
    String profileURL;

    public User() {
    }

    public User(String userID, String username, String email, String profileURL) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.profileURL = profileURL;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }


}
