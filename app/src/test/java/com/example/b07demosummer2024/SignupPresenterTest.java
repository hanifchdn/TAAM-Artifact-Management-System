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

@RunWith(MockitoJUnitRunner.class)
public class SignupPresenterTest {
    // Test fields
    private static final String EMAIL = "email@domain.com";
    private static final String PASSWORD = "Password1!";
    private static final String CONFIRM_PASSWORD = "Password1!";
    private static final String UID = "test_uid";
    private static final String ERROR_MESSAGE = "User already exist.";

    // Setting up the test environment
    @Mock
    private SignUpContract.View view;

    @Mock
    private SignUpContract.Model model;

    @Captor
    private ArgumentCaptor<SignUpContract.Model.Authcallback> callbackCaptor;

    private SignUpPresenter presenter;

    @Before
    public void setUp() {
        presenter = new SignUpPresenter(view, model);
    }

    @Test
    public void signUp_EmptyEmailShowsEmptyEmailError() { /* Tests if empty email error is shown
                                                             after given empty email
                                                             */
        presenter.signUp("", PASSWORD, CONFIRM_PASSWORD);

        verify(view).showEmptyEmailError();
        verifyNoInteractions(model);
    }

    @Test
    public void signUp_EmptyPasswordShowsEmptyPasswordError() { /* Tests if empty password error is
                                                                   shown after given empty password
                                                                   */
        presenter.signUp(EMAIL, "", CONFIRM_PASSWORD);

        verify(view).showEmptyPasswordError();
        verifyNoInteractions(model);
    }

    @Test
    public void signUp_NullEmailShowsEmptyEmailError() { /* Tests if null email is handled like
                                                            empty email
                                                            */
        presenter.signUp(null, PASSWORD, CONFIRM_PASSWORD);

        verify(view).showEmptyEmailError();
        verifyNoInteractions(model);
    }

    @Test
    public void signUp_NullPasswordShowsEmptyPasswordError() { /* Tests if null password is handled
                                                                  like empty password
                                                                  */
        presenter.signUp(EMAIL, null, CONFIRM_PASSWORD);

        verify(view).showEmptyPasswordError();
        verifyNoInteractions(model);
    }

    @Test
    public void signUp_InvalidEmailShowsInvalidEmailFormatError() { /* Tests if invalid email error
                                                                       is shown after given invalid
                                                                       email
                                                                       */
        presenter.signUp("invalid-email", PASSWORD, CONFIRM_PASSWORD);

        verify(view).showInvalidEmailFormatError();
        verifyNoInteractions(model);
    }

    @Test
    public void signUp_EmptyConfirmPasswordShowsEmptyConfirmPasswordError() { /* Tests if empty
                                                                                 confirm password
                                                                                 error is shown after
                                                                                 given empty password
                                                                                 */
        presenter.signUp(EMAIL, PASSWORD, "");

        verify(view).showEmptyConfirmPasswordError();
        verifyNoInteractions(model);
    }

    @Test
    public void signUp_NullConfirmPasswordShowsEmptyConfirmPasswordError() { /* Tests if empty
                                                                                confirm password
                                                                                error is shown after
                                                                                given empty password
                                                                                */
        presenter.signUp(EMAIL, PASSWORD, null);

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
        presenter.signUp(EMAIL, PASSWORD, "DifferentPassword1!");

        verify(view).showPasswordMismatchError();
        verifyNoInteractions(model);
    }

    @Test
    public void signUp_SignUpAttemptShowsLoading() { /* Tests if loading indicator is shown after an
                                                        attempted signup
                                                        */
        presenter.signUp(EMAIL, PASSWORD, CONFIRM_PASSWORD);

        verify(view).showLoadingIndicator();
        verify(view).disableButton();
    }

    @Test
    public void signUp_ValidCredentialsCallsModel() { /* Tests if model is called after given valid
                                                         signup credentials
                                                         */
        presenter.signUp(EMAIL, PASSWORD, CONFIRM_PASSWORD);

        verify(model).signUp(eq(EMAIL), eq(PASSWORD), callbackCaptor.capture());

        assertNotNull(callbackCaptor.getValue());
    }

    @Test
    public void signUp_ModelSuccessHandlesCorrectBehavior() { /* Tests if presenter handles a
                                                                 successful signup callback. Hides
                                                                 loading indicator, navigates user
                                                                 to the homepage, and no errors are
                                                                 displayed
                                                                 */
        presenter.signUp(EMAIL, PASSWORD, CONFIRM_PASSWORD);

        verify(model).signUp(eq(EMAIL), eq(PASSWORD), callbackCaptor.capture());

        callbackCaptor.getValue().onSuccess(UID);

        verify(view).hideLoadingIndicator();
        verify(view).enableButton();
        verify(view).navigateToHome();
        verify(view, never()).showSignUpFailedError(anyString());
    }

    @Test
    public void signUp_ModelFailureHandlesCorrectBehavior() { /* Tests if presenter handles a failed
                                                                 signup callback. Hides loading
                                                                 indicator, re-enables signup button,
                                                                 and displays error message. No
                                                                 navigation should happen
                                                                 */
        presenter.signUp(EMAIL, PASSWORD, CONFIRM_PASSWORD);

        verify(model).signUp(eq(EMAIL), eq(PASSWORD), callbackCaptor.capture());

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
        presenter.signUp(EMAIL, PASSWORD, CONFIRM_PASSWORD);

        verify(model).signUp(eq(EMAIL), eq(PASSWORD), callbackCaptor.capture());

        presenter.onDestroy();

        clearInvocations(view);

        callbackCaptor.getValue().onSuccess(UID);

        verifyNoInteractions(view);
    }

    @Test
    public void signUp_ModelFailureHandlesDestroyCorrectly() { /* Tests if presenter handles destroy
                                                                  correctly after a failed signup
                                                                  callback
                                                                  */
        presenter.signUp(EMAIL, PASSWORD, CONFIRM_PASSWORD);

        verify(model).signUp(eq(EMAIL), eq(PASSWORD), callbackCaptor.capture());

        presenter.onDestroy();

        clearInvocations(view);

        callbackCaptor.getValue().onFailure("User already exists.");

        verifyNoInteractions(view);
    }

    @Test
    public void signUp_AfterDestroyCannotInteract() { /* Tests if presenter correctly handles a
                                                         destroyed presenter. No actions should happen
                                                         */
        presenter.onDestroy();

        presenter.signUp(EMAIL, PASSWORD, CONFIRM_PASSWORD);

        verifyNoInteractions(view);
        verifyNoInteractions(model);
    }

}
