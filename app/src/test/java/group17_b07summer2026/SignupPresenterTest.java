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
public class SignupPresenterTest {
    // Test fields
    private static final String USERNAME = "test_username";
    private static final String EMAIL = "email@domain.com";
    private static final String PASSWORD = "Password1!";
    private static final String CONFIRM_PASSWORD = "Password1!";
    private static final String UID = "test_uid";

    // Setting up the test environment
    @Mock
    private SignUpContract.View view;

    @Mock
    private SignUpContract.Model model;

    @Mock
    private SessionContract.Model sessionModel;

    @Mock
    private User user;

    @Captor
    private ArgumentCaptor<SignUpContract.Model.Authcallback> callbackCaptor;

    @Captor
    private ArgumentCaptor<SessionContract.Model.ProfileCallback> profileCallbackCaptor;

    private SignUpPresenter presenter;

    @Before
    public void setUp() {
        presenter = new SignUpPresenter(view, model, sessionModel);
    }

    @Test
    public void signUp_EmptyUsernameShowsEmptyUsernameError() { /* Tests if empty username error is
                                                                   shown after given empty username
                                                                   */
        presenter.signUp("", EMAIL, PASSWORD, CONFIRM_PASSWORD);

        verify(view).showEmptyUsernameError();
        verifyNoInteractions(model);
    }

    @Test
    public void signUp_NullUsernameShowsEmptyUsernameError() { /* Tests if null username is handled
                                                                  like empty username
                                                                  */
        presenter.signUp(null, EMAIL, PASSWORD, CONFIRM_PASSWORD);

        verify(view).showEmptyUsernameError();
        verifyNoInteractions(model);
    }

    @Test
    public void signUp_EmptyEmailShowsEmptyEmailError() { /* Tests if empty email error is shown
                                                             after given empty email
                                                             */
        presenter.signUp(USERNAME, "", PASSWORD, CONFIRM_PASSWORD);

        verify(view).showEmptyEmailError();
        verifyNoInteractions(model);
    }

    @Test
    public void signUp_EmptyPasswordShowsEmptyPasswordError() { /* Tests if empty password error is
                                                                   shown after given empty password
                                                                   */
        presenter.signUp(USERNAME, EMAIL, "", CONFIRM_PASSWORD);

        verify(view).showEmptyPasswordError();
        verifyNoInteractions(model);
    }

    @Test
    public void signUp_NullEmailShowsEmptyEmailError() { /* Tests if null email is handled like
                                                            empty email
                                                            */
        presenter.signUp(USERNAME, null, PASSWORD, CONFIRM_PASSWORD);

        verify(view).showEmptyEmailError();
        verifyNoInteractions(model);
    }

    @Test
    public void signUp_NullPasswordShowsEmptyPasswordError() { /* Tests if null password is handled
                                                                  like empty password
                                                                  */
        presenter.signUp(USERNAME, EMAIL, null, CONFIRM_PASSWORD);

        verify(view).showEmptyPasswordError();
        verifyNoInteractions(model);
    }

    @Test
    public void signUp_InvalidEmailShowsInvalidEmailFormatError() { /* Tests if invalid email error
                                                                       is shown after given invalid
                                                                       email
                                                                       */
        presenter.signUp(USERNAME, "invalid-email", PASSWORD, CONFIRM_PASSWORD);

        verify(view).showInvalidEmailFormatError();
        verifyNoInteractions(model);
    }

    @Test
    public void signUp_EmptyConfirmPasswordShowsEmptyConfirmPasswordError() { /* Tests if empty
                                                                                 confirm password
                                                                                 error is shown after
                                                                                 given empty password
                                                                                 */
        presenter.signUp(USERNAME, EMAIL, PASSWORD, "");

        verify(view).showEmptyConfirmPasswordError();
        verifyNoInteractions(model);
    }

    @Test
    public void signUp_NullConfirmPasswordShowsEmptyConfirmPasswordError() { /* Tests if empty
                                                                                confirm password
                                                                                error is shown after
                                                                                given empty password
                                                                                */
        presenter.signUp(USERNAME, EMAIL, PASSWORD, null);

        verify(view).showEmptyConfirmPasswordError();
        verifyNoInteractions(model);
    }

    @Test
    public void signUp_MismatchedPasswordsShowsPasswordMismatchError() { /* Tests if mismatched
                                                                            passwords error is shown
                                                                            after given mismatched
                                                                            password and confirm
                                                                            password
                                                                            */
        presenter.signUp(USERNAME, EMAIL, PASSWORD, "DifferentPassword1!");

        verify(view).showPasswordMismatchError();
        verifyNoInteractions(model);
    }

    @Test
    public void signUp_SignUpAttemptShowsLoading() { /* Tests if loading indicator is shown after an
                                                        attempted signup
                                                        */
        presenter.signUp(USERNAME, EMAIL, PASSWORD, CONFIRM_PASSWORD);

        verify(view).showLoadingIndicator();
        verify(view).disableButton();
    }

    @Test
    public void signUp_ValidCredentialsCallsModel() { /* Tests if model is called after given valid
                                                         signup credentials
                                                         */
        presenter.signUp(USERNAME, EMAIL, PASSWORD, CONFIRM_PASSWORD);

        verify(model).signUp(eq(USERNAME), eq(EMAIL), eq(PASSWORD), callbackCaptor.capture());

        assertNotNull(callbackCaptor.getValue());
    }

    @Test
    public void signUp_ModelSuccessHandlesCorrectBehavior() { /* Tests if presenter handles a
                                                                 successful signup callback. Hides
                                                                 loading indicator, navigates user
                                                                 to the homepage, and no errors are
                                                                 displayed
                                                                 */
        presenter.signUp(USERNAME, EMAIL, PASSWORD, CONFIRM_PASSWORD);

        verify(model).signUp(eq(USERNAME), eq(EMAIL), eq(PASSWORD), callbackCaptor.capture());

        callbackCaptor.getValue().onSuccess(UID);

        verify(sessionModel).fetchUserProfile(eq(UID), profileCallbackCaptor.capture());

        profileCallbackCaptor.getValue().onProfileLoaded(user);

        verify(view).hideLoadingIndicator();
        verify(view).enableButton();
        verify(view).navigateToHome();
        verify(view, never()).showSignUpFailedError(anyString());

        assertSame(user, SessionManager.getInstance().getCurrentUser());
    }

    @Test
    public void signUp_ModelFailureHandlesCorrectBehavior() { /* Tests if presenter handles a failed
                                                                 signup callback. Hides loading
                                                                 indicator, re-enables signup button,
                                                                 and displays error message. No
                                                                 navigation should happen
                                                                 */
        presenter.signUp(USERNAME, EMAIL, PASSWORD, CONFIRM_PASSWORD);

        verify(model).signUp(eq(USERNAME), eq(EMAIL), eq(PASSWORD), callbackCaptor.capture());

        callbackCaptor.getValue().onFailure("User already exists.");

        verify(view).hideLoadingIndicator();
        verify(view).enableButton();
        verify(view).showSignUpFailedError("User already exists.");
        verify(view, never()).navigateToHome();
    }

    @Test
    public void signUp_ModelSuccessHandlesDestroyCorrectly() { /* Tests if presenter handles destroy
                                                                  correctly after a successful signup
                                                                  callback
                                                                  */
        presenter.signUp(USERNAME, EMAIL, PASSWORD, CONFIRM_PASSWORD);

        verify(model).signUp(eq(USERNAME), eq(EMAIL), eq(PASSWORD), callbackCaptor.capture());

        presenter.onDestroy();

        clearInvocations(view);

        callbackCaptor.getValue().onSuccess(UID);

        verifyNoInteractions(view);
        verifyNoInteractions(sessionModel);
    }

    @Test
    public void signUp_ProfileSuccessHandlesDestroyCorrectly() { /* Tests if presenter handles
                                                                    destroy correctly after a
                                                                    successful signup, but before
                                                                    profile load
                                                                    */
        presenter.signUp(USERNAME, EMAIL, PASSWORD, CONFIRM_PASSWORD);

        verify(model).signUp(eq(USERNAME), eq(EMAIL), eq(PASSWORD), callbackCaptor.capture());

        callbackCaptor.getValue().onSuccess(UID);

        verify(sessionModel).fetchUserProfile(eq(UID), profileCallbackCaptor.capture());

        presenter.onDestroy();

        clearInvocations(view);

        profileCallbackCaptor.getValue().onProfileLoaded(user);

        verifyNoInteractions(view);
    }

    @Test
    public void signUp_ModelFailureHandlesDestroyCorrectly() { /* Tests if presenter handles destroy
                                                                  correctly after a failed signup
                                                                  callback
                                                                  */
        presenter.signUp(USERNAME, EMAIL, PASSWORD, CONFIRM_PASSWORD);

        verify(model).signUp(eq(USERNAME), eq(EMAIL), eq(PASSWORD), callbackCaptor.capture());

        presenter.onDestroy();

        clearInvocations(view);

        callbackCaptor.getValue().onFailure("User already exists.");

        verifyNoInteractions(view);
        verifyNoInteractions(sessionModel);
    }

    @Test
    public void signUp_AfterDestroyCannotInteract() { /* Tests if presenter correctly handles a
                                                         destroyed presenter. No actions should happen
                                                         */
        presenter.onDestroy();

        presenter.signUp(USERNAME, EMAIL, PASSWORD, CONFIRM_PASSWORD);

        verifyNoInteractions(view);
        verifyNoInteractions(model);
        verifyNoInteractions(sessionModel);
    }

}