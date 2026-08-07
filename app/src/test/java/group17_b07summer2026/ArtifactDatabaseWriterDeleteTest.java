package group17_b07summer2026;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import android.content.Context;

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

    private static final String IMAGE_URL = "https://example.supabase.co/storage/v1/object/public/artifacts/LOT100.jpg";

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
    private DatabaseReference likeLot;

    @Mock
    private Query commentsOrderedByArtifactLot;

    @Mock
    private Query matchingComments;

    @Mock
    private Task<DataSnapshot> commentGetTask;

    @Mock
    private DataSnapshot commentSnapshot;

    @Mock
    private DataSnapshot commentOne;

    @Mock
    private DataSnapshot commentTwo;

    @Mock
    private DatabaseReference commentOneRef;

    @Mock
    private DatabaseReference commentTwoRef;

    @Mock
    private Task<Void> commentOneDeleteTask;

    @Mock
    private Task<Void> commentTwoDeleteTask;

    @Mock
    private Task<Void> likeDeleteTask;

    @Mock
    private Task<Void> artifactDeleteTask;

    @Mock
    private Task<Void> allDeleteTask;

    @Mock
    private WriteCallback callback;

    @Mock
    private DatabaseItem notAnArtifact;

    @Mock
    private Context context;

    @Mock
    private SupabaseImageUploader imageUploader;

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

        writer = new ArtifactDatabaseWriter(context, imageUploader);

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
        mockedTasks.close();
        mockedFirebaseDatabase.close();
    }

    @Test
    public void deleteFromDatabase_validLotQueriesCommentsCorrectly() { /* Tests if comments are
                                                                       queried correctly
                                                                       */
        configureArtifactDeletion();

        writer.deleteFromDatabase("LOT100", callback);

        verify(commentsDir).orderByChild("artifactLot");
        verify(commentsOrderedByArtifactLot).equalTo("LOT100");
    }

    @Test
    public void deleteFromDatabase_validLotDeletesLikesCorrectly() { /* Tests if the artifact's likes
                                                                    are deleted correctly
                                                                    */
        configureArtifactDeletion();

        writer.deleteFromDatabase("LOT100", callback);

        triggerCommentLookupSuccess();

        verify(likesDir).child("LOT100");
        verify(likeLot).removeValue();
    }

    @Test
    public void deleteFromDatabase_validLotDeletesAllRelatedData() { /* Tests if all comments, likes,
                                                                       and the artifact itself are
                                                                       correctly deleted
                                                                       */
        configureArtifactDeletion();
        configureRelatedComments();

        writer.deleteFromDatabase("LOT100", callback);

        triggerCommentLookupSuccess();

        verify(commentOneRef).removeValue();
        verify(commentTwoRef).removeValue();

        verify(likesDir).child("LOT100");
        verify(likeLot).removeValue();

        verify(artifactsDir).child("LOT100");
        verify(lot).removeValue();

        assertTrue(capturedDeleteTasks.contains(commentOneDeleteTask));
        assertTrue(capturedDeleteTasks.contains(commentTwoDeleteTask));
        assertTrue(capturedDeleteTasks.contains(likeDeleteTask));
        assertTrue(capturedDeleteTasks.contains(artifactDeleteTask));
    }

    @Test
    public void deleteFromDatabase_allDeletesSucceedCallsSuccess() { /* Tests if successfully deleting
                                                                        all comments, likes, and the
                                                                        artifact calls the success
                                                                        callback
                                                                        */
        configureArtifactDeletion();
        configureRelatedComments();

        writer.deleteFromDatabase("LOT100", callback);

        triggerCommentLookupSuccess();
        triggerAllDeletesSuccess();

        verify(callback).onSuccess();
        verify(callback, never()).onFailure(anyString());
    }

    @Test
    public void deleteFromDatabase_deleteFailsCallsFailure() { /* Tests if a failure while deleting
                                                                  any related data calls the failure
                                                                  callback
                                                                  */
        configureArtifactDeletion();
        configureRelatedComments();

        writer.deleteFromDatabase("LOT100", callback);

        triggerCommentLookupSuccess();

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
        configureCommentLookup();

        writer.deleteFromDatabase("LOT100", callback);

        ArgumentCaptor<OnFailureListener> failureCaptor = ArgumentCaptor.forClass(OnFailureListener.class);

        verify(commentGetTask).addOnFailureListener(failureCaptor.capture());

        failureCaptor.getValue().onFailure(new Exception("Failed comment query"));

        verify(callback).onFailure("Failed comment query");

        verify(likesDir, never()).child(anyString());
        verify(artifactsDir, never()).child(anyString());
        verify(callback, never()).onSuccess();
    }

    @Test
    public void deleteFromDatabase_bareArtifactDeletesArtifactAndLikePath() { /* Tests if an
                                                                                 artifact with no
                                                                                 comments or likes is
                                                                                 still deleted
                                                                                 successfully
                                                                                 */
        configureArtifactDeletion();

        writer.deleteFromDatabase("LOT100", callback);

        triggerCommentLookupSuccess();

        verify(likeLot).removeValue();
        verify(lot).removeValue();

        assertTrue(capturedDeleteTasks.contains(likeDeleteTask));
        assertTrue(capturedDeleteTasks.contains(artifactDeleteTask));

        triggerAllDeletesSuccess();

        verify(callback).onSuccess();
    }

    @Test
    public void deleteFromDatabase_artifactWithoutImageCallsLotDeletion() { /* Tests if an artifact
                                                                               with no image is still
                                                                               deleted successfully
                                                                               */
        ArtifactDatabaseWriter writerSpy = spy(writer);

        doNothing().when(writerSpy).deleteFromDatabase("LOT100", callback);

        writerSpy.deleteFromDatabase(artifact, callback);

        verify(writerSpy).deleteFromDatabase("LOT100", callback);
        verifyNoInteractions(imageUploader);
    }

    @Test
    public void deleteFromDatabase_artifactWithImageDeletesImage() { /* Tests if an artifact with an
                                                                        image is deleted correctly
                                                                        */
        artifact.setImageUrl(IMAGE_URL);

        ArtifactDatabaseWriter writerSpy = spy(writer);
        doNothing().when(writerSpy).deleteFromDatabase("LOT100", callback);

        ArgumentCaptor<SupabaseImageUploader.DeleteCallback> deleteCallbackCaptor = ArgumentCaptor.forClass(SupabaseImageUploader.DeleteCallback.class);

        writerSpy.deleteFromDatabase(artifact, callback);

        verify(imageUploader).deleteImage(eq(IMAGE_URL), deleteCallbackCaptor.capture());
        verify(writerSpy, never()).deleteFromDatabase("LOT100", callback);

        deleteCallbackCaptor.getValue().onSuccess();

        verify(writerSpy).deleteFromDatabase("LOT100", callback);
    }

    @Test
    public void deleteFromDatabase_imageDeleteFailureCallsFailure() { /* Tests if failing to delete
                                                                         the image calls the failure
                                                                         callback and stops deletion
                                                                         */
        artifact.setImageUrl(IMAGE_URL);

        ArgumentCaptor<SupabaseImageUploader.DeleteCallback> deleteCallbackCaptor = ArgumentCaptor.forClass(SupabaseImageUploader.DeleteCallback.class);

        writer.deleteFromDatabase(artifact, callback);

        verify(imageUploader).deleteImage(eq(IMAGE_URL), deleteCallbackCaptor.capture());

        deleteCallbackCaptor.getValue().onError("Image deletion failed");

        verify(callback).onFailure("Image deletion failed");
        verify(callback, never()).onSuccess();

        verify(commentsDir, never()).orderByChild(anyString());
        verify(likesDir, never()).child(anyString());
        verify(artifactsDir, never()).child(anyString());
    }

    @Test
    public void deleteFromDatabase_notAnArtifactDoesNotDelete() { /* Tests if attempting to delete a
                                                                     non-artifact object calls
                                                                     failure, and prevents deletion
                                                                     */
        writer.deleteFromDatabase(notAnArtifact, callback);

        verify(callback).onFailure("Item is not an artifact");
        verifyNoInteractions(imageUploader);
        verify(commentsDir, never()).orderByChild(anyString());
        verify(likesDir, never()).child(anyString());
        verify(artifactsDir, never()).child(anyString());
    }

    private void configureCommentLookup() {
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
    }

    private void configureArtifactDeletion() {
        configureCommentLookup();

        when(commentSnapshot.getChildren())
                .thenReturn(Collections.emptyList());

        when(likesDir.child("LOT100"))
                .thenReturn(likeLot);

        when(likeLot.removeValue())
                .thenReturn(likeDeleteTask);

        when(artifactsDir.child("LOT100"))
                .thenReturn(lot);

        when(lot.removeValue())
                .thenReturn(artifactDeleteTask);

        when(allDeleteTask.addOnSuccessListener(any(OnSuccessListener.class)))
                .thenReturn(allDeleteTask);

        when(allDeleteTask.addOnFailureListener(any(OnFailureListener.class)))
                .thenReturn(allDeleteTask);

        mockedTasks.when(() -> Tasks.whenAll(anyCollection()))
                .thenAnswer(invocation -> {
                    Collection<? extends Task<?>> tasks = invocation.getArgument(0);

                    capturedDeleteTasks = new ArrayList<>(tasks);

                    return allDeleteTask;
                });
    }

    private void configureRelatedComments() {
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
    }

    private void triggerCommentLookupSuccess() {
        ArgumentCaptor<OnSuccessListener<DataSnapshot>> captor = ArgumentCaptor.forClass(OnSuccessListener.class);

        verify(commentGetTask).addOnSuccessListener(captor.capture());

        captor.getValue().onSuccess(commentSnapshot);
    }

    private void triggerAllDeletesSuccess() {
        ArgumentCaptor<OnSuccessListener<Void>> captor = ArgumentCaptor.forClass(OnSuccessListener.class);

        verify(allDeleteTask).addOnSuccessListener(captor.capture());

        captor.getValue().onSuccess(null);
    }
}