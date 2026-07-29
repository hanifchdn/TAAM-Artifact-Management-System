package com.example.b07demosummer2024;

import java.util.ArrayList;
import java.util.List;

import kotlin.collections.ArrayDeque;

public class User {

    private String username;
    private String email;
    private String uid;
    private boolean isAdmin;
    private List<String> likedArtifacts;

    public User(){

    }

    public User(String username, String email, String uid, boolean isAdmin) {
        this.username = username;
        this.email = email;
        this.uid = uid;
        this.isAdmin = isAdmin;
        likedArtifacts = new ArrayList<String>();
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
        return isAdmin;
    }
    public List<String> getLikedArtifacts() {
        return likedArtifacts;
    }

    /**
     * Adds a artifact lot to it's liked artifact list
     * @param lot of artifact to add
     */
    public void addLikedArtifact(String lot) {
        if (likedArtifacts.contains(lot)) {
            return;
        }
        likedArtifacts.add(lot);

    }

    /**
     * Removes a artifact lot from it's liked artifact list
     * @param lot of artifact to remove
     */
    public void removeLikedArtifact(String lot) {
        if (!likedArtifacts.contains(lot)) {
            return;
        }
        likedArtifacts.remove(lot);
    }

    /**
     * Returns if an artifact lot is in the users liked artifacts
     */
    public boolean containsLikedArtifact(String lot) {
        return likedArtifacts.contains(lot);
    }
}
