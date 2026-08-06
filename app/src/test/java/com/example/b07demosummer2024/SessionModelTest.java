package com.example.b07demosummer2024;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;

@RunWith(MockitoJUnitRunner.class)
public class SessionModelTest {

    private static final String UID = "test-uid";

    // Setting up the test environment
    @Mock
    private FirebaseAuth auth;

    @Mock
    private DatabaseReference userDir;

    @Mock
    private DatabaseReference userRef;

    @Mock
    private Task<DataSnapshot> task;

    @Mock
    private DataSnapshot snapshot;

    @Mock
    private User user;

    @Mock
    private SessionContract.Model.ProfileCallback callback;

    @Captor
    private ArgumentCaptor<OnCompleteListener<DataSnapshot>> listenerCaptor;

    private SessionModel model;

    @Before
    public void setUp() {
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.clear();

        model = new SessionModel(auth, userDir);
    }

    @Test
    public void currentUserUid_authenticatedUserReturnsUid() { /* Tests if UID of current user is
                                                                  returned
                                                                  */
        when(auth.getUid()).thenReturn(UID);

        String result = model.currentUserUid();

        assertEquals(UID, result);
    }

    @Test
    public void currentUserUid_noAuthenticatedUserReturnsNull() { /* Tests if null is returned if
                                                                     no user is currently
                                                                     authenticated
                                                                     */
        when(auth.getUid()).thenReturn(null);

        String result = model.currentUserUid();

        assertNull(result);
    }

    @Test
    public void fetchUserProfile_validProfileCallsOnProfileLoaded() { /* Tests if valid profile
                                                                         correctly loads the user and
                                                                         passes to the callback
                                                                         */
        configureProfileFetch();

        when(task.isSuccessful()).thenReturn(true);
        when(task.getResult()).thenReturn(snapshot);
        when(snapshot.getValue(User.class)).thenReturn(user);

        model.fetchUserProfile(UID, callback);
        completeProfileFetch();

        verify(userDir).child(UID);
        verify(userRef).get();
        verify(callback).onProfileLoaded(user);
        verify(callback, never()).onProfileError(anyString());
    }

    @Test
    public void fetchUserProfile_invalidProfileCallsOnProfileError() { /* Tests if an invalid user
                                                                          calls profile not found
                                                                          error
                                                                          */
        configureProfileFetch();

        when(task.isSuccessful()).thenReturn(true);
        when(task.getResult()).thenReturn(snapshot);
        when(snapshot.getValue(User.class)).thenReturn(null);

        model.fetchUserProfile(UID, callback);
        completeProfileFetch();

        verify(callback).onProfileError("Profile not found.");
        verify(callback, never()).onProfileLoaded(user);
    }

    @Test
    public void fetchUserProfile_checkError_networkFailureCallsOnProfileError() { /* Tests if bad
                                                                                     network calls
                                                                                     network failure
                                                                                     error
                                                                                     */
        configureProfileFetch();

        when(task.isSuccessful()).thenReturn(false);
        when(task.getException()).thenReturn(new FirebaseNetworkException("Network failure"));

        model.fetchUserProfile(UID, callback);
        completeProfileFetch();

        verify(callback).onProfileError("No internet connection.");
        verify(callback, never()).onProfileLoaded(user);
    }

    @Test
    public void fetchUserProfile_checkError_databaseFailureCallsOnProfileError() { /* Tests if failed
                                                                                      Firebase read
                                                                                      calls database
                                                                                      fail error
                                                                                      */
        configureProfileFetch();

        when(task.isSuccessful()).thenReturn(false);
        when(task.getException()) .thenReturn(new DatabaseException("Database failure"));

        model.fetchUserProfile(UID, callback);
        completeProfileFetch();

        verify(callback).onProfileError("Couldn't read your profile.");
        verify(callback, never()).onProfileLoaded(user);
    }

    @Test
    public void fetchUserProfile_checkError_genericExceptionCallsGenericError() { /* Tests if generic
                                                                                     exception calls
                                                                                     generic error
                                                                                     */
        configureProfileFetch();

        when(task.isSuccessful()).thenReturn(false);
        when(task.getException()).thenReturn(new RuntimeException("Generic Exception"));

        model.fetchUserProfile(UID, callback);
        completeProfileFetch();

        verify(callback).onProfileError("Something went wrong.");
        verify(callback, never()).onProfileLoaded(user);
    }

    @Test
    public void logOut_authenticatedUserSignsOutAndClearsSession() { /* Tests if logging out signs
                                                                        out of Firebase Auth and
                                                                        clears session
                                                                        */
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.setCurrentUser(user);

        model.logOut();

        verify(auth).signOut();
        assertNull(sessionManager.getCurrentUser());
    }

    @Test
    public void logOut_noUserSignsOutAndKeepsClearSession() { /* Tests if logging out with no user
                                                                 signs out of Firebase Auth and keeps
                                                                 a clear session
                                                                 */
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.clear();

        model.logOut();

        verify(auth).signOut();
        assertNull(sessionManager.getCurrentUser());
    }

    private void configureProfileFetch() {
        when(userDir.child(UID)).thenReturn(userRef);
        when(userRef.get()).thenReturn(task);
    }

    private void completeProfileFetch() {
        verify(task).addOnCompleteListener(listenerCaptor.capture());
        listenerCaptor.getValue().onComplete(task);
    }
}