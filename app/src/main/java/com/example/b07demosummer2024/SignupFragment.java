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


public class SignupFragment extends Fragment {
    private Button signupButton;
    private boolean isPasswordVisible = false;
    private EditText usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private TextView loginLink;
    private TextView signupErrorText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usernameInput = view.findViewById(R.id.signup_username_input);
        passwordInput = view.findViewById(R.id.signup_password_input);
        emailInput = view.findViewById(R.id.signup_email_input);
        signupButton = view.findViewById(R.id.signupButton);
        confirmPasswordInput = view.findViewById(R.id.signup_confirm_password);
        loginLink = view.findViewById(R.id.loginLink);
        signupErrorText = view.findViewById(R.id.signupErrorText);

        signupButton.setOnClickListener(v -> {
            // Connect to SignupPresenter when possible
        });

        loginLink.setOnClickListener(v -> {
            loadFragment(new LoginFragment());
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

    public void showSignupError(String message) {
        signupErrorText.setText(message);
        signupErrorText.setVisibility(View.VISIBLE);
    }

    public void clearSignupError() {
        signupErrorText.setVisibility(View.GONE);
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