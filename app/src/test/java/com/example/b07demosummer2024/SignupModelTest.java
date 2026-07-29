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
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

@RunWith(MockitoJUnitRunner.class)
public class SignupModelTest {
    // Test fields
    private static final String EMAIL = "email@domain.com";
    private static final String PASSWORD = "Password1!";
    private static final String UID = "test_uid";

    // Setting up the test environment
    @Mock
    private FirebaseAuth auth;

    @Mock
    private DatabaseReference usersDatabase;

    @Mock
    private DatabaseReference userDatabase;

    @Mock
    private FirebaseUser firebaseUser;

    @Mock
    private Task<AuthResult> authTask;

    @Mock
    private Task<Void> databaseTask;

    @Mock
    private Task<Void> deleteTask;

    @Captor
    private ArgumentCaptor<OnCompleteListener<AuthResult>>
            authListenerCaptor;

    @Captor
    private ArgumentCaptor<OnCompleteListener<Void>>
            databaseListenerCaptor;

    @Captor
    private ArgumentCaptor<OnCompleteListener<Void>>
            deleteListenerCaptor;

    private SignupModel model;
    private TestCallback callback;

    @Before
    public void setUp() {
        model = new SignupModel(auth, usersDatabase);
        callback = new TestCallback();

        when(auth.createUserWithEmailAndPassword(EMAIL, PASSWORD)).thenReturn(authTask);
    }

    @Test
    public void signUp_FirebaseReceivesGivenCredentials() { /* Tests if Firebase Authentication
                                                               receives email and password given
                                                               */
        model.signUp(EMAIL, PASSWORD, callback);

        verify(auth).createUserWithEmailAndPassword(EMAIL, PASSWORD);
    }

    @Test
    public void signUp_SuccessfulAuthenticationWritesUserToDatabase() { /* Tests if Firebase
                                                                           Authentication
                                                                           successfully creates
                                                                           account
                                                                           */
        prepareSuccessfulAuthentication();

        model.signUp(EMAIL, PASSWORD, callback);
        completeAuthenticationTask();

        verify(usersDatabase).child(UID);
        verify(userDatabase).setValue(any(User.class));
    }

    @Test
    public void signUp_SuccessfulDatabaseWriteReturnsUid() { /* Tests if Firebase returns UID after
                                                                successful write to both Firebase and
                                                                Firebase Authentication
                                                                */
        prepareSuccessfulAuthentication();
        when(databaseTask.isSuccessful()).thenReturn(true);

        model.signUp(EMAIL, PASSWORD, callback);
        completeAuthenticationTask();
        completeDatabaseTask();

        assertTrue(callback.success);
        assertFalse(callback.failure);
        assertEquals(UID, callback.uid);
        assertNull(callback.errorMessage);
    }

    @Test
    public void signUp_DatabaseFailureRollbackAuthentication() { /* Tests if Firebase write fails,
                                                                    then Firebase Authentication
                                                                    attempts a rollback, and displays
                                                                    the corresponding error message
                                                                    */
        prepareSuccessfulAuthentication();
        prepareFailedDatabaseWrite(new Exception("Database write failed"));

        when(auth.getCurrentUser()).thenReturn(firebaseUser);
        when(firebaseUser.delete()).thenReturn(deleteTask);
        when(deleteTask.isSuccessful()).thenReturn(true);

        model.signUp(EMAIL, PASSWORD, callback);
        completeAuthenticationTask();
        completeDatabaseTask();

        verify(firebaseUser).delete();

        completeDeleteTask();

        assertFalse(callback.success);
        assertTrue(callback.failure);
        assertNull(callback.uid);
        assertEquals("Could not complete signup.", callback.errorMessage);
    }

    @Test
    public void signUp_FailedRollbackReturnsGenericMessage() { /* Tests if both Firebase write fails,
                                                                  and Firebase Authentication
                                                                  rollback fails, then a generic
                                                                  error message is displayed

                                                                  Note: If this ever happens, then
                                                                  the Firebase would require manual
                                                                  cleanup
                                                                  */
        prepareSuccessfulAuthentication();
        prepareFailedDatabaseWrite(new Exception("Database write failed"));

        when(auth.getCurrentUser()).thenReturn(firebaseUser);
        when(firebaseUser.delete()).thenReturn(deleteTask);
        when(deleteTask.isSuccessful()).thenReturn(false);
        when(deleteTask.getException()).thenReturn(new Exception("Account deletion failed"));

        model.signUp(EMAIL, PASSWORD, callback);
        completeAuthenticationTask();
        completeDatabaseTask();
        completeDeleteTask();

        assertFalse(callback.success);
        assertTrue(callback.failure);
        assertNull(callback.uid);
        assertEquals("Something went wrong.", callback.errorMessage);
    }

    @Test
    public void signUp_UserCollisionReturnsUserExistsMessage() { /* Tests if attempting to create
                                                                    an account with an existing email
                                                                    returns user exists message
                                                                    */
        FirebaseAuthUserCollisionException exception = new FirebaseAuthUserCollisionException(
                "ERROR_EMAIL_ALREADY_IN_USE",
                "Email already registered"
        );

        prepareFailedAuthentication(exception);

        model.signUp(EMAIL, PASSWORD, callback);
        completeAuthenticationTask();

        assertAuthenticationFailure("User already exists.");
    }

    @Test
    public void signUp_WeakPasswordReturnsWeakPasswordMessage() { /* Tests if attempting to create an
                                                                     account with a weak password
                                                                     returns weak password message
                                                                     */
        FirebaseAuthWeakPasswordException exception = new FirebaseAuthWeakPasswordException(
                "ERROR_WEAK_PASSWORD",
                "Password is too weak",
                "Password must contain at least six characters"
        );

        prepareFailedAuthentication(exception);

        model.signUp(EMAIL, PASSWORD, callback);
        completeAuthenticationTask();

        assertAuthenticationFailure("Password is too weak.");
    }

    @Test
    public void signUp_InvalidEmailReturnsInvalidEmailMessage() { /* Tests if attempting to create an
                                                                     account with an invalid email
                                                                     returns invalid email message
                                                                     */
        FirebaseAuthInvalidCredentialsException exception = new FirebaseAuthInvalidCredentialsException(
                "ERROR_INVALID_EMAIL",
                "Invalid email"
        );

        prepareFailedAuthentication(exception);

        model.signUp(EMAIL, PASSWORD, callback);
        completeAuthenticationTask();

        assertAuthenticationFailure("Invalid email.");
    }

    @Test
    public void signUp_NetworkFailureReturnsNoInternetMessage() { /* Tests if bad network message is
                                                                     returned after given network
                                                                     failure
                                                                     */
        FirebaseNetworkException exception = new FirebaseNetworkException("Network unavailable");

        prepareFailedAuthentication(exception);

        model.signUp(EMAIL, PASSWORD, callback);
        completeAuthenticationTask();

        assertAuthenticationFailure("No internet connection.");
    }

    @Test
    public void signUp_TooManyRequestsReturnsTryAgainLaterMessage() { /* Tests if too many requests
                                                                         message is returned after
                                                                         given too many requests
                                                                         failure
                                                                         */
        FirebaseTooManyRequestsException exception = new FirebaseTooManyRequestsException(
                "Too many account creation attempts"
        );

        prepareFailedAuthentication(exception);

        model.signUp(EMAIL, PASSWORD, callback);
        completeAuthenticationTask();

        assertAuthenticationFailure("Try again later.");
    }

    @Test
    public void signUp_GenericAuthenticationFailureReturnsGenericMessage() { /* Tests if generic
                                                                                error message is
                                                                                returned after given
                                                                                generic exception
                                                                                */
        Exception exception = new Exception("Unexpected authentication failure");

        prepareFailedAuthentication(exception);

        model.signUp(EMAIL, PASSWORD, callback);
        completeAuthenticationTask();

        assertAuthenticationFailure("Something went wrong.");
    }

    @Test
    public void signUp_NullAuthenticationExceptionReturnsGenericMessage() { /* Tests if generic error
                                                                               message is returned
                                                                               after given null
                                                                               exception
                                                                               */
        prepareFailedAuthentication(null);

        model.signUp(EMAIL, PASSWORD, callback);
        completeAuthenticationTask();

        assertAuthenticationFailure("Something went wrong.");
    }

    private void prepareSuccessfulAuthentication() {
        when(authTask.isSuccessful()).thenReturn(true);
        when(auth.getUid()).thenReturn(UID);
        when(usersDatabase.child(UID)).thenReturn(userDatabase);
        when(userDatabase.setValue(any(User.class))).thenReturn(databaseTask);
    }

    private void prepareFailedAuthentication(Exception exception) {
        when(authTask.isSuccessful()).thenReturn(false);
        when(authTask.getException()).thenReturn(exception);
    }

    private void prepareFailedDatabaseWrite(Exception exception) {
        when(databaseTask.isSuccessful()).thenReturn(false);
        when(databaseTask.getException()).thenReturn(exception);
    }

    private void assertAuthenticationFailure(String expectedMessage) {
        assertFalse(callback.success);
        assertTrue(callback.failure);
        assertNull(callback.uid);
        assertEquals(expectedMessage, callback.errorMessage);

        verifyNoInteractions(usersDatabase);
    }

    private void completeAuthenticationTask() {
        verify(authTask).addOnCompleteListener(
                authListenerCaptor.capture()
        );

        authListenerCaptor.getValue().onComplete(authTask);
    }

    private void completeDatabaseTask() {
        verify(databaseTask).addOnCompleteListener(
                databaseListenerCaptor.capture()
        );

        databaseListenerCaptor.getValue().onComplete(databaseTask);
    }

    private void completeDeleteTask() {
        verify(deleteTask).addOnCompleteListener(
                deleteListenerCaptor.capture()
        );

        deleteListenerCaptor.getValue().onComplete(deleteTask);
    }

    private static class TestCallback implements SignUpContract.Model.Authcallback {

        boolean success;
        boolean failure;
        String uid;
        String errorMessage;

        @Override
        public void onSuccess(String uid) {
            success = true;
            this.uid = uid;
        }

        @Override
        public void onFailure(String message) {
            failure = true;
            errorMessage = message;
        }
    }
}
