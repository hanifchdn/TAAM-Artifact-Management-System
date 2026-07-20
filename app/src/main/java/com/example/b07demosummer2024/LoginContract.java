package com.example.b07demosummer2024;

public interface LoginContract {
    interface View extends BaseContract.View{
        void showEmptyEmailError();
        void showEmptyPasswordError();
        void showInvalidEmailFormatError();
        void navigateToHome();
    }
    interface Presenter extends BaseContract.Presenter{
        void login(String email, String password);
    }

    interface Model{
        void login(String email, String password, AuthCallback callback);

        interface AuthCallback {
            void onSuccess(String uid);
            void onFailure(String message);
        }
    }

}
