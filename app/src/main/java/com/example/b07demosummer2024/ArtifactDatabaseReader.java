package com.example.b07demosummer2024;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutionException;


/**
 * Allows for easy Reading to the db using the Artifact class.
 *
 * Due to Async nature of reading, all read functions will use async callback functions
 * For an example usage of this callback, view the contains method associated with this.
 */
public class ArtifactDatabaseReader {

    /**
     * Interface for GetItem callback
     */
    public interface GetArtifactItemCallback {
        /**
         * On Firebase read success, returns the artifact as a param
         * @param artifact returned artifact
         */
        void onSuccess(Artifact artifact);
        /**
         * On Firebase read failure, return error message
         * @param errorMessage Firebase error message
         */
        void onFailure(String errorMessage);
    }

    /**
     * Interface for contains method callback
     */
    public interface ContainsArtifactItemCallback {
        /**
         * On Firebase read success, returns the artifact as a param
         * @param contains boolean if a artifact with that LOT exists
         */
        void onSuccess(boolean contains);
        /**
         * On Firebase read failure, return error message
         * @param errorMessage Firebase error message
         */
        void onFailure(String errorMessage);
    }
    private FirebaseDatabase db; //firebase db object
    private DatabaseReference dbReference; //db reference that can read/write artifacts


    public ArtifactDatabaseReader() {
        db = FirebaseDatabase.getInstance("https://taam-artifact-storage-system-default-rtdb.firebaseio.com/");
        dbReference = db.getReference("artifacts");
    }

    /**
     *Gets am Artifact from the database
     *
     * @param LOT The LOT of Artifact
     * @param callback An anonymous object of GetArtifactItemCallback which will call
     */
    public void getItem(String LOT, GetArtifactItemCallback  callback) {
        if(LOT == null || LOT.trim().isEmpty()){
            callback.onFailure("LOT number cannot be empty.");
            return;
        }
        try {
            dbReference.child(LOT).get().addOnCompleteListener((dataSnapshotTask) -> {
                if (dataSnapshotTask.isSuccessful()) {
                    callback.onSuccess(dataSnapshotTask.getResult().getValue(Artifact.class));
                }
                else {
                    Exception exception = dataSnapshotTask.getException();
                    if(exception == null || exception.getMessage() == null || exception.getMessage().trim().isEmpty()){
                        callback.onFailure("A critical error has occurred. Please try again later.");
                    }
                    callback.onFailure(dataSnapshotTask.getException().getMessage());
                }
            });
        }
        catch (Exception e) {
            callback.onFailure("A critical error has occurred. Please try again later.");
        }
    }

    /**
     *Returns if a LOT number is found in the database
     * @param  LOT The LOT of the artifact
     * @param callback an async function that will be called once done
     */
    public void contains(String LOT, ContainsArtifactItemCallback callback) {
        getItem(LOT, new GetArtifactItemCallback() {

            @Override
            public void onSuccess(Artifact artifact) {
                callback.onSuccess(artifact != null);
            }

            @Override
            public void onFailure(String errorMessage) {
                callback.onFailure(errorMessage);
            }
        });
    }
}