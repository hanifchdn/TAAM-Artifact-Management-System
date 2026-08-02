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
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.TextPaint;
import android.widget.TextView;
import android.widget.ProgressBar;




public class LoginFragment extends Fragment implements LoginContract.View {
    private Button loginButton;
    private EditText usernameInput;
    private EditText passwordInput;
    private TextView signupLink;
    private TextView loginErrorText;
    private ProgressBar loginProgressBar;

    private boolean isPasswordVisible = false;


    private LoginContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

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

    private void clearLoginError() {
        loginErrorText.setVisibility(View.GONE);
    }

    @Override
    public void showLoadingIndicator() {
        loginProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingIndicator() {
        loginProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void disableButton() {
        loginButton.setEnabled(false);
        usernameInput.setEnabled(false);
        passwordInput.setEnabled(false);
        signupLink.setEnabled(false);
    }

    @Override
    public void enableButton() {
        loginButton.setEnabled(true);
        usernameInput.setEnabled(true);
        passwordInput.setEnabled(true);
        signupLink.setEnabled(true);
    }

    @Override
    public void showEmptyEmailError() {
        showLoginError("Email cannot be empty.");
        usernameInput.requestFocus();
    }

    @Override
    public void showEmptyPasswordError() {
        showLoginError("Password cannot be empty.");
        passwordInput.requestFocus();
    }

    @Override
    public void showInvalidEmailFormatError() {
        showLoginError("Please enter a valid email address.");
        usernameInput.requestFocus();
    }

    @Override
    public void showLoginError(String message) {
        loginErrorText.setText(message);
        loginErrorText.setVisibility(View.VISIBLE);
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
