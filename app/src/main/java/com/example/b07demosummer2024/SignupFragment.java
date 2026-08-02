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
import android.widget.ProgressBar;
import android.widget.TextView;


public class SignupFragment extends Fragment implements SignUpContract.View {
    private Button signupButton;
    private EditText usernameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private TextView loginLink;
    private TextView signupErrorText;
    private ProgressBar signupProgressBar;

    private boolean isPasswordVisible = false;


    private SignUpContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * Binds variables from xml layout to java
         */
        usernameInput = view.findViewById(R.id.signup_username_input);
        passwordInput = view.findViewById(R.id.signup_password_input);
        emailInput = view.findViewById(R.id.signup_email_input);
        signupButton = view.findViewById(R.id.signupButton);
        confirmPasswordInput = view.findViewById(R.id.signup_confirm_password);
        loginLink = view.findViewById(R.id.loginLink);
        signupErrorText = view.findViewById(R.id.signupErrorText);
        signupProgressBar = view.findViewById(R.id.signupProgressBar);

        presenter = new SignUpPresenter(this, new SignupModel());

        signupButton.setOnClickListener(v -> {
            clearSignupError();
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();
            presenter.signUp(username, email, password, confirmPassword);
        });

        loginLink.setOnClickListener(v -> {
            loadFragment(new LoginFragment(), true);
        });

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

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    public void clearSignupError() {
        signupErrorText.setVisibility(View.GONE);
    }

    @Override
    public void showLoadingIndicator() {
        signupProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingIndicator() {
        signupProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void disableButton() {
        signupButton.setEnabled(false);
        usernameInput.setEnabled(false);
        emailInput.setEnabled(false);
        passwordInput.setEnabled(false);
        confirmPasswordInput.setEnabled(false);
        loginLink.setEnabled(false);
    }

    @Override
    public void enableButton() {
        signupButton.setEnabled(true);
        usernameInput.setEnabled(true);
        emailInput.setEnabled(true);
        passwordInput.setEnabled(true);
        confirmPasswordInput.setEnabled(true);
        loginLink.setEnabled(true);
    }

    @Override
    public void showEmptyUsernameError() {
        showSignUpFailedError("Username cannot be empty.");
    }

    @Override
    public void showEmptyEmailError() {
        showSignUpFailedError("Email cannot be empty.");
    }

    @Override
    public void showEmptyPasswordError() {
        showSignUpFailedError("Password cannot be empty.");
    }

    @Override
    public void showEmptyConfirmPasswordError() {
        showSignUpFailedError("Please confirm your password.");
    }

    @Override
    public void showInvalidEmailFormatError() {
        showSignUpFailedError("Please enter a valid email.");
    }

    @Override
    public void showPasswordMismatchError() {
        showSignUpFailedError("Passwords do not match.");
    }

    @Override
    public void showSignUpFailedError(String message) {
        signupErrorText.setText(message);
        signupErrorText.setVisibility(View.VISIBLE);
    }

    @Override
    public void navigateToHome() {
        loadFragment(new HomeFragment(), false);
    }

    @Override
    public void onDestroyView() {
        if (presenter != null) {
            presenter.onDestroy();
            presenter = null;
        }
        super.onDestroyView();
    }
}