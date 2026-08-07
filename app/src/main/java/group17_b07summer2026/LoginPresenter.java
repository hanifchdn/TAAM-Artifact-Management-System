package group17_b07summer2026;


/**
 * Presenter that handles login validation, authentication,
 * and user session initialization.
 */
public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    private LoginContract.Model model;
    private SessionContract.Model sessionModel;
    private SessionManager session;

    /**
     * Creates a login presenter
     * @param view View that displays login UI updates
     * @param model Model that performs authentication
     * @param sessionModel Model that retrieves user profile data
     * @param session Stores the current logged-in user
     */
    public LoginPresenter(LoginContract.View view, LoginContract.Model model, SessionContract.Model sessionModel, SessionManager session){
        this.view = view;
        this.model = model;
        this.sessionModel = sessionModel;
        this.session = session;
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
     * Validate the login credentials, authenticated users,
     * and initializes the user session on success.
     * @param email User's email address
     * @param password User's password
     */
    @Override
    public void login(String email, String password) {
        if (view == null) {
            return;
        }
        if (email == null || email.isEmpty()) {
            view.showEmptyEmailError();
            return;
        }

        if (password == null || password.isEmpty()) {
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
            /**
             * Handles successful authentication by clearing any existing session
             * and retrieving the authenticated user's profile.
             * @param uid ID of the authenticated user
             */
            @Override
            public void onSuccess(String uid) {
                if (view == null) {
                    return;
                }
                if (session.getCurrentUser() != null){
                    session.clear();
                }
                sessionModel.fetchUserProfile(uid, new SessionContract.Model.ProfileCallback() {
                    /**
                     * Handles successful profile retrieval by storing the user
                     * and navigating to the home page.
                     * @param user Retrieved user profile
                     */
                    public void onProfileLoaded(User user) {
                        if (view == null) return;
                        session.setCurrentUser(user);
                        view.hideLoadingIndicator(); view.enableButton(); view.navigateToHome();
                    }
                    /**
                     * Handles profile retrieval failure by logging out
                     * and displaying an error message.
                     * @param message Error message to display
                     */
                    public void onProfileError(String message) {
                        sessionModel.logOut();

                        if (view == null) return;
                        view.hideLoadingIndicator(); view.enableButton(); view.showLoginError(message);
                    }
                });
            }
            /**
             * Handles failed authentication by restoring the login UI
             * and displaying an error message.
             * @param message Error message to display
             */
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

    /**
     * Releases the view reference to prevent memory leaks
     */
    @Override
    public void onDestroy(){
        view = null;
    }
}