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
public class LoginFragmentTest {
    //Test Fields
    private FragmentScenario<LoginFragment> scenario;

    //Setting up test environment
    @Before
    public void setUp() {
        scenario = launchInContainer(LoginFragment.class, null);
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
    public void loginPage_DisplaysComponents() {
        onView(withId(R.id.username_input)).check(matches(isDisplayed()));
        onView(withId(R.id.password_input)).check(matches(isDisplayed()));
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));
        onView(withId(R.id.signupLink)).check(matches(isDisplayed()));
    }

    /**
     * Tests whether username_input field accepts
     * and displays text entered by user
     */
    @Test
    public void emailInput_AcceptsTypedText() {
        onView(withId(R.id.username_input)).perform(typeText("test123@gmail.com"),
                        closeSoftKeyboard()).check(matches(withText("test123@gmail.com")));
    }

    /**
     * Tests whether the password_input field accepts
     * and matches text entered by the user.
     */
    @Test
    public void passwordInput_AcceptsTypedText() {
        onView(withId(R.id.password_input)).perform(typeText("Passwordtest123"),
                        closeSoftKeyboard());
        scenario.onFragment(fragment -> {
            EditText passwordInput = fragment.getView().findViewById(R.id.password_input);
            assertEquals("Passwordtest123", passwordInput.getText().toString());
        });
    }

    /**
     * Tests whether showLoginError displays the given
     * error message in the login error TextView.
     */
    @Test
    public void showLoginError_DisplaysMessage() {
        scenario.onFragment(fragment -> {
            fragment.showLoginError("Error message 123");
        });
        onView(withId(R.id.loginErrorText)).check(matches(isDisplayed()))
                .check(matches(withText("Error message 123")));
    }

    /**
     * Tests whether showLoadingIndicator makes the
     * login progress indicator visible.
     */
    @Test
    public void showLoadingIndicator_DisplaysProgressBar() {
        scenario.onFragment(fragment -> {
            fragment.showLoadingIndicator();
        });
        onView(withId(R.id.loginProgressBar)).check(matches(isDisplayed()));
    }

    /**
     * Tests whether hideLoadingIndicator makes the
     * login progress indicator invisible.
     */
    @Test
    public void hideLoadingIndicator_HidesProgressBar() {
        scenario.onFragment(fragment -> {
            fragment.showLoadingIndicator();
            fragment.hideLoadingIndicator();
        });
        onView(withId(R.id.loginProgressBar)).check(matches(not(isDisplayed())));
    }

    /**
     * Tests whether disableButton disables all login controls while
     * a login request is being processed.
     */
    @Test
    public void disableButton_DisablesLoginControls() {
        scenario.onFragment(fragment -> {
            fragment.disableButton();
        });
        onView(withId(R.id.loginButton)).check(matches(not(isEnabled())));
        onView(withId(R.id.username_input)).check(matches(not(isEnabled())));
        onView(withId(R.id.password_input)).check(matches(not(isEnabled())));
        onView(withId(R.id.signupLink)).check(matches(not(isEnabled())));
    }

    /**
     * Tests whether enableButton re-enables all login controls after
     * they have previously been disabled.
     */
    @Test
    public void enableButton_EnablesLoginControls() {
        scenario.onFragment(fragment -> {
            fragment.disableButton();
            fragment.enableButton();
        });
        onView(withId(R.id.loginButton)).check(matches(isEnabled()));
        onView(withId(R.id.username_input)).check(matches(isEnabled()));
        onView(withId(R.id.password_input)).check(matches(isEnabled()));
        onView(withId(R.id.signupLink)).check(matches(isEnabled()));
    }
}