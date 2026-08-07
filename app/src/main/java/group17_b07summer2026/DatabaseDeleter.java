package group17_b07summer2026;

/**
 * Defines a method for deleting database items from the Firebase Database
 */
public interface DatabaseDeleter {

    /**
     * Deletes a database item from the Firebase Database
     *
     * @param e the database item to delete
     * @param callback the callback notified when the write succeeds or fails
     */
    public void deleteFromDatabase(DatabaseItem e, WriteCallback callback);

}