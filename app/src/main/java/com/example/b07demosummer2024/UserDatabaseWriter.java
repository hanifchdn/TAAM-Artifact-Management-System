package com.example.b07demosummer2024;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Takes in a User class and will ONLY update user preference/collections/likes etc
 */
public class UserDatabaseWriter {
    private FirebaseDatabase db;
    private DatabaseReference dbReference;
    public UserDatabaseWriter() {
        db = FirebaseDatabase.getInstance("https://taam-artifact-storage-system-default-rtdb.firebaseio.com/");
        dbReference = db.getReference();
    }

    /**
     * Updates the liked artifact list for a user, given a UID and list
     * @param userUid UID of user to update
     * @param artifactList artifact list (of lot) to update
     * @param callback function
     */
    public void updateLikedArtifacts(String userUid, List<String> artifactList, WriteCallback callback) {
        dbReference.child(userUid).child("likedArtifacts").setValue(artifactList).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess();
                return;
            }
            else {
                if (task.getException() == null) {
                    callback.onFailure("An unknown error occured");
                    return;
                }
                callback.onFailure(task.getException().toString());
                return;
            }
        });
    }

    /**
     * Updates the liked artifact list for a user, given a user
     * @param user
     * @param callback
     */
    public void updateLikedArtifacts(User user, WriteCallback callback) {
        updateLikedArtifacts(user.getUid(), user.getLikedArtifacts(), callback);
    }

}
