package com.example.b07demosummer2024;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ArtifactDatabaseReader implements DatabaseReader<Artifact> {
    private FirebaseDatabase db;
    private DatabaseReference dbReference;

    public ArtifactDatabaseReader() {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://taam-artifact-storage-system-default-rtdb.firebaseio.com/");
        DatabaseReference dbReference = db.getReference("artifacts");
    }

    public Artifact itemLocation;

    public Artifact getItem(String LOT) {
        db = FirebaseDatabase.getInstance("https://taam-artifact-storage-system-default-rtdb.firebaseio.com/");
        dbReference = db.getReference("artifacts/" + LOT);
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemLocation = snapshot.getValue(Artifact.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return itemLocation;
    }
}
