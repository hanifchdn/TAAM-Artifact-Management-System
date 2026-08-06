package com.example.b07demosummer2024;
/**
 * Defines the MVP contract for the sign-up feature.
 */
public interface SignUpContract {
    /**
     * Defines sign-up related view operations.
     */
    interface View extends BaseContract.View{
        void showEmptyUsernameError();
        void showEmptyEmailError();
        void showEmptyPasswordError();
        void showEmptyConfirmPasswordError();
        void showInvalidEmailFormatError();
        void showPasswordMismatchError();
        void showSignUpFailedError(String message);
        void navigateToHome();
    }
    /**
     * Defines sign-up related presenter operations.
     */
    interface Presenter extends BaseContract.Presenter{
        /**
         * Attempts to register a new user.
         * @param username User's username
         * @param email User's email address
         * @param password User's password
         * @param confirmPassword Password confirmation
         */
        void signUp(String username, String email, String password, String confirmPassword);
    }
    /**
     * Defines sign-up related model operations.
     */
    interface Model{
        /**
         * Create a new user account
         * @param username User's username
         * @param email User's email
         * @param password User's password
         * @param callback Callback for sign-up success or failure
         */
        void signUp(String username, String email, String password, Authcallback callback);

        /**
         * Callback for sign-up success and failure
         */
        interface Authcallback{
            /**
             * Called when sign-up succeeds.
             * @param uid ID of the newly created user
             */
            void onSuccess(String uid);

            /**
             * Called when sign-up fails
             * @param message Error message
             */
            void onFailure(String message);
        }

    }
}
