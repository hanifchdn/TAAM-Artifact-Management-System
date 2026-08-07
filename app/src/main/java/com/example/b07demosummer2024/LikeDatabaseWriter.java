package com.example.b07demosummer2024;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Writes like data to the Firebase Database
 * Provides methods for adding and deleting likes in the Firebase
 */
public class LikeDatabaseWriter {
    // firebase db instances
    FirebaseDatabase db;
    DatabaseReference dbReference;

    public LikeDatabaseWriter() {
        // set database cursor to artifacts section
        db = FirebaseDatabase.getInstance("https://taam-artifact-storage-system-default-rtdb.firebaseio.com/");
        dbReference = db.getReference("/likes");
    }

    /**
     * Adds a Like to the database.
     * Uses the LikeId as the unique ID to save the like.
     *
     * @param like the like to add to database.
     * @param callback a Write callback on error
     */
    public void addToDatabase(Like like, WriteCallback callback) {
        dbReference.child(like.getArtifactLot()).child(like.getUserId()).setValue(like)
                .addOnSuccessListener( a -> {
                    if(callback != null){
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    if(callback != null){
                        callback.onFailure(e.getMessage());
                    }
                });

    }

    /**
     * Removes a Like to the database.
     * Uses the artifact Lot number and userID to remove the like
     *
     * @param userId the userId of the owner of the like
     * @param artifactLot the LOT of the artifact
     * @param callback a Write callback on error
     */
    public void removeFromDatabase(String userId, String artifactLot, WriteCallback callback) {
        dbReference.child(artifactLot).child(userId).removeValue().addOnSuccessListener(dataSnapshot -> {
            callback.onSuccess();
            return;
        }).addOnFailureListener(e -> {
            callback.onFailure("Error connecting to firebase");
        });
    }

}

