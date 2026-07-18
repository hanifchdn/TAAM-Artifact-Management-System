package com.example.b07demosummer2024;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutionException;

public class ArtifactDatabaseReader implements DatabaseReader {
    private FirebaseDatabase db;
    private DatabaseReference dbReference;

    private Artifact artifactReference;

    public ArtifactDatabaseReader() {
         db = FirebaseDatabase.getInstance("https://taam-artifact-storage-system-default-rtdb.firebaseio.com/");
         dbReference = db.getReference("artifacts");
    }

    @Override
    public Artifact getItem(String LOT) {
        try {
            Tasks.await(dbReference.child(LOT).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    artifactReference = task.getResult().getValue(Artifact.class);
                }
                else {
                    artifactReference = null;
                }
            }));
        }
        catch (ExecutionException | InterruptedException exception) {
            return null;
        }
        return artifactReference;
    }

    @Override
    public boolean contains(String LOT) {
        getItem(LOT);
        return (artifactReference != null);
    }
}
