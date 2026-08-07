package com.example.b07demosummer2024;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

/**
 * Takes in a User class and will ONLY update user preference/collections/likes etc
 */
public class UserDatabaseWriter {
    private FirebaseDatabase db;
    private DatabaseReference dbReference;

    /**
     * Creates a UserDatabaseWriter and initializes a reference to the users
     * section of the Firebase Database
     */
    public UserDatabaseWriter() {
        db = FirebaseDatabase.getInstance("https://taam-artifact-storage-system-default-rtdb.firebaseio.com/");
        dbReference = db.getReference().child("users");
    }

    /**
     * Updates the savedArtifacts of a given userUid
     * @param userUid of the user to update savedArtifactList
     * @param savedArtifactList of the user to update
     * @param callback on success/failure of database query
     */
    public void updateSavedArtifacts(String userUid, List<String> savedArtifactList, WriteCallback callback) {
        dbReference.child(userUid).child("savedArtifactList").setValue(savedArtifactList).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess();
                return;
            }
            else {
                if (task.getException() == null) {
                    callback.onFailure("An unknown error occurred");
                    return;
                }
                callback.onFailure(task.getException().toString());
                return;
            }
        });
    }


    /**
     * Updates the savedArtifacts of a given userUid
     * @param user object on which to update savedArtifacts in database
     * @param callback on success/failure of database query
     */
    public void updateSavedArtifacts(User user, WriteCallback callback) {
        updateSavedArtifacts(user.getUid(), user.getSavedArtifactList(), callback);
    }


}