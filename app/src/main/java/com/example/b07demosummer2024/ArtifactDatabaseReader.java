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
public class ArtifactDatabaseReader {

    public interface GetArtifactReaderCallback {
        void getArtifactCallback(Artifact artifact);
    }
    public interface ContainsReaderCallback {
        void containsCallback(boolean contains);
    }
    private FirebaseDatabase db;
    private DatabaseReference dbReference;

    private Artifact artifactReference;

    /**
     * Allows for reading from the db using the Artifact class.
     *
     */
    public ArtifactDatabaseReader() {
         db = FirebaseDatabase.getInstance("https://taam-artifact-storage-system-default-rtdb.firebaseio.com/");
         dbReference = db.getReference("artifacts");
    }

    /**
     *Gets am Artifact from the database
     *
     * @param LOT The LOT of Artifact
     * @param callback an async function that will be called once done
     */
    public void getItem(String LOT, GetArtifactReaderCallback callback) {
        try {
            dbReference.child(LOT).get().addOnCompleteListener((dataSnapshotTask) -> {
                callback.getArtifactCallback(dataSnapshotTask.getResult().getValue(Artifact.class));
            });
        }
        catch (Exception e) {
            callback.getArtifactCallback(null);
        }
    }

    /**
     *Returns if a LOT number is found
     *
     *
     * @param  LOT The LOT of the artifact
     * @param callback an async function that will be called once done
     *
     */
    public void contains(String LOT, ContainsReaderCallback callback) {
        getItem(LOT, (Artifact a) -> {
            callback.containsCallback(a != null);
        });
    }
}
