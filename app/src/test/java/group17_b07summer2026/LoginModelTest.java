package group17_b07summer2026;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

@RunWith(MockitoJUnitRunner.class)
public class LoginModelTest {
    // Test fields
    private static final String EMAIL = "email@domain.com";
    private static final String PASSWORD = "Password1!";

    // Setting up the test environment
    @Mock
    private FirebaseAuth auth;

    @Mock
    private Task<AuthResult> task;

    @Captor
    private ArgumentCaptor<OnCompleteListener<AuthResult>> listenerCaptor;

    private LoginModel model;
    private TestCallback callback;

    @Before
    public void setUp() {
        model = new LoginModel(auth);
        callback = new TestCallback();

        when(auth.signInWithEmailAndPassword(EMAIL, PASSWORD)).thenReturn(task);
    }

    @Test
    public void login_FirebaseReceivesGivenCredentials() { /* Tests if Firebase received the
                                                              credentials given
                                                              */
        model.login(EMAIL, PASSWORD, callback);

        verify(auth).signInWithEmailAndPassword(EMAIL, PASSWORD);
    }

    @Test
    public void login_FirebaseReturnsUidAfterSuccessfulLogin() { /* Tests if Firebase returns uid
                                                                    after successful login
                                                                    */
        when(task.isSuccessful()).thenReturn(true);
        when(auth.getUid()).thenReturn("test_uid");

        model.login(EMAIL, PASSWORD, callback);
        completeFirebaseTask();

        assertTrue(callback.success);
        assertFalse(callback.failure);
        assertEquals("test_uid", callback.uid);
    }

    @Test
    public void login_ReturnsInvalidCredentialsMessage() { /* Tests if invalid credentials message
                                                              is returned after given invalid
                                                              credentials
                                                              */
        FirebaseAuthInvalidCredentialsException exception = new FirebaseAuthInvalidCredentialsException(
                "ERROR_INVALID_CREDENTIAL",
                "Invalid credentials"
        );

        when(task.isSuccessful()).thenReturn(false);
        when(task.getException()).thenReturn(exception);

        model.login(EMAIL, PASSWORD, callback);
        completeFirebaseTask();

        assertFalse(callback.success);
        assertTrue(callback.failure);
        assertEquals("Invalid email or password.", callback.errorMessage);
    }

    @Test
    public void login_ReturnsBadNetworkMessage() { /* Tests if bad network message is returned after
                                                      given network failure
                                                      */
        FirebaseNetworkException exception = new FirebaseNetworkException("Network unavailable");

        when(task.isSuccessful()).thenReturn(false);
        when(task.getException()).thenReturn(exception);

        model.login(EMAIL, PASSWORD, callback);
        completeFirebaseTask();

        assertFalse(callback.success);
        assertTrue(callback.failure);
        assertEquals("No internet connection.", callback.errorMessage);
    }

    @Test
    public void login_ReturnsInvalidUserMessage() { /* Tests if invalid user message is returned
                                                       after given invalid user
                                                       */
        FirebaseAuthInvalidUserException exception = new FirebaseAuthInvalidUserException(
                "ERROR_USER_NOT_FOUND",
                "User does not exist"
        );

        when(task.isSuccessful()).thenReturn(false);
        when(task.getException()).thenReturn(exception);

        model.login(EMAIL, PASSWORD, callback);
        completeFirebaseTask();

        assertFalse(callback.success);
        assertTrue(callback.failure);
        assertEquals("Invalid email or password.", callback.errorMessage);
    }

    @Test
    public void login_ReturnsTooManyRequestsMessage() { /* Tests if too many requests message is
                                                           returned after given too many requests
                                                           */
        FirebaseTooManyRequestsException exception = new FirebaseTooManyRequestsException(
                "Too many login attempts"
        );

        when(task.isSuccessful()).thenReturn(false);
        when(task.getException()).thenReturn(exception);

        model.login(EMAIL, PASSWORD, callback);
        completeFirebaseTask();

        assertFalse(callback.success);
        assertTrue(callback.failure);
        assertEquals("Try again later.", callback.errorMessage);
    }

    @Test
    public void login_GenericExceptionReturnsGenericErrorMessage() { /* Tests if generic error message is returned
                                                                        after given generic exception
                                                                        */
        Exception exception = new Exception("Unexpected Firebase failure");

        when(task.isSuccessful()).thenReturn(false);
        when(task.getException()).thenReturn(exception);

        model.login(EMAIL, PASSWORD, callback);
        completeFirebaseTask();

        assertFalse(callback.success);
        assertTrue(callback.failure);
        assertEquals("Something went wrong.", callback.errorMessage);
    }

    @Test
    public void login_NullExceptionReturnsGenericErrorMessage() { /* Tests if generic error message is returned
                                                                     after given null exception
                                                                     */
        when(task.isSuccessful()).thenReturn(false);
        when(task.getException()).thenReturn(null);

        model.login(EMAIL, PASSWORD, callback);
        completeFirebaseTask();

        assertFalse(callback.success);
        assertTrue(callback.failure);
        assertEquals("Something went wrong.", callback.errorMessage);
    }

    private void completeFirebaseTask() {
        verify(task).addOnCompleteListener(listenerCaptor.capture());
        listenerCaptor.getValue().onComplete(task);
    }

    private static class TestCallback implements LoginContract.Model.AuthCallback {
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
