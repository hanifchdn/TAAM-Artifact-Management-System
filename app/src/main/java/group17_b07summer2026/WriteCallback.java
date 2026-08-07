package group17_b07summer2026;

/**
 * Callback used to report the result of a database write
 */
public interface WriteCallback {
    /**
     * Called upon database write success
     */
    void onSuccess();

    /**
     * Called upon database write failure
     *
     * @param err the error message of the failure
     */
    void onFailure(String err);
}