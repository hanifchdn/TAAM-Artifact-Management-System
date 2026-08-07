package group17_b07summer2026;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Writes comment data to the Firebase Database
 * Provides methods for adding and deleting comments in the Firebase
 */
public class CommentDatabaseWriter {
    // firebase db instances
    FirebaseDatabase db;
    DatabaseReference dbReference;

    /**
     * Creates a database writer object
     */
    public CommentDatabaseWriter() {
        // set database cursor to Comments section
        db = FirebaseDatabase.getInstance("https://taam-artifact-storage-system-default-rtdb.firebaseio.com/");
        dbReference = db.getReference("/comments");
    }

    /**
     * Adds a comment to the database.
     * Uses the comments id in order to create a new comment item in the database
     * @param comment the comment to write to the database
     * @param callback the callback notified when write succeeds or fails
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
     * Note that the comment that will be deleted is based on its id, and nothing else
     * If the comment does not exist, this method will do nothing and report no errors
     * @param comment The comment to delete.
     * @param callback The write callback on success/delete
     */
    public void deleteFromDatabase(Comment comment, WriteCallback callback) {
        deleteFromDatabase(comment.getId(), callback);
    }

    /**
     *Deletes a comment from the Database.
     * Note that the comment that will be deleted is based on its id, and nothing else
     * If the comment does not exist, this method will do nothing and report no errors
     * @param commentId The comment to delete.
     * @param callback The write callback on success/delete
     */
    public void deleteFromDatabase(String commentId, WriteCallback callback) {
        Task<Void> task = dbReference.child(commentId).removeValue();
        task.addOnCompleteListener(completeTask -> {
            if (completeTask.isSuccessful()) {
                callback.onSuccess();
                return;
            }
            else {
                if (completeTask.getException() != null) {
                    callback.onFailure("Unknown error occurred");
                    return;
                }
                else {
                    callback.onFailure(completeTask.getException().getMessage());
                }
            }
        });
    }
}
