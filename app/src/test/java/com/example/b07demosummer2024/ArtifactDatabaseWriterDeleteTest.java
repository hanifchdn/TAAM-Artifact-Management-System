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
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactDatabaseWriterDeleteTest {

    private static final String DATABASE_URL = "https://taam-artifact-storage-system-default-rtdb.firebaseio.com/";

    @Mock
    private FirebaseDatabase firebaseDatabase;

    @Mock
    private DatabaseReference artifactsDir;

    @Mock
    private DatabaseReference commentsDir;

    @Mock
    private DatabaseReference likesDir;

    @Mock
    private DatabaseReference lot;

    @Mock
    private Query commentsOrderedByArtifactLot;

    @Mock
    private Query matchingComments;

    @Mock
    private Query likesOrderedByArtifactLot;

    @Mock
    private Query matchingLikes;

    @Mock
    private Task<DataSnapshot> commentGetTask;

    @Mock
    private Task<DataSnapshot> likeGetTask;

    @Mock
    private DataSnapshot commentSnapshot;

    @Mock
    private DataSnapshot likeSnapshot;

    @Mock
    private DataSnapshot commentOne;

    @Mock
    private DataSnapshot commentTwo;

    @Mock
    private DataSnapshot likeOne;

    @Mock
    private DataSnapshot likeTwo;

    @Mock
    private DatabaseReference commentOneRef;

    @Mock
    private DatabaseReference commentTwoRef;

    @Mock
    private DatabaseReference likeOneRef;

    @Mock
    private DatabaseReference likeTwoRef;

    @Mock
    private Task<Void> commentOneDeleteTask;

    @Mock
    private Task<Void> commentTwoDeleteTask;

    @Mock
    private Task<Void> likeOneDeleteTask;

    @Mock
    private Task<Void> likeTwoDeleteTask;

    @Mock
    private Task<Void> artifactDeleteTask;

    @Mock
    private Task<Void> allDeleteTask;

    @Mock
    private WriteCallback callback;

    @Mock
    private DatabaseItem notAnArtifact;

    private MockedStatic<FirebaseDatabase> mockedFirebaseDatabase;
    private MockedStatic<Tasks> mockedTasks;

    private ArtifactDatabaseWriter writer;
    private Artifact artifact;

    private List<Task<?>> capturedDeleteTasks;

    @Before
    public void setUp() {
        mockedFirebaseDatabase = mockStatic(FirebaseDatabase.class);
        mockedTasks = mockStatic(Tasks.class);

        mockedFirebaseDatabase.when(() -> FirebaseDatabase.getInstance(DATABASE_URL))
                .thenReturn(firebaseDatabase);

        when(firebaseDatabase.getReference("/artifacts"))
                .thenReturn(artifactsDir);
        when(firebaseDatabase.getReference("/comments"))
                .thenReturn(commentsDir);
        when(firebaseDatabase.getReference("/likes"))
                .thenReturn(likesDir);

        writer = new ArtifactDatabaseWriter();

        artifact = new Artifact(
                "LOT100",
                "Ceramic Bowl",
                "Historical ceramic bowl",
                "Ceramics",
                "Ceramic",
                "Ming Dynasty (1368-1644 CE)"
        );

        // Configuring queries
        when(commentsDir.orderByChild("artifactLot"))
                .thenReturn(commentsOrderedByArtifactLot);

        when(commentsOrderedByArtifactLot.equalTo("LOT100"))
                .thenReturn(matchingComments);

        when(matchingComments.get())
                .thenReturn(commentGetTask);

        when(commentGetTask.addOnSuccessListener(any(OnSuccessListener.class)))
                .thenReturn(commentGetTask);

        when(commentGetTask.addOnFailureListener(any(OnFailureListener.class)))
                .thenReturn(commentGetTask);

        when(likesDir.orderByChild("artifactLot"))
                .thenReturn(likesOrderedByArtifactLot);

        when(likesOrderedByArtifactLot.equalTo("LOT100"))
                .thenReturn(matchingLikes);

        when(matchingLikes.get())
                .thenReturn(likeGetTask);

        when(likeGetTask.addOnSuccessListener(any(OnSuccessListener.class)))
                .thenReturn(likeGetTask);

        when(likeGetTask.addOnFailureListener(any(OnFailureListener.class)))
                .thenReturn(likeGetTask);

        // Configure delete
        when(artifactsDir.child("LOT100"))
                .thenReturn(lot);

        when(lot.removeValue())
                .thenReturn(artifactDeleteTask);

        // Configure whenAll task
        when(allDeleteTask.addOnSuccessListener(any(OnSuccessListener.class)))
                .thenReturn(allDeleteTask);

        when(allDeleteTask.addOnFailureListener(any(OnFailureListener.class)))
                .thenReturn(allDeleteTask);

        mockedTasks.when(() -> Tasks.whenAll(anyCollection()))
                .thenAnswer(invocation -> {
                    Collection<? extends Task<?>> tasks =
                            invocation.getArgument(0);

                    capturedDeleteTasks = new ArrayList<>(tasks);

                    return allDeleteTask;
                });
    }

    @After
    public void tearDown() {
        mockedTasks.close();

        mockedFirebaseDatabase.close();
    }

    @Test
    public void deleteFromDatabase_validLotQueriesCommentsAndLikes() { /* Tests if comments and likes
                                                                         are queried correctly
                                                                         */
        configureRelatedCommentsAndLikes();

        writer.deleteFromDatabase("LOT100", callback);

        verify(commentsDir).orderByChild("artifactLot");
        verify(commentsOrderedByArtifactLot).equalTo("LOT100");

        triggerCommentLookupSuccess();

        verify(likesDir).orderByChild("artifactLot");
        verify(likesOrderedByArtifactLot).equalTo("LOT100");

        triggerLikeLookupSuccess();
    }

    @Test
    public void deleteFromDatabase_validLotDeletesAllRelatedData() { /* Tests if all comments, likes,
                                                                       and the artifact itself are
                                                                       correctly deleted
                                                                       */
        configureRelatedCommentsAndLikes();

        writer.deleteFromDatabase("LOT100", callback);

        triggerCommentLookupSuccess();

        verify(commentOneRef).removeValue();
        verify(commentTwoRef).removeValue();

        triggerLikeLookupSuccess();

        verify(likeOneRef).removeValue();
        verify(likeTwoRef).removeValue();

        verify(artifactsDir).child("LOT100");
        verify(lot).removeValue();

        assertTrue(capturedDeleteTasks.contains(commentOneDeleteTask));
        assertTrue(capturedDeleteTasks.contains(commentTwoDeleteTask));
        assertTrue(capturedDeleteTasks.contains(likeOneDeleteTask));
        assertTrue(capturedDeleteTasks.contains(likeTwoDeleteTask));
        assertTrue(capturedDeleteTasks.contains(artifactDeleteTask));
    }

    @Test
    public void deleteFromDatabase_allDeletesSucceedCallsSuccess() { /* Tests if successfully deleting
                                                                       all comments, likes, and the
                                                                       artifact calls the success
                                                                       callback
                                                                       */
        configureRelatedCommentsAndLikes();

        writer.deleteFromDatabase("LOT100", callback);

        triggerCommentLookupSuccess();
        triggerLikeLookupSuccess();
        triggerAllDeletesSuccess();

        verify(callback).onSuccess();
        verify(callback, never()).onFailure(anyString());
    }

    @Test
    public void deleteFromDatabase_deleteFailsCallsFailure() { /* Tests if a failure while deleting
                                                                  any related data calls the failure
                                                                  callback
                                                                  */
        configureRelatedCommentsAndLikes();

        writer.deleteFromDatabase("LOT100", callback);

        triggerCommentLookupSuccess();
        triggerLikeLookupSuccess();

        ArgumentCaptor<OnFailureListener> failureCaptor = ArgumentCaptor.forClass(OnFailureListener.class);

        verify(allDeleteTask).addOnFailureListener(failureCaptor.capture());

        failureCaptor.getValue().onFailure(new Exception("Deletion failed"));

        verify(callback).onFailure("Deletion failed");
        verify(callback, never()).onSuccess();
    }

    @Test
    public void deleteFromDatabase_commentLookupFailsCallsFailure() { /* Tests if failing to retrieve
                                                                        the artifact's comments calls
                                                                        the failure callback and
                                                                        stops deletion
                                                                        */
        writer.deleteFromDatabase("LOT100", callback);

        ArgumentCaptor<OnFailureListener> failureCaptor = ArgumentCaptor.forClass(OnFailureListener.class);

        verify(commentGetTask).addOnFailureListener(failureCaptor.capture());

        failureCaptor.getValue().onFailure(new Exception("Failed comment query"));

        verify(callback).onFailure("Failed comment query");

        verify(likesDir, never()).orderByChild(anyString());
        verify(artifactsDir, never()).child(anyString());
    }

    @Test
    public void deleteFromDatabase_likeLookupFailsCallsFailure() { /* Tests if failing to retrieve
                                                                     the artifact's likes calls the
                                                                     failure callback and stops
                                                                     deletion
                                                                     */
        configureRelatedCommentsAndLikes();

        writer.deleteFromDatabase("LOT100", callback);

        triggerCommentLookupSuccess();

        ArgumentCaptor<OnFailureListener> failureCaptor = ArgumentCaptor.forClass(OnFailureListener.class);

        verify(likeGetTask).addOnFailureListener(failureCaptor.capture());

        failureCaptor.getValue().onFailure(new Exception("Could not retrieve likes"));

        verify(callback).onFailure("Could not retrieve likes");

        verify(artifactsDir, never()).child(anyString());
        verify(callback, never()).onSuccess();
    }

    @Test
    public void deleteFromDatabase_bareArtifactDeletesArtifact() { /* Tests if an artifact with no
                                                                      comments or likes is still
                                                                      deleted successfully
                                                                      */
        when(commentSnapshot.getChildren()).thenReturn(Collections.emptyList());

        when(likeSnapshot.getChildren()).thenReturn(Collections.emptyList());

        writer.deleteFromDatabase("LOT100", callback);

        triggerCommentLookupSuccess();
        triggerLikeLookupSuccess();

        verify(lot).removeValue();

        assertTrue(capturedDeleteTasks.contains(artifactDeleteTask));

        triggerAllDeletesSuccess();

        verify(callback).onSuccess();
    }

    @Test
    public void deleteFromDatabase_artifactCallsLotDeletion() { /* Tests if deleting with an Artifact
                                                                   passes to the LOT-based delete
                                                                   method and deletes correctly
                                                                   */
        ArtifactDatabaseWriter writerSpy = spy(writer);

        doNothing().when(writerSpy).deleteFromDatabase("LOT100", callback);

        writerSpy.deleteFromDatabase(artifact, callback);

        verify(writerSpy).deleteFromDatabase("LOT100", callback);
    }

    @Test
    public void deleteFromDatabase_notAnArtifactDoesNotDelete() { /* Tests if attempting to delete a
                                                                     non-artifact object calls
                                                                     failure, and prevents deletion
                                                                     */
        writer.deleteFromDatabase(notAnArtifact, callback);

        verify(callback).onFailure("Item is not an artifact");
        verify(commentsDir, never()).orderByChild(anyString());
        verify(likesDir, never()).orderByChild(anyString());
    }

    private void configureRelatedCommentsAndLikes() {
        when(commentSnapshot.getChildren())
                .thenReturn(Arrays.asList(commentOne, commentTwo));

        when(commentOne.getRef())
                .thenReturn(commentOneRef);

        when(commentTwo.getRef())
                .thenReturn(commentTwoRef);

        when(commentOneRef.removeValue())
                .thenReturn(commentOneDeleteTask);

        when(commentTwoRef.removeValue())
                .thenReturn(commentTwoDeleteTask);

        when(likeSnapshot.getChildren())
                .thenReturn(Arrays.asList(likeOne, likeTwo));

        when(likeOne.getRef())
                .thenReturn(likeOneRef);

        when(likeTwo.getRef())
                .thenReturn(likeTwoRef);

        when(likeOneRef.removeValue())
                .thenReturn(likeOneDeleteTask);

        when(likeTwoRef.removeValue())
                .thenReturn(likeTwoDeleteTask);
    }


    private void triggerCommentLookupSuccess() {
        ArgumentCaptor<OnSuccessListener<DataSnapshot>> captor = ArgumentCaptor.forClass(OnSuccessListener.class);

        verify(commentGetTask).addOnSuccessListener(captor.capture());

        captor.getValue().onSuccess(commentSnapshot);
    }


    private void triggerLikeLookupSuccess() {
        ArgumentCaptor<OnSuccessListener<DataSnapshot>> captor = ArgumentCaptor.forClass(OnSuccessListener.class);

        verify(likeGetTask).addOnSuccessListener(captor.capture());

        captor.getValue().onSuccess(likeSnapshot);
    }


    private void triggerAllDeletesSuccess() {
        ArgumentCaptor<OnSuccessListener<Void>> captor = ArgumentCaptor.forClass(OnSuccessListener.class);

        verify(allDeleteTask).addOnSuccessListener(captor.capture());

        captor.getValue().onSuccess(null);
    }

}