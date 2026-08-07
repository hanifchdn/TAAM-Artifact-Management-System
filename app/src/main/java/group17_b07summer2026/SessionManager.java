package group17_b07summer2026;

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

    /**
     * Sets the user for the current session.
     *
     * @param u the user to store as the current session user
     */
    public void setCurrentUser(User u){
        currentUser = u;
    }

    /**
     *
     * @return the user associated with the current session, or null if none is set
     */
    public User getCurrentUser(){
        return currentUser;
    }

    /**
     * Clears the current session by removing the current user.
     */
    public void clear(){
        currentUser = null;
    }
}