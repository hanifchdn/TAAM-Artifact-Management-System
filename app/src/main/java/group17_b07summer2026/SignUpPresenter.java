package group17_b07summer2026;
/**
 * Presenter that handles sign-up validation and account creation
 */
public class SignUpPresenter implements SignUpContract.Presenter {

    private SignUpContract.View view;
    private SignUpContract.Model model;
    private SessionContract.Model sessionModel;

    /**
     * Creates a sign-up presenter
     * @param view View that displays sign-up UI updates
     * @param model Model that creates user accounts
     * @param sessionModel Model that retrieves user profile data
     */
    public SignUpPresenter(SignUpContract.View view, SignUpContract.Model model, SessionContract.Model sessionModel){

        this.view = view;
        this.model = model;
        this.sessionModel = sessionModel;
    }
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    /**
     * Checks whether an email has a valid format
     * @param email Email to validate
     * @return true if the email format is valid, false otherwise
     */
    private boolean isValidEmail(String email){
        return email.matches(EMAIL_REGEX);
    }

    /**
     * Validates the sign-up input, creates the user account, and initialize the user success on
     * success
     * @param username User's username
     * @param email User's email address
     * @param password User's password
     * @param confirmPassword Password confirmation
     */
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
            /**
             * Handles successful account creation by retrieving
             * the user's profile.
             * @param uid ID of the newly created user
             */
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
                    /**
                     * Stores the user profile and navigate to home page
                     * @param user the loaded user profile
                     */
                    @Override
                    public void onProfileLoaded(User user) {
                        if (view == null) return;
                        session.setCurrentUser(user);
                        view.hideLoadingIndicator(); view.enableButton(); view.navigateToHome();
                    }

                    /**
                     * Handles failure to retrieve the user profile
                     * @param message a human-readable error message describing the failure
                     */
                    @Override
                    public void onProfileError(String message) {
                        sessionModel.logOut();

                        if (view == null) return;
                        view.hideLoadingIndicator(); view.enableButton(); view.showSignUpFailedError(message);
                    }
                });
            }

            /**
             * Handles failed account creation
             * @param message Error message
             */
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

    /**
     * Releases the view reference to prevent memory leaks
     */
    @Override
    public void onDestroy(){
        view = null;
    }
}