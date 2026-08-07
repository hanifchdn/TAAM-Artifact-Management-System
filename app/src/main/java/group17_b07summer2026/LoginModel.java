package group17_b07summer2026;

import android.util.Log;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

/**
 * Model that handle user authentication with Firebase
 */
public class LoginModel implements LoginContract.Model{
    private final FirebaseAuth auth;

    /**
     * Create a login model using the FirebaseAuth instance
     */
    public LoginModel() {
        this(FirebaseAuth.getInstance());
    }

    /**
     * Created a login model with a provided FirebaseAuth instance
     * @param auth FirebaseAuth instance to use
     */
    LoginModel(FirebaseAuth auth) {
        this.auth = auth;
    }
    /**
     * Attempts to authenticate a user with email and password
     * @param email User's email address
     * @param password User's password
     * @param callback Callback for login success or failure
     */
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
    /**
     * Converts Firebase authentication errors into user-friendly messages
     * @param ex Authentication exception
     * @return Error message to display
     */
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
