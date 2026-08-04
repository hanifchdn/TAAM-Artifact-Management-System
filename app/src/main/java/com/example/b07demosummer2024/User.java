package com.example.b07demosummer2024;

import java.util.ArrayList;
import java.util.List;

import kotlin.collections.ArrayDeque;

public class User {

    private String username;
    private String email;
    private String uid;
    private boolean isAdmin;
    public User(){

    }
    public User(String username, String email, String uid, boolean isAdmin) {
        this.username = username;
        this.email = email;
        this.uid = uid;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
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
