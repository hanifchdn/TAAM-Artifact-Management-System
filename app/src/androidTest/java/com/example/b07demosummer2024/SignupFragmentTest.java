package com.example.b07demosummer2024;

import static androidx.fragment.app.testing.FragmentScenario.launchInContainer;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

import android.widget.EditText;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignupFragmentTest {
    //Test Fields
    private FragmentScenario<SignupFragment> scenario;

    //Setting up test environment
    @Before
    public void setUp() {
        scenario = launchInContainer(SignupFragment.class, null);
    }

    //Closing test scenario
    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();
        }
    }

    /**
     * Tests whether all the required
     * components are displayed
     */
    @Test
    public void signupScreen_DisplaysComponents() {
        onView(withId(R.id.signup_username_input)).check(matches(isDisplayed()));
        onView(withId(R.id.signup_email_input)).check(matches(isDisplayed()));
        onView(withId(R.id.signup_password_input)).check(matches(isDisplayed()));
        onView(withId(R.id.signup_confirm_password)).check(matches(isDisplayed()));
        onView(withId(R.id.signupButton)).check(matches(isDisplayed()));
        onView(withId(R.id.loginLink)).check(matches(isDisplayed()));
    }

    /**
     * Tests whether the signup username and email input fields accept and display text
     * entered by the user.
     */
    @Test
    public void usernameInputs_AcceptTypedText() {
        onView(withId(R.id.signup_username_input)).perform(typeText("test123"));
        onView(withId(R.id.signup_email_input)).perform(typeText("test123@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.signup_username_input)).check(matches(withText("test123")));
        onView(withId(R.id.signup_email_input)).check(matches(withText("test123@gmail.com")));
    }

    /**
     * Tests whether the signup password and confirm_password field accept
     * and match text entered by the user.
     */
    @Test
    public void passwordInputs_AcceptTypedText() {
        onView(withId(R.id.signup_password_input)).perform(typeText("Passwordtest123"));
        onView(withId(R.id.signup_confirm_password)).perform(typeText("Passwordtest123"), closeSoftKeyboard());
        scenario.onFragment(fragment -> {
            EditText password = fragment.getView().findViewById(R.id.signup_password_input);
            EditText confirmPassword = fragment.getView().findViewById(R.id.signup_confirm_password);
            assertEquals("Passwordtest123", password.getText().toString());
            assertEquals("Passwordtest123", confirmPassword.getText().toString());
        });
    }

    /**
     * Tests whether showSignUpFailedError displays the given
     * error message in the signup error TextView.
     */
    @Test
    public void showSignupError_DisplaysGivenMessage() {
        scenario.onFragment(fragment ->
                fragment.showSignUpFailedError("Error message 123")
        );
        onView(withId(R.id.signupErrorText)).check(matches(isDisplayed()))
                .check(matches(withText("Error message 123")));
    }

    /**
     * Tests whether showLoadingIndicator makes the
     * signup progress indicator visible.
     */
    @Test
    public void showLoadingIndicator_DisplaysProgressBar() {
        scenario.onFragment(fragment -> {
            fragment.showLoadingIndicator();
        });
        onView(withId(R.id.signupProgressBar)).check(matches(isDisplayed()));
    }

    /**
     * Tests whether hideLoadingIndicator makes the
     * signup progress indicator invisible.
     */
    @Test
    public void hideLoadingIndicator_HidesProgressBar() {
        scenario.onFragment(fragment -> {
            fragment.showLoadingIndicator();
            fragment.hideLoadingIndicator();
        });
        onView(withId(R.id.signupProgressBar)).check(matches(not(isDisplayed())));
    }

    /**
     * Tests whether disableButton disables all signup controls while
     * a signup request is being processed.
     */
    @Test
    public void disableButton_DisablesSignupControls() {
        scenario.onFragment(SignupFragment::disableButton);
        scenario.onFragment(fragment -> {
            fragment.disableButton();
        });
        onView(withId(R.id.signupButton)).check(matches(not(isEnabled())));
        onView(withId(R.id.signup_username_input)).check(matches(not(isEnabled())));
        onView(withId(R.id.signup_email_input)).check(matches(not(isEnabled())));
        onView(withId(R.id.signup_password_input)).check(matches(not(isEnabled())));
        onView(withId(R.id.signup_confirm_password)).check(matches(not(isEnabled())));
        onView(withId(R.id.loginLink)).check(matches(not(isEnabled())));
    }

    /**
     * Tests whether enableButton re-enables all signup controls after
     * they have previously been disabled.
     */
    @Test
    public void enableButton_EnablesSignupControls() {
        scenario.onFragment(fragment -> {
            fragment.disableButton();
            fragment.enableButton();
        });
        onView(withId(R.id.signupButton)).check(matches(isEnabled()));
        onView(withId(R.id.signup_username_input)).check(matches(isEnabled()));
        onView(withId(R.id.signup_email_input)).check(matches(isEnabled()));
        onView(withId(R.id.signup_password_input)).check(matches(isEnabled()));
        onView(withId(R.id.signup_confirm_password)).check(matches(isEnabled()));
        onView(withId(R.id.loginLink)).check(matches(isEnabled()));
    }
}