package com.example.b07demosummer2024;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class AuthModel implements LoginContract.Model{
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    public void login(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
            task -> {
                if (task.isSuccessful()){
                    callback.onSuccess(auth.getCurrentUser().getUid());
                }
                else {
                    Log.e("AUTH", "reason", task.getException());
                    callback.onFailure("Login Failed");
                }
            });

    }
}
