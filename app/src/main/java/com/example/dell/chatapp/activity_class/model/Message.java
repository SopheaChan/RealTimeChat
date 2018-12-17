package com.example.dell.chatapp.activity_class.model;

public class Message {
    private String message;
    private Long sentDate;
    private User sender;


    public Message() {
    }

    public Message(String message, Long sentDate, User sender) {
        this.message = message;
        this.sentDate = sentDate;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getSentDate() {
        return sentDate;
    }

    public void setSentDate(Long sentDate) {
        this.sentDate = sentDate;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }
}
