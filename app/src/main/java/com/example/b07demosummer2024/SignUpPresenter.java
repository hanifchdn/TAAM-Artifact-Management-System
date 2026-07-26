package com.example.b07demosummer2024;

public class SignUpPresenter implements SignUpContract.Presenter {

    private SignUpContract.View view;
    private SignUpContract.Model model;

    public SignUpPresenter(SignUpContract.View view, SignUpContract.Model model){

        this.view = view;
        this.model = model;
    }
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private boolean isValidEmail(String email){
        return email.matches(EMAIL_REGEX);
    }
    @Override
    public void signUp(String email, String password, String confirmPassword){
        if (email.isEmpty()) {
            view.showEmptyEmailError();
            return;
        }
        if(!isValidEmail(email)){
            view.showInvalidEmailFormatError();
            return;
        }
        if (password.isEmpty()) {
            view.showEmptyPasswordError();
            return;
        }
        if (confirmPassword.isEmpty() ){
            view.showEmptyConfirmPasswordError();
            return;
        }
        if (!password.equals(confirmPassword)){
            view.showPasswordMismatchError();
            return;
        }

        view.showLoadingIndicator();
        view.disableButton();

        model.signUp(email, password, new SignUpContract.Model.Authcallback() {
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
                view.showSignUpFailedError(message);
            }
        });
    }
    @Override
    public void onDestroy(){
        view = null;
    }
}