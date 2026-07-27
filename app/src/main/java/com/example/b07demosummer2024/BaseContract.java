package com.example.b07demosummer2024;

public interface BaseContract {
     interface View {
        void showLoadingIndicator();
        void hideLoadingIndicator();
        void disableButton();
        void enableButton();
    }
    interface Presenter{
        void onDestroy();
    }
}
