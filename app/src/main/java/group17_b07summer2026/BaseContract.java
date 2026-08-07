package group17_b07summer2026;

/**
 * Base contract containing common methods shared by Login and Sign Up MVP views and presenters
 */
public interface BaseContract {
    /**
     * Defines common UI operations used by Login and Sign Up views
     */
     interface View {
        /**
         * Displays the loading indicator
         */
        void showLoadingIndicator();

        /**
         * Hides the loading indicator
         */
        void hideLoadingIndicator();

        /**
         * Disable the action button
         */
        void disableButton();

        /**
         * Enable the action button
         */
        void enableButton();
    }

    /**
     * Defines common operation used by the Login and Sign Up presenters
     */
    interface Presenter{
        /**
         * Cleans up the presenter when the view is destroyed
         */
        void onDestroy();
    }
}
