package com.example.b07demosummer2024;

public class SignUpPresenter implements SignUpContract.Presenter {

    private SignUpContract.View view;
    private SignUpContract.Model model;
    private SessionContract.Model sessionModel;

    public SignUpPresenter(SignUpContract.View view, SignUpContract.Model model, SessionContract.Model sessionModel){

        this.view = view;
        this.model = model;
        this.sessionModel = sessionModel;
    }
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private boolean isValidEmail(String email){
        return email.matches(EMAIL_REGEX);
    }
    @Override
    public void signUp(String username, String email, String password, String confirmPassword){
        if (view == null) {
            return;
        }
        if (username == null || username.isEmpty()){
            view.showEmptyUsernameError();
            return;
        }
        if (email == null || email.isEmpty()) {
            view.showEmptyEmailError();
            return;
        }
        if(!isValidEmail(email)){
            view.showInvalidEmailFormatError();
            return;
        }
        if (password == null || password.isEmpty()) {
            view.showEmptyPasswordError();
            return;
        }
        if (confirmPassword == null || confirmPassword.isEmpty() ){
            view.showEmptyConfirmPasswordError();
            return;
        }
        if (!password.equals(confirmPassword)){
            view.showPasswordMismatchError();
            return;
        }

        view.showLoadingIndicator();
        view.disableButton();

        model.signUp(username, email, password, new SignUpContract.Model.Authcallback() {
            @Override
            public void onSuccess(String uid) {
                if (view == null) {
                    return;
                }
                SessionManager session = SessionManager.getInstance();
                if (session.getCurrentUser() != null){
                    session.clear();
                }
                sessionModel.fetchUserProfile(uid, new SessionContract.Model.ProfileCallback() {
                    @Override
                    public void onProfileLoaded(User user) {
                        if (view == null) return;
                        session.setCurrentUser(user);
                        view.hideLoadingIndicator(); view.enableButton(); view.navigateToHome();
                    }

                    @Override
                    public void onProfileError(String message) {
                        sessionModel.logOut();

                        if (view == null) return;
                        view.hideLoadingIndicator(); view.enableButton(); view.showSignUpFailedError(message);
                    }
                });
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