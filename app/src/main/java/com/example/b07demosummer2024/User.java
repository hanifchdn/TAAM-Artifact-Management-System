package com.example.b07demosummer2024;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String username;
    private String email;
    private String uid;
    private boolean isAdmin;
    private List<String> savedArtifactList;

    public User(){

    }

    public User(String username, String email, String uid, boolean isAdmin) {
        this.username = username;
        this.email = email;
        this.uid = uid;
        this.isAdmin = isAdmin;
        this.savedArtifactList = new ArrayList<>();
    }

    /**
     * Deletes an artifact lot to the saved artifact list
     * If the artifact lot is already in there, nothing is removed, no error occurs
     * @param lot of artifact to remove
     */
    public void removeSavedArtifact(String lot) {
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
    public List<String> getSavedArtifactList() {return savedArtifactList; }
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
        return isAdmin;
    }

}
