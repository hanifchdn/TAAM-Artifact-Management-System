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
 * Allows for easy Reading to the db using the Artifact class. MUST be wrapped in async function or be ran in a different thread
 *
 * When reading from this class, it must be done in a different thread than the UI thread to
 * prevent the UI thread from being blocked and a RuntimeException to occur
 *
 */
public class ArtifactDatabaseReader implements DatabaseReader {


    private FirebaseDatabase db;
    private DatabaseReference dbReference;

    private Artifact artifactReference;

    /**
     * Allows for easy Reading to the db using the Artifact class. MUST be wrapped in async function or be ran in a different thread
     *
     * When reading from this class, it must be done in a different thread than the UI thread to
     * prevent the UI thread from being blocked and a RuntimeException to occur.
     *
     */
    public ArtifactDatabaseReader() {
         db = FirebaseDatabase.getInstance("https://taam-artifact-storage-system-default-rtdb.firebaseio.com/");
         dbReference = db.getReference("artifacts");
    }

    /**
     *Gets am Artifact from the database
     *
     * MUST be used within a async caller such as an event listener or a CompletableFuture.supplyAsync()
     * or in a new thread to prevent a runtime error. Returns the Artifact using the LOT if found, if not found or error
     * returns null
     *
     * @param LOT The LOT of Artifact
     *
     */
    @Override
    public Artifact getItem(String LOT) {
        try {
            Task<DataSnapshot> task = dbReference.child(LOT).get();
            DataSnapshot data = Tasks.await(task);
            if (data.getValue() != null) {
                return data.getValue(Artifact.class);
            }
        }
        catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     *Returns if a LOT number is found
     *
     * MUST be used within a async caller such as an event listener or a CompletableFuture.supplyAsync()
     * or in a new thread to prevent a runtime error. Returns a boolean value of if the LOT number is found.
     *
     * @param  LOT The LOT of the artifact
     *
     */
    @Override
    public boolean contains(String LOT) {
        getItem(LOT);
        return (artifactReference != null);
    }
}
