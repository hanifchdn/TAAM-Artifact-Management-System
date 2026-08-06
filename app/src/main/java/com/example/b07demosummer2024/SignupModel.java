package com.example.b07demosummer2024;

import android.util.Log;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Model that handles user registration and profile creation
 */
public class SignupModel implements SignUpContract.Model{
    private final FirebaseAuth auth;
    private final DatabaseReference db;
    /**
     * Creates a sign-up model using default Firebase instances.
     */
    public SignupModel() {
        this(
                FirebaseAuth.getInstance(),
                FirebaseDatabase.getInstance().getReference("users")
        );
    }

    /**
     * Creates a sign-up model with provided Firebase dependencies.
     * @param auth Firebase authentication instance
     * @param db User database reference
     */
    SignupModel(FirebaseAuth auth, DatabaseReference db) {
        this.auth = auth;
        this.db = db;
    }

    /**
     * Creates a new user account and stores the user profile.
     * @param username User's username
     * @param email User's email
     * @param password User's password
     * @param callback Callback for sign-up success or failure
     */
    @Override
    public void signUp(String username, String email, String password, Authcallback callback){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task ->  {
                    if (!task.isSuccessful()){
                        callback.onFailure(checkError(task.getException()));
                        return;
                    }

                    String uid = auth.getUid();
                    db.child(uid).setValue(new User(username, email, uid, false))
                            .addOnCompleteListener(dbTask -> {
                            if (dbTask.isSuccessful()) {
                                callback.onSuccess(uid);
                            }
                            else {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user == null) {
                                    callback.onFailure(checkDBError(dbTask.getException()));
                                    return;
                                }
                                user.delete().addOnCompleteListener(delTask -> {
                                    if (delTask.isSuccessful()) {
                                        Log.e("SIGNUP", "Profile write failed", dbTask.getException());
                                        callback.onFailure("Could not complete signup.");
                                    } else {
                                        Log.e("SIGNUP", "Profile write failed AND rollback failed", delTask.getException());
                                        callback.onFailure("Something went wrong.");
                                    }
                                });
                            }
                        });
            });
    }
    /**
     * Converts database errors into user-friendly messages.
     * @param ex Database exception
     * @return Error message to display
     */
    private String checkDBError(Exception ex) {

        Log.e("SIGNUP", "reason", ex);
        return "Something went wrong.";
    }
    /**
     * Converts authentication errors into user-friendly messages.
     * @param ex Authentication exception
     * @return Error message to display
     */
    private String checkError(Exception ex) {
        if (ex instanceof FirebaseAuthUserCollisionException){
            return "User already exists.";
        }
        if (ex instanceof FirebaseAuthWeakPasswordException){
            return "Password is too weak.";
            // Firebase hardcodes less than 6 characters to weak password.
        }
        if (ex instanceof FirebaseAuthInvalidCredentialsException){
            return "Invalid email.";
        }
        if (ex instanceof FirebaseNetworkException){
            return "No internet connection.";
        }
        if (ex instanceof FirebaseTooManyRequestsException){
            return "Try again later.";
        }
        Log.e("AUTH", "reason", ex);
        return "Something went wrong.";
    }
}
