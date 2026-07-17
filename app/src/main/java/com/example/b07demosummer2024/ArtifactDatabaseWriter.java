package com.example.b07demosummer2024;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class ArtifactDatabaseWriter implements DatabaseAdder, DatabaseDeleter, DatabaseUpdater{
    FirebaseDatabase db;
    DatabaseReference dbReference;
    public ArtifactDatabaseWriter() {
        db = FirebaseDatabase.getInstance("https://taam-artifact-storage-system-default-rtdb.firebaseio.com/");
        dbReference = db.getReference("/artifacts");
    }

    @Override
    public void addToDatabase(DatabaseItem item) {
        if (!(item instanceof Artifact)) {
            return;
        }
        Artifact artifact = (Artifact) item;
        String LOT = dbReference.push().getKey();
        dbReference.child(LOT).setValue(artifact);
    }

    @Override
    public void updateDatabase(DatabaseItem item) {
        if (!(item instanceof Artifact)) {
            return;
        }
        Artifact artifact = (Artifact) item;
        dbReference.child(artifact.getLOT()).setValue(artifact);
    }

    @Override
    public void deleteFromDatabase(DatabaseItem item) {
        if (!(item instanceof Artifact)) {
            return;
        }
        Artifact artifact = (Artifact) item;
        dbReference.child(artifact.getLOT()).removeValue();
    }

    public void deleteFromDatabase(int LOT) {
        dbReference.child(String.valueOf(LOT)).removeValue();
    }
}
