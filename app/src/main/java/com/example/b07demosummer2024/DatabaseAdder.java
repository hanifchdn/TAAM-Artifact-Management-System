package com.example.b07demosummer2024;

/**
 * Defines a method for adding database items to the Firebase Database
 */
public interface DatabaseAdder{
    /**
     * Adds a database item to the Firebase Database
     *
     * @param e the database item to add
     * @param callback the callback notified when the write succeeds or fails
     */
    public void addToDatabase(DatabaseItem e, WriteCallback callback);

}