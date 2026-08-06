package com.example.b07demosummer2024;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class LikeDatabaseReader {

    /**
     * Interface for GetLikesOnArtifact callback
     */
    public interface GetLikesCallback {
        /**
         * On Firebase read success, returns the likes amount as a param
         * @param amountOfLikes , amount of likes
         */
        void onSuccess(int amountOfLikes);
        /**
         * On Firebase read failure, return error message
         * @param errorMessage Firebase error message
         */
        void onFailure(String errorMessage);
    }

    /**
     * Interface for hasUserLiked callback
     */
    public interface HasUserLikedCallback {
        /**
         * On Firebase read success, returns if the user has liked or not
         * @param isLiked , boolean if liked or not
         */
        void onSuccess(boolean isLiked);
        /**
         * On Firebase read failure, return error message
         * @param errorMessage Firebase error message
         */
        void onFailure(String errorMessage);
    }
    private FirebaseDatabase db; //firebase db object
    private DatabaseReference dbReference; //db reference that can read/write artifacts

    public LikeDatabaseReader() {
        db = FirebaseDatabase.getInstance("https://taam-artifact-storage-system-default-rtdb.firebaseio.com/");
        dbReference = db.getReference("likes");
    }

    /**
     *Gets all likes on an Artifact from the database
     *
     * @param LOT The LOT of Artifact
     * @param callback callback function
     */
    public void GetLikesOnArtifact(String LOT, GetLikesCallback callback) {
        dbReference.get().addOnSuccessListener(dataSnapshot -> {
            int amount = 0;
            // for each like
            for (DataSnapshot child: dataSnapshot.getChildren()){

                Like like = child.getValue(Like.class);
                if ( like != null && like.getArtifactLot().equals(LOT)) {
                    amount += 1;
                }
            }
            callback.onSuccess(amount);
        }).addOnFailureListener(e -> {
            callback.onFailure(null);
        });
    }

    /**
     * Determines if a user has liked an artifact or not
     * @param LOT The LOT of Artifact
     * @param callback callback function
     */
    public void hasUserLiked(String userId, String LOT, HasUserLikedCallback callback) {
        dbReference.get().addOnSuccessListener(dataSnapshot -> {
            // for each like
            for (DataSnapshot child: dataSnapshot.getChildren()){

                Like like = child.getValue(Like.class);
                if (like.getArtifactLot().equals(LOT) && like.getUserId().equals(userId)) {
                    callback.onSuccess(true);
                    return;
                }
            }
            callback.onSuccess(false);
            return;
        }).addOnFailureListener(e -> {
            callback.onFailure(e.getMessage());
            return;
        });
    }
}
