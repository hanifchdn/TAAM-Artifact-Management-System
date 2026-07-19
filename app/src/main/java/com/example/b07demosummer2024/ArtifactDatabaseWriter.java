package com.example.b07demosummer2024;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

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

    public ArtifactDatabaseWriter() {
        // set database cursor to artifacts section
        db = FirebaseDatabase.getInstance("https://taam-artifact-storage-system-default-rtdb.firebaseio.com/");
        dbReference = db.getReference("/artifacts");
    }

    /**
     *Adds an Artifact to the database.
     *
     * Uses the artifact LOT number as the unique ID to save the artifact.
     *
     * @param item the Artifact to add to database.
     *
     */
    @Override
    public void addToDatabase(DatabaseItem item) {
        if (!(item instanceof Artifact)) {
            return;
        }
        Artifact artifact = (Artifact) item;
        dbReference.child(artifact.getLOT()).setValue(artifact);

    }

    /**
     *Edits/Overwrites an Artifact to the database.
     *
     * Uses the artifact LOT number as the unique ID to overwrite the data associated with that LOT number.
     * Note that updateDatabase will act like ArtfactDatabaseWriter if it does not exist yet.
     *
     * @param item the Artifact to overwrite
     *
     */
    @Override
    public void updateDatabase(DatabaseItem item) {
        if (!(item instanceof Artifact)) {
            return;
        }
        Artifact artifact = (Artifact) item;
        dbReference.child(artifact.getLOT()).setValue(artifact);
    }

    /**
     *Deletes an artifact form the Database.
     *
     * Note that the Artifact that will be deleted will only depend on the LOT number,
     * if you wish to delete using the LOT number, use the overloaded method with int LOT
     *
     *
     * @param item The Artifact to delete.
     *
     */
    @Override
    public void deleteFromDatabase(DatabaseItem item) {
        if (!(item instanceof Artifact)) {
            return;
        }
        Artifact artifact = (Artifact) item;
        dbReference.child(artifact.getLOT()).removeValue();
    }

    /**
     *Deletes an artifact form the Database using it's LOT .
     *
     *If deleting an artifact that does not exist, the method
     * will do nothing.
     *
     *
     * @param LOT the LOT of the artifact to remove
     *
     */
    public void deleteFromDatabase(String LOT) {
        dbReference.child(LOT).removeValue();
    }
}
