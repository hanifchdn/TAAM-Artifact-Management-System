package com.example.b07demosummer2024;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class ArtifactDatabaseWriter implements DatabaseAdder<Artifact>, DatabaseDeleter<Artifact>, DatabaseUpdater<Artifact>{
    FirebaseDatabase db;
    DatabaseReference dbReference;
    public ArtifactDatabaseWriter() {
        db = FirebaseDatabase.getInstance("https://taam-artifact-storage-system-default-rtdb.firebaseio.com/");
        dbReference = db.getReference("/artifacts");
    }

    @Override
    public void addToDatabase(Artifact artifact) {
        dbReference.child(String.valueOf(artifact.getLOT())).push().setValue(artifact);
    }

    @Override
    public void updateDatabase(Artifact artifact) {

        dbReference.child(String.valueOf(artifact.getLOT())).setValue(artifact);
    }

    @Override
    public void deleteFromDatabase(Artifact artifact) {
        dbReference.child(String.valueOf(artifact.getLOT())).removeValue();
    }

    public void deleteFromDatabase(int LOT) {
        dbReference.child(String.valueOf(LOT)).removeValue();
    }
}
