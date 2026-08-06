package com.example.b07demosummer2024;

/**
 * Defines the MVP contract for the login feature
 */
public interface LoginContract {
    /**
     * Defines login-related view operations
     */
    interface View extends BaseContract.View{
        void showEmptyEmailError();
        void showEmptyPasswordError();
        void showInvalidEmailFormatError();
        void navigateToHome();
        void showLoginError(String message);
    }

    /**
     * Defines login-related presenter operations
     */
    interface Presenter extends BaseContract.Presenter{
        /**
         * Attempts to log in with the provided credentials
         * @param email User's email address
         * @param password User's password
         */
        void login(String email, String password);
    }
    /**
     * Defines login-related model operations
     */
    interface Model{
        /**
         * Authenticates a user.
         * @param email User's email address
         * @param password User's password
         * @param callback Callback for authentication result
         */
        void login(String email, String password, AuthCallback callback);
        /**
         * Callback for login success or failure
         */
        interface AuthCallback {
            /**
             * Called when login succeeds
             *
             * @param uid Authenticated user's ID
             */
            void onSuccess(String uid);

            /**
             * Called when login fails
             * @param message Error message
             */
            void onFailure(String message);
        }
    }

}
