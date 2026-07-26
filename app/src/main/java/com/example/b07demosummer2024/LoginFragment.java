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




public class LoginFragment extends Fragment {
    private Button loginButton;
    private boolean isPasswordVisible = false;
    private EditText usernameInput;
    private EditText passwordInput;
    private TextView signupLink;
    private ProgressBar loginProgressBar;
    private TextView loginErrorText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usernameInput = view.findViewById(R.id.username_input);
        passwordInput = view.findViewById(R.id.password_input);
        loginButton = view.findViewById(R.id.loginButton);
        signupLink = view.findViewById(R.id.signupLink);
        loginProgressBar = view.findViewById(R.id.loginProgressBar);
        loginErrorText = view.findViewById(R.id.loginErrorText);

        loginButton.setOnClickListener(v -> {
            setLoading(true);

            // TODO: Perform login request here

            loginButton.postDelayed(() -> {
                setLoading(false);
                loadFragment(new HomeFragment());
            }, 1500);
        });

        signupLink.setOnClickListener(v -> {
            loadFragment(new SignupFragment());
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

    public void showLoginError(String message) {
        loginErrorText.setText(message);
        loginErrorText.setVisibility(View.VISIBLE);
    }

    public void clearLoginError() {
        loginErrorText.setVisibility(View.GONE);
    }

    /**
     * Make buttons inactive when login request is running
     */
    private void setLoading(boolean isLoading) {
        if (isLoading) {
            loginProgressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);
            usernameInput.setEnabled(false);
            passwordInput.setEnabled(false);
            signupLink.setEnabled(false);
        } else {
            loginProgressBar.setVisibility(View.GONE);
            loginButton.setEnabled(true);
            usernameInput.setEnabled(true);
            passwordInput.setEnabled(true);
            signupLink.setEnabled(true);
        }
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

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
