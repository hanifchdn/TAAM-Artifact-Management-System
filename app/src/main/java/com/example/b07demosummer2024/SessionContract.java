package com.example.b07demosummer2024;

/**
 *  Contract on how the Session Model will be implemented.
 */
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