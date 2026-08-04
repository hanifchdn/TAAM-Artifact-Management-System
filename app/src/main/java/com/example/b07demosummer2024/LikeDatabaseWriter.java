package com.example.b07demosummer2024;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

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
     *Adds an Like to the database.
     *
     * Uses the LikeId as the unique ID to save the like.
     *
     * @param like the like to add to database.
     * @param callback a Write callback on error
     */
    public void addToDatabase(Like like, WriteCallback callback) {
        dbReference.child(like.getLikeId()).setValue(like)
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
     *Removes an Like to the database.
     *
     * Uses the artifact Lot numebr and userID to remove the like
     *
     * @param userId the userId of the owner of the like
     * @param artifactLot the LOT of the artifact
     * @param callback a Write callback on error
     */
    public void removeFromDatabase(String userId, String artifactLot, WriteCallback callback) {
        dbReference.get().addOnSuccessListener(dataSnapshot -> {
            List<Like> likes = new ArrayList<>();

            // for each like
            for (DataSnapshot child: dataSnapshot.getChildren()){
                Like like = child.getValue(Like.class);
                if (like == null || like.getUserId() == null || like.getArtifactLot() == null) {
                    callback.onFailure("Bad data");
                }
                if (like.getUserId().compareTo(userId) == 0 && like.getArtifactLot().compareTo(artifactLot) == 0) {
                    dbReference.child(like.getLikeId()).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onSuccess();
                            return;
                        }
                        else {
                            callback.onFailure("Bad Database read");
                        }
                    });
                }
            }
        }).addOnFailureListener(e -> {
            callback.onFailure("Error connecting to firebase");
        });
    }

}

