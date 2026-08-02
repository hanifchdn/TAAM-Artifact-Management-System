package com.example.b07demosummer2024;

public interface SignUpContract {
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
    interface Presenter extends BaseContract.Presenter{
        void signUp(String username, String email, String password, String confirmPassword);
    }

    interface Model{
        void signUp(String username, String email, String password, Authcallback callback);

        interface Authcallback{
            void onSuccess(String uid);
            void onFailure(String message);
        }

    }
}
