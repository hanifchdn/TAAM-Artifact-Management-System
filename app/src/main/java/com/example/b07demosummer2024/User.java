package com.example.b07demosummer2024;

import java.util.ArrayList;
import java.util.List;

/**
 * User class that defines User structure
 *
 * Represents the type contract and fields of a User
 */
public class User {

    private String username;
    private String email;
    private String uid;
    private boolean admin;
    private List<String> savedArtifactList;

    /**
     * No Argument constructor for firebase, Do not use this to create a User
     */
    public User(){

    }

    /**
     * Creates a User with the given account information
     *
     * @param username the user's username
     * @param email the user's email address
     * @param uid the unique ID of the user
     * @param admin whether the user is an administrator
     */
    public User(String username, String email, String uid, boolean admin) {
        this.username = username;
        this.email = email;
        this.uid = uid;
        this.admin = admin;
    }

    /**
     * Deletes an artifact lot to the saved artifact list
     * If the artifact lot is already in there, nothing is removed, no error occurs
     * @param lot of artifact to remove
     */
    public void removeSavedArtifact(String lot) {
        if(savedArtifactList == null) {
            return;
        }
        savedArtifactList.remove(lot);
    }

    /**
     * Adds an artifact lot to the saved artifact list
     * If artifact lot is already in there, a duplicate will not be added
     * @param lot of artifact to add
     */
    public void addSavedArtifact(String lot) {
        if (containsSavedArtifact(lot)) {
            return;
        }
        if (savedArtifactList == null) {
            savedArtifactList = new ArrayList<String>();
        }
        savedArtifactList.add(lot);
    }

    /**
     * Returns if an artifact is in the saved artifact list
     * @param lot of artifact to check
     * @return true iff artifactList contains the lot
     */
    public boolean containsSavedArtifact(String lot) {
        if (savedArtifactList == null) {
            return false;
        }
        return savedArtifactList.contains(lot);
    }

    public void setSavedArtifactList(List<String> savedArtifactList) {
        this.savedArtifactList = savedArtifactList;
    }
    public List<String> getSavedArtifactList() {
        if (savedArtifactList == null) {
            return new ArrayList<String>();
        }
        return savedArtifactList;
    }
    public String getUsername() {
        return username;
    }
    public String getUid(){
        return uid;
    }
    public String getEmail() {
        return email;
    }
    public boolean isAdmin() {
        return admin;
    }

}
