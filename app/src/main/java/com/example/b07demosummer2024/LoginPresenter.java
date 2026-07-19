package com.example.b07demosummer2024;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    public LoginPresenter(LoginContract.View view){
        this.view = view;
    }
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private boolean isValidEmail(String email){
        return email.matches(EMAIL_REGEX);
    }
    @Override
    public void login(String email, String password) {
        if (email.isEmpty()) {
            view.showEmptyEmailError();
            return;
        }

        if (password.isEmpty()) {
            view.showEmptyPasswordError();
            return;
        }

        if (!isValidEmail(email)) {
            view.showInvalidEmailFormatError();
            return;
        }
    }
    @Override
    public void onDestroy(){
        view = null;
    }
}