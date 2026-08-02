package com.example.b07demosummer2024;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CommentDatabaseWriter {
    // firebase db instances
    FirebaseDatabase db;
    DatabaseReference dbReference;

    public CommentDatabaseWriter() {
        // set database cursor to Comments section
        db = FirebaseDatabase.getInstance("https://taam-artifact-storage-system-default-rtdb.firebaseio.com/");
        dbReference = db.getReference("/comments");
    }

    /**
     *Adds a comment to the database.
     *Uses the comments id in order to create a new comment item in the database
     * @param comment the comment to write to the database
     * @param callback a Write callback on error
     */
    public void addToDatabase(Comment comment, WriteCallback callback) {
        dbReference.child(comment.getId()).setValue(comment)
                .addOnSuccessListener( a -> {
                    if(callback != null){
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(exception -> {
                    if(callback != null){
                        callback.onFailure(exception.getMessage());
                    }
                });

    }

    /**
     *Deletes a comment from the Database.
     * Note that the comment that will be deleted is based on it's id, and nothing else
     * If the comment does not exist, this method will do nothing and report no errors
     * @param comment The comment to delete.
     */
    public void deleteFromDatabase(Comment comment) {
        dbReference.child(comment.getId()).removeValue();
    }

    /**
     *Deletes a comment from the Database.
     * Note that the comment that will be deleted is based on it's id, and nothing else
     * If the comment does not exist, this method will do nothing and report no errors
     * @param commentId The comment to delete.
     */
    public void deleteFromDatabase(String commentId) {
        dbReference.child(commentId).removeValue();
    }
}
