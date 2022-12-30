package org.example.Entities;

public class User {
    private String email;
    private int user_id;
    private int session_id;

    public User(String email, int user_id, int session_id) {
        this.email = email;
        this.user_id = user_id;
        this.session_id = session_id;
    }

    public String getEmail() {
        return email;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getSession_id() {
        return session_id;
    }
}
