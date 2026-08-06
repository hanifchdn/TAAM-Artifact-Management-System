package com.example.b07demosummer2024;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows for easy Writing to the db using the Artifact class.
 *
 * When passed in an Artifact object, the class will send a write request to the firebase db depending on nature of the request.
 * i.e delete will delete, update will update, and add will add.
 *
 */
public class ArtifactDatabaseWriter implements DatabaseAdder, DatabaseDeleter, DatabaseUpdater{
    // firebase db instances
    FirebaseDatabase db;
    DatabaseReference dbReference;
    DatabaseReference commentReference;

    public ArtifactDatabaseWriter() {
        // set database cursor to artifacts section
        db = FirebaseDatabase.getInstance("https://taam-artifact-storage-system-default-rtdb.firebaseio.com/");
        dbReference = db.getReference("/artifacts");
        commentReference = db.getReference("/comments");
    }

    /**
     *Adds an Artifact to the database.
     *
     * Uses the artifact LOT number as the unique ID to save the artifact.
     *
     * @param item the Artifact to add to database.
     * @param callback a Write callback on error
     */

    @Override
    public void addToDatabase(DatabaseItem item, WriteCallback callback) {
        if (!(item instanceof Artifact)) {
            if(callback != null){
                callback.onFailure("Item is not an artifact");
                return;
            }
            return;
        }
        Artifact artifact = (Artifact) item;
        dbReference.child(artifact.getLOT()).setValue(artifact)
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
     *Edits/Overwrites an Artifact to the database.
     *
     * Uses the artifact LOT number as the unique ID to overwrite the data associated with that LOT number.
     * Note that updateDatabase will act like ArtfactDatabaseWriter if it does not exist yet.
     *
     * @param item the Artifact to overwrite
     * @param writeCallback A callback on failure/success
     */
    @Override
    public void updateDatabase(DatabaseItem item, WriteCallback writeCallback) {
        addToDatabase(item, writeCallback);
    }
    /**
     *Deletes an artifact form the Database.
     *
     * Note that the Artifact that will be deleted will only depend on the LOT number,
     * if you wish to delete using the LOT number, use the overloaded method with int LOT
     *
     *
     * @param item The Artifact to delete.
     * @param callback a callback that will run a on success/failure method
     */
    @Override
    public void deleteFromDatabase(DatabaseItem item, WriteCallback callback) {
        if (!(item instanceof Artifact)) {
            if(callback != null){
                callback.onFailure("Item is not an artifact");
                return;
            }
            return;
        }
        deleteFromDatabase(item.getLOT(), callback);

    }

    /**
     * Deletes an artifact from the Database using its LOT.
     * Also deletes all comments under the artifact.
     *
     * If deleting an artifact that does not exist, the method
     * will do nothing.
     *
     *
     * @param LOT the LOT of the artifact to remove
     *
     */
    public void deleteFromDatabase(String LOT, WriteCallback callback) {
        commentReference.orderByChild("artifactLot").equalTo(LOT).get()
                .addOnSuccessListener(snapshot ->{
                    List<Task<Void>> deleteTask = new ArrayList<>();

                    for (DataSnapshot child : snapshot.getChildren()) {
                        deleteTask.add(child.getRef().removeValue());
                    }

                    deleteTask.add(dbReference.child(LOT).removeValue());

                    Tasks.whenAll(deleteTask).addOnSuccessListener(x ->{
                        if(callback != null){
                            callback.onSuccess();
                        }
                    }).addOnFailureListener(e -> {
                        if(callback != null){
                            callback.onFailure(e.getMessage());
                        }
                    });
                }).addOnFailureListener(e -> {
                    if(callback != null){
                        callback.onFailure(e.getMessage());
                    }
                });
    }
}