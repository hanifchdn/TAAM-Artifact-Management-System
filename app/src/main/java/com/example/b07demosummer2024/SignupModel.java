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

public class SignupModel implements SignUpContract.Model{
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
    @Override
    public void signUp(String email, String password, Authcallback callback){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task ->  {
                    if (!task.isSuccessful()){
                        callback.onFailure(checkError(task.getException()));
                        return;
                    }

                    String uid = auth.getUid();
                    db.child(uid).setValue(new User(email, uid, false))
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

    private String checkDBError(Exception ex) {

        Log.e("SIGNUP", "reason", ex);
        return "Something went wrong.";
    }

    private String checkError(Exception ex) {
        if (ex instanceof FirebaseAuthUserCollisionException){
            return "User already exist.";
        }
        if (ex instanceof FirebaseAuthWeakPasswordException){
            return "Password is too weak.";
            // Firebase hardcodes less than 6 characters to weak password.
        }
        if (ex instanceof FirebaseAuthInvalidCredentialsException){
            return "Invalid email or password.";
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
