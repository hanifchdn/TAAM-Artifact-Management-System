package com.example.b07demosummer2024;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ProgressBar;


/**
 * Fragment that displays the login screen
 */
public class LoginFragment extends Fragment implements LoginContract.View {
    private Button loginButton;
    private EditText usernameInput;
    private EditText passwordInput;
    private TextView signupLink;
    private TextView loginErrorText;
    private ProgressBar loginProgressBar;

    private boolean isPasswordVisible = false;


    private LoginContract.Presenter presenter;

    /**
     * Creates and returns the login view.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Inflated login view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
    /**
     * Initializes UI components and click listeners.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * Binds variables from xml layout to java
         */
        usernameInput = view.findViewById(R.id.username_input);
        passwordInput = view.findViewById(R.id.password_input);
        loginButton = view.findViewById(R.id.loginButton);
        signupLink = view.findViewById(R.id.signupLink);
        loginProgressBar = view.findViewById(R.id.loginProgressBar);
        loginErrorText = view.findViewById(R.id.loginErrorText);

        presenter = new LoginPresenter(this, new LoginModel(), new SessionModel(), SessionManager.getInstance());

        loginButton.setOnClickListener(v -> {
            clearLoginError();
            String email = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            presenter.login(email, password);
        });

        signupLink.setOnClickListener(v -> {
            loadFragment(new SignupFragment(), true);
        });

        /**
         * Password visibility listener
         */
        passwordInput.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordInput.getRight()
                        - passwordInput.getCompoundDrawables()[DRAWABLE_END].getBounds().width()
                        - passwordInput.getPaddingEnd())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * Toggles password visibility
     */
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        int cursorPosition = passwordInput.getSelectionEnd();
        if (isPasswordVisible) {
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        passwordInput.setSelection(cursorPosition);
    }

    /**
     * Replaces the current fragment.
     * @param fragment Fragment to display.
     * @param addToBackStack Whether to add the transaction to the back stack.
     */
    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Hides the current login error message.
     */
    private void clearLoginError() {
        loginErrorText.setVisibility(View.GONE);
    }

    /**
     * Shows the loading indicator
     */
    @Override
    public void showLoadingIndicator() {
        loginProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the loading indicator
     */
    @Override
    public void hideLoadingIndicator() {
        loginProgressBar.setVisibility(View.GONE);
    }

    /**
     * Disables login form interaction
     */
    @Override
    public void disableButton() {
        loginButton.setEnabled(false);
        usernameInput.setEnabled(false);
        passwordInput.setEnabled(false);
        signupLink.setEnabled(false);
    }

    /**
     * Enables login form interaction
     */
    @Override
    public void enableButton() {
        loginButton.setEnabled(true);
        usernameInput.setEnabled(true);
        passwordInput.setEnabled(true);
        signupLink.setEnabled(true);
    }

    /**
     * Displays an empty email error
     */
    @Override
    public void showEmptyEmailError() {
        showLoginError("Email cannot be empty.");
        usernameInput.requestFocus();
    }

    /**
     * Displays an empty password error
     */
    @Override
    public void showEmptyPasswordError() {
        showLoginError("Password cannot be empty.");
        passwordInput.requestFocus();
    }

    /**
     * Display an invalid email format error
     */
    @Override
    public void showInvalidEmailFormatError() {
        showLoginError("Please enter a valid email address.");
        usernameInput.requestFocus();
    }

    /**
     * Displays a login error message.
     *
     * @param message Error message to display.
     */
    @Override
    public void showLoginError(String message) {
        loginErrorText.setText(message);
        loginErrorText.setVisibility(View.VISIBLE);
    }

    /**
     * Navigates to the home page after successful login.
     */
    @Override
    public void navigateToHome() {
        loadFragment(new HomeFragment(), false);
    }

    /**
     * Detaches and clears the presenter when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        if (presenter != null) {
            presenter.onDestroy();
            presenter = null;
        }
        super.onDestroyView();
    }
}
