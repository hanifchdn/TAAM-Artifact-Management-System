package com.example.b07demosummer2024;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    private LoginContract.Model model;
    public LoginPresenter(LoginContract.View view, LoginContract.Model model){
        this.view = view;
        this.model = model;
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

        view.showLoadingIndicator();
        view.disableButton();

        model.login(email, password, new LoginContract.Model.AuthCallback() {
            @Override
            public void onSuccess(String uid) {
                if (view == null) {
                    return;
                }
                view.hideLoadingIndicator();
                view.enableButton();
                view.navigateToHome();
            }
            @Override
            public void onFailure(String message) {
                if (view == null) {
                    return;
                }
                view.hideLoadingIndicator();
                view.enableButton();
                view.showLoginError(message);
            }
        });
    }
    @Override
    public void onDestroy(){
        view = null;
    }
}