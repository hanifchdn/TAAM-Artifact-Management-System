package com.example.b07demosummer2024;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
public class CommentDatabaseReader {
    FirebaseDatabase db;
    DatabaseReference dbReference;
    public CommentDatabaseReader() {
        //set database cursor to comment section
        db = FirebaseDatabase.getInstance("https://taam-artifact-storage-system-default-rtdb.firebaseio.com/");
        dbReference = db.getReference("/comments");
    }

    /**
     * Callback used to report the result of a getCommentsForArtifact() call
     */
    public interface GetCommentsForArtifactCallback {
        void onSuccess(List<Comment> comments);
        void onFailure(String e);
    }

    /**
     * Retrieves all comments belong to an artifact.
     * @param lot the lot number of the artifact whose comment should be retrieved
     * @param callback a callback that will be notified with the retrieved comments or error message if retrieval fails
     */
    public void getCommentsForArtifact(String lot, GetCommentsForArtifactCallback callback){
        dbReference.orderByChild("artifactLot").equalTo(lot).get()
                .addOnSuccessListener(dataSnapshot -> {
                    List<Comment> comments = new ArrayList<>();
                    for(DataSnapshot child: dataSnapshot.getChildren()){
                        Comment comment = child.getValue(Comment.class);
                        if(comment != null){
                            comments.add(comment);
                        }
                    }
                    callback.onSuccess(comments);
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e.getMessage());
                });
    }

}