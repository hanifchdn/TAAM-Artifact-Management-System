package com.example.b07demosummer2024;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactDatabaseWriterAddTest {

    private static final String DATABASE_URL = "https://taam-artifact-storage-system-default-rtdb.firebaseio.com/";

    @Mock
    private FirebaseDatabase firebaseDatabase;

    @Mock
    private DatabaseReference artifactsDir;

    @Mock
    private DatabaseReference lot;

    @Mock
    private Task<Void> setValueTask;

    @Mock
    private WriteCallback callback;

    @Mock
    private DatabaseItem notAnArtifact;

    private MockedStatic<FirebaseDatabase> mockedFirebaseDatabase;

    private ArtifactDatabaseWriter writer;

    private Artifact artifact;

    // Setting up the test environment
    @Before
    public void setUp() {
        mockedFirebaseDatabase = mockStatic(FirebaseDatabase.class);

        mockedFirebaseDatabase.when(() -> FirebaseDatabase.getInstance(DATABASE_URL))
                .thenReturn(firebaseDatabase);

        when(firebaseDatabase.getReference("/artifacts"))
                .thenReturn(artifactsDir);

        writer = new ArtifactDatabaseWriter();

        artifact = new Artifact(
                "LOT100",
                "Ceramic Bowl",
                "Historical ceramic bowl",
                "Ceramics",
                "Ceramic",
                "Ming Dynasty (1368-1644 CE)"
        );
    }

    @After
    public void tearDown() {
        mockedFirebaseDatabase.close();
    }

    /**
     * Sets the Firebase write chain:
     * /artifacts
     *      -> child("LOT100")
     *      -> setValue(artifact)
     */
    private void configureFirebaseWrite() {
        when(artifactsDir.child("LOT100"))
                .thenReturn(lot);

        when(lot.setValue(artifact))
                .thenReturn(setValueTask);

        when(setValueTask.addOnSuccessListener(any(OnSuccessListener.class)))
                .thenReturn(setValueTask);

        when(setValueTask.addOnFailureListener(any(OnFailureListener.class)))
                .thenReturn(setValueTask);
    }

    @Test
    public void addToDatabase_validArtifactWritesCorrectly() { /* Tests if the artifact is written
                                                                  to the Firebase correctly
                                                                  */
        configureFirebaseWrite();

        writer.addToDatabase(artifact, callback);

        verify(artifactsDir).child("LOT100");
        verify(lot).setValue(artifact);
    }

    @Test
    public void addToDatabase_firebaseSuccessCallsSucessCallback() { /* Tests if a successful
                                                                            write calls the success
                                                                            callback
                                                                            */
        configureFirebaseWrite();

        ArgumentCaptor<OnSuccessListener<Void>> successCaptor = ArgumentCaptor.forClass(OnSuccessListener.class);

        writer.addToDatabase(artifact, callback);

        verify(setValueTask).addOnSuccessListener(successCaptor.capture());

        successCaptor.getValue().onSuccess(null);

        verify(callback).onSuccess();
        verify(callback, never()).onFailure(any());
    }

    @Test
    public void addToDatabase_firebaseFailureCallsFailureCallback() { /* Tests if a failed write
                                                                         calls the failure callback
                                                                         */
        configureFirebaseWrite();

        ArgumentCaptor<OnFailureListener> failureCaptor = ArgumentCaptor.forClass(OnFailureListener.class);

        writer.addToDatabase(artifact, callback);

        verify(setValueTask).addOnFailureListener(failureCaptor.capture());

        Exception exception = new Exception("Some Exception");

        failureCaptor.getValue().onFailure(exception);

        verify(callback).onFailure("Some Exception");
        verify(callback, never()).onSuccess();
    }

    @Test
    public void addToDatabase_notAnArtifactCallsFailureAndDoesNotWrite() { /* Tests if attempting to
                                                                              write a non-artifact
                                                                              object calls failure,
                                                                              and prevents writing
                                                                              */
        writer.addToDatabase(notAnArtifact, callback);

        verify(callback).onFailure("Item is not an artifact");

        verify(artifactsDir, never()).child(any());
    }

    @Test
    public void addToDatabase_nullItemCallsFailureAndDoesNotWrite() { /* Tests if attempting to
                                                                         write a null object calls
                                                                         failure, and prevents
                                                                         writing
                                                                         */
        writer.addToDatabase(null, callback);

        verify(callback).onFailure("Item is not an artifact");

        verify(artifactsDir, never()).child(any());
    }
}