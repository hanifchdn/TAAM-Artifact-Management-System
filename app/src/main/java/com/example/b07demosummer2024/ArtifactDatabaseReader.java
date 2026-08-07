package com.example.b07demosummer2024;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows for easy Reading to the db using the Artifact class.
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
     * Interface for receiving list of artifacts
     */
    public interface GetArtifactListReaderCallback{
        /**
         * Called when the artifact list has been successfully retrieved.
         * @param artifacts the list of retrieved artifacts
         */
        void getArtifactListCallback(List<Artifact> artifacts);
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

    /**
     * Check if the artifact list was loaded correctly.
     * Shows a toast message if the list is null or empty.
     * @param artifacts The list of artifacts loaded from the database.
     * @return true if artifact exist and can be shown, false otherwise.
     */
    public boolean handleArtifactListError(List<Artifact> artifacts){
        if(artifacts == null) {
            return false;
        }
        else if (artifacts.isEmpty()) {
            return false;
        }
        return true;
    }
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
     * Retrieves all artifacts from database.
     * If the database operation succeeds, the callback receives a list containing all
     * retrieved artifacts. If the operation fails, the callback receives null.
     * @param callback is called when the database operation completes.
     */
    public void getArtifactList(GetArtifactListReaderCallback callback){
        dbReference.get().addOnSuccessListener(dataSnapshot -> {
            List<Artifact> artifacts = new ArrayList<>();
            for (DataSnapshot child: dataSnapshot.getChildren()){
                Artifact artifact = child.getValue(Artifact.class);
                if(artifact != null){
                    artifacts.add(artifact);
                }
            }
            callback.getArtifactListCallback(artifacts);
        }).addOnFailureListener(e -> {
            callback.getArtifactListCallback(null);
        });
    }

    /**
     * Retrieves all artifacts that has a field which contains a substring from the database.
     * If the database operation succeeds, the callback receives a list containing all
     * artifacts that has the substring within the field. Ignores nested object fields
     * If the operation fails, the callback receives null.
     * @param substring the substring to search for in all fields
     * @param callback is called when the database operation completes.
     */
    public void getArtifactListBySubstring(String substring, GetArtifactListReaderCallback callback){
        dbReference.get().addOnSuccessListener(dataSnapshot -> {
            List<Artifact> artifacts = new ArrayList<>();

            // for each artifact
            for (DataSnapshot child: dataSnapshot.getChildren()){
                Artifact artifact = child.getValue(Artifact.class);

                // for each field
                for (DataSnapshot field : child.getChildren()) {

                    // if field itself is an object ignore it
                    if (field.hasChildren()) {
                        continue;
                    }

                    // Else it is a field representable as a string
                    String fieldValue = field.getValue(String.class);
                    if(artifact != null && fieldValue != null && fieldValue.toLowerCase().contains(substring.toLowerCase())){
                        artifacts.add(artifact);
                        break;
                    }
                }
            }
            callback.getArtifactListCallback(artifacts);
        }).addOnFailureListener(e -> {
            callback.getArtifactListCallback(null);
        });
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