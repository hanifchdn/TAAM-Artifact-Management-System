package group17_b07summer2026;

/**
 *  Contract on how the Session Model will be implemented.
 */
public interface SessionContract {
    interface Model {
        /**
         * Returns the UID of the currently authenticated user.
         *
         * @return the current user's UID, or null if no user is signed in
         */
        String currentUserUid();

        /**
         * Fetches the profile of the user with the given UID from the database.
         *
         * @param uid the id stored in Firebase for the user whose profile to fetch
         * @param callback a callback invoked with the loaded profile or an error message
         */
        void fetchUserProfile(String uid, ProfileCallback callback);

        /**
         * Signs the current user out and clears the active session.
         */
        void logOut();

        /**
         * Callback for the result of fetching a user profile.
         */
        interface ProfileCallback {
            /**
             * Called when the user's profile has been loaded successfully.
             *
             * @param user the loaded user profile
             */
            void onProfileLoaded(User user);

            /**
             * Called when the profile could not be loaded.
             *
             * @param message a human-readable error message describing the failure
             */
            void onProfileError(String message);
        }
    }
}