package com.example.b07demosummer2024;

public interface SignUpContract {
    interface View extends BaseContract.View{
        void showEmptyEmailError();
        void showEmptyPasswordError();
        void showEmptyConfirmPasswordError();
        void showInvalidEmailFormatError();
        void showPasswordMismatchError();
        void showSignUpFailedError();
        void navigateToHome();
    }
    interface Presenter extends BaseContract.Presenter{
        void signUp(String email, String password, String confirmPassword);
    }
}
