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

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {
    // Test fields
    private static final String EMAIL = "email@domain.com";
    private static final String PASSWORD = "Password1!";
    private static final String UID = "test-uid";

    // Setting up the test environment
    @Mock
    private LoginContract.View view;

    @Mock
    private LoginContract.Model model;

    @Mock
    private SessionContract.Model sessionModel;

    @Mock
    private SessionManager session;

    @Mock
    private User user;

    @Captor
    private ArgumentCaptor<SessionContract.Model.ProfileCallback>
            profileCallbackCaptor;

    @Captor
    private ArgumentCaptor<LoginContract.Model.AuthCallback> callbackCaptor;

    private LoginPresenter presenter;

    @Before
    public void setUp() {
        presenter = new LoginPresenter(view, model, sessionModel, session);
    }

    @Test
    public void login_EmptyEmailShowsEmptyEmailError() { /* Tests if empty email error is shown after
                                                            given an empty email
                                                            */
        presenter.login("", PASSWORD);

        verify(view).showEmptyEmailError();
        verify(model, never()).login(anyString(), anyString(), any(LoginContract.Model.AuthCallback.class));
    }

    @Test
    public void login_EmptyPasswordShowsEmptyPasswordError() { /* Tests if empty password error is
                                                                  shown after given an empty password
                                                                  */
        presenter.login(EMAIL, "");

        verify(view).showEmptyPasswordError();
        verify(model, never()).login(anyString(), anyString(), any(LoginContract.Model.AuthCallback.class));
    }

    @Test
    public void login_NullEmailShowsEmptyEmailError() { /* Tests if null email is handled like empty
                                                           email
                                                           */
        presenter.login(null, PASSWORD);

        verify(view).showEmptyEmailError();
        verifyNoInteractions(model);
    }

    @Test
    public void login_NullPasswordShowsEmptyPasswordError() { /* Tests if null password is handled
                                                                 like empty email
                                                                 */
        presenter.login(EMAIL, null);

        verify(view).showEmptyPasswordError();
        verifyNoInteractions(model);
    }

    @Test
    public void login_InvalidEmailShowsInvalidEmailFormatError() { /* Tests if invalid email error is
                                                                      shown after given an invalid
                                                                      email
                                                                       */
        presenter.login("invalid-email", PASSWORD);

        verify(view).showInvalidEmailFormatError();
        verify(model, never()).login(anyString(), anyString(), any(LoginContract.Model.AuthCallback.class));
    }

    @Test
    public void login_LoginAttemptShowsLoading() { /* Tests if loading indicator is shown after an
                                                      attempted login
                                                      */
        presenter.login(EMAIL, PASSWORD);

        verify(view).showLoadingIndicator();
        verify(view).disableButton();
    }

    @Test
    public void login_ValidCredentialsCallsModel() { /* Tests if model is called after given valid
                                                        credentials
                                                        */
        presenter.login(EMAIL, PASSWORD);

        verify(model).login(eq(EMAIL), eq(PASSWORD), callbackCaptor.capture());

        assertNotNull(callbackCaptor.getValue());
    }

    @Test
    public void login_ModelSuccessHandlesCorrectBehavior() { /* Tests if model handles a successful
                                                                auth callback. Hides loading
                                                                indicator, navigates user to the
                                                                homepage, and no errors are displayed
                                                                */
        presenter.login(EMAIL, PASSWORD);

        verify(model).login(eq(EMAIL), eq(PASSWORD), callbackCaptor.capture());

        callbackCaptor.getValue().onSuccess(UID);

        verify(sessionModel).fetchUserProfile(eq(UID), profileCallbackCaptor.capture());

        profileCallbackCaptor.getValue().onProfileLoaded(user);

        verify(view).hideLoadingIndicator();
        verify(view).enableButton();
        verify(view).navigateToHome();
        verify(view, never()).showLoginError(anyString());
    }

    @Test
    public void login_ModelFailureHandlesCorrectBehavior() { /* Tests if model handles a failed auth
                                                                callback. Hides loading indicator,
                                                                re-enables login button, and displays
                                                                error message. No navigation should
                                                                happen
                                                                */
        presenter.login(EMAIL, PASSWORD);

        verify(model).login(eq(EMAIL), eq(PASSWORD), callbackCaptor.capture());

        callbackCaptor.getValue().onFailure("Invalid email or password.");

        verify(view).hideLoadingIndicator();
        verify(view).enableButton();
        verify(view).showLoginError("Invalid email or password.");
        verify(view, never()).navigateToHome();
    }

    @Test
    public void login_ModelSuccessHandlesDestroyCorrectly() { /* Tests if model handles destroy
                                                                 correctly after a successful auth
                                                                 callback
                                                                 */
        presenter.login(EMAIL, PASSWORD);

        verify(model).login(eq(EMAIL), eq(PASSWORD), callbackCaptor.capture());

        presenter.onDestroy();

        clearInvocations(view);

        callbackCaptor.getValue().onSuccess(UID);

        verifyNoInteractions(view);
    }

    @Test
    public void login_ModelFailureHandlesDestroyCorrectly() { /* Tests if model handles destroy
                                                                 correctly after a failed auth
                                                                 callback
                                                                 */
        presenter.login(EMAIL, PASSWORD);

        verify(model).login(eq(EMAIL), eq(PASSWORD), callbackCaptor.capture());

        presenter.onDestroy();

        clearInvocations(view);

        callbackCaptor.getValue().onFailure("Invalid email or password.");

        verifyNoInteractions(view);
    }

    @Test
    public void login_AfterDestroyCannotInteract() { /* Tests if presenter correctly handles
                                                        destroyed presenter (no actions should pass)
                                                        */
        presenter.onDestroy();

        presenter.login(EMAIL, PASSWORD);

        verifyNoInteractions(view);
        verifyNoInteractions(model);
    }
}
