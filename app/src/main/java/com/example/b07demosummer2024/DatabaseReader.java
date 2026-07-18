package com.example.b07demosummer2024;

public interface DatabaseReader {

    public DatabaseItem getItem(String UID);

    public boolean contains(String UID);

}
