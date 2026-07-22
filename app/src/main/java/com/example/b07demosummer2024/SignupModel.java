package com.example.b07demosummer2024;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class SignupModel implements SignUpContract.Model{
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    public void signUp(String email, String password, Authcallback callback){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task ->  {
                    if (task.isSuccessful()) {
                        callback.onSuccess(auth.getUid());
                    } else {
                        callback.onFailure(checkError(task.getException()));
                    }
            });
    }

    private String checkError(Exception ex) {
        return "";
    }
}
