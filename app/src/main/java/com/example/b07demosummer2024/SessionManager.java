package com.example.b07demosummer2024;

/**
 * SessionManager acts as the hub to oversee current session.
 */
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private SessionManager() {

    }

    /**
     *
     * @return returns the instance of SessionManager
     */
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
