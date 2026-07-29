package com.example.b07demosummer2024;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private SessionManager() {

    }
    public static synchronized SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }
    public void setCurrentUser(User u){
        currentUser = u;
    }
    public User getCurrentUser(){
        return currentUser;
    }
    public boolean isAdmin(){
        return currentUser != null && currentUser.isAdmin();
    }
    public void clear(){
        currentUser = null;
    }
}
