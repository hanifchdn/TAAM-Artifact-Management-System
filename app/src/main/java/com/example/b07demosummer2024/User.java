package com.example.b07demosummer2024;

public class User {

    private String email;
    private String uid;
    private boolean isAdmin;

    public User(){

    }

    public User(String email, String uid, boolean isAdmin) {
        this.email = email;
        this.uid = uid;
        this.isAdmin = isAdmin;
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
