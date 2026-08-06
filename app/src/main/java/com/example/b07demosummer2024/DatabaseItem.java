package com.example.b07demosummer2024;

/**
 * Defines the structure required for items stored in the Firebase Database
 */
public interface DatabaseItem {

    /**
     * Returns the LOT number used to identify a database item
     *
     * @return the LOT number of a database item
     */
    public abstract String getLOT();

}