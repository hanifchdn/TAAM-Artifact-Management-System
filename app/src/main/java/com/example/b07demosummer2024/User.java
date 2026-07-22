package com.example.b07demosummer2024;

public class User {

    private String email;
    private String uid;
    private boolean isAdmin;

    public User(){

    }

    public User(String username, String email, String uid) {
        this.email = email;
        this.uid = uid;
        this.isAdmin = false;
    }
    public String getUid(){
        return uid;
    }
    public String getEmail() {
        return email;
    }
    public boolean isAdmin() {
        return isAdmin;
    }
}
