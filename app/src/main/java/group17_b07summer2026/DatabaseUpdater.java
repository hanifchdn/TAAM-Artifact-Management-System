package group17_b07summer2026;

/**
 * Defines a method for updating database items in the Firebase Database
 */
public interface DatabaseUpdater {

    /**
     * Updates a database item in the Firebase Database
     *
     * @param e the database item to update
     * @param writeCallback the callback notified when the write succeeds or fails
     */
    public void updateDatabase(DatabaseItem e, WriteCallback writeCallback);

}