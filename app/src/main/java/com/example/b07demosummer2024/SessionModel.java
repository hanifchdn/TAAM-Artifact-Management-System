package com.example.b07demosummer2024;

import android.util.Log;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Model behind the current authenticated user, used to fetch user profile and signing out.
 */
public class SessionModel implements SessionContract.Model{
    private final FirebaseAuth auth;
    private final DatabaseReference usersRef;
    /**
     * Creates a SessionModel using the default Firebase Authentication instance
     * and users in the Firebase Database
     */
    public SessionModel() {
        this(FirebaseAuth.getInstance(),
                FirebaseDatabase.getInstance().getReference("users"));
    }
    /**
     * Creates a SessionModel using the given authentication instance and
     * user reference in the Firebase Database
     *
     * @param auth the Firebase Authentication instance used to access the current user
     * @param usersRef the database reference used for the current user
     */
    SessionModel(FirebaseAuth auth, DatabaseReference usersRef){
        this.auth = auth;
        this.usersRef = usersRef;
    }
    @Override
    public String currentUserUid(){
        return auth.getUid();
    }

    @Override
    public void fetchUserProfile(String uid, ProfileCallback callback) {
        usersRef.child(uid).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                callback.onProfileError(checkError(task.getException()));
                return;
            }
            User user = task.getResult().getValue(User.class);
            if (user == null) {
                callback.onProfileError("Profile not found.");
                return;
            }
            callback.onProfileLoaded(user);
        });
    }

    /**
     * Helper function to return error message
     * @param ex the exception object from the task
     * @return a human-readable error message describing the exception
     */
    private String checkError(Exception ex) {
        if (ex instanceof FirebaseNetworkException) {
            return "No internet connection.";
        }
        if (ex instanceof DatabaseException) {
            return "Couldn't read your profile.";
        }
        Log.e("SESSION", "profile read failed", ex);
        return "Something went wrong.";
    }
    @Override
    public void logOut(){
        auth.signOut();
        SessionManager.getInstance().clear();
    }
}