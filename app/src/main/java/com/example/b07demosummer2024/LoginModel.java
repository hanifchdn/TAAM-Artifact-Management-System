package com.example.b07demosummer2024;

import android.util.Log;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginModel implements LoginContract.Model{
    private final FirebaseAuth auth;

    public LoginModel() {
        this(FirebaseAuth.getInstance());
    }

    // Added to allow for unit testing
    LoginModel(FirebaseAuth auth) {
        this.auth = auth;
    }

    @Override
    public void login(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
            task -> {
                if (task.isSuccessful()){
                    callback.onSuccess(auth.getUid());
                }
                else {
                    callback.onFailure(checkError(task.getException()));
                }
            });

    }

    private String checkError(Exception ex) {

        if (ex instanceof FirebaseAuthInvalidCredentialsException || ex instanceof FirebaseAuthInvalidUserException){
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
