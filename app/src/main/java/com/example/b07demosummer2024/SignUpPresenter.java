package com.example.b07demosummer2024;

public class SignUpPresenter implements SignUpContract.Presenter {

    private SignUpContract.View view;
    public SignUpPresenter(SignUpContract.View view){

        this.view = view;
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
    }
    @Override
    public void onDestroy(){
        view = null;
    }
}