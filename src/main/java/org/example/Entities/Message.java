package org.example.Entities;

public class Message {
    String sessionId;
    int profileId;
    String message;
    String time;
    boolean isUserSender;



    public Message(int profileId, String message, String time, boolean isUserSender) {
        this.profileId = profileId;
        this.message = message;
        this.time = time;
        this.isUserSender = isUserSender;
    }

    public Message(String sessionId, int profileId, String message, boolean isUserSender) {
        this.sessionId = sessionId;
        this.profileId = profileId;
        this.message = message;
        this.isUserSender = isUserSender;
    }

    public String getMessage() {
        return message;
    }

    public int getProfileId() {
        return profileId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getTime() {
        return time;
    }

    public boolean isUserSender() {
        return isUserSender;
    }
}
