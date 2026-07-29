package com.example.b07demosummer2024;

public interface SessionContract {
    interface Model {
        String currentUserUid();
        void fetchUserProfile(String uid, ProfileCallback callback);
        void logOut();

        interface ProfileCallback {
            void onProfileLoaded(User user);
            void onProfileError(String message);
        }
    }
}