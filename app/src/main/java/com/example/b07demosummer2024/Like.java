package com.example.b07demosummer2024;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Like class that defines Like structure
 *
 * Represents the type contract and fields of a Like under an Artifact
 */
public class Like {

    private String userId;
    private String artifactLot;
    private String likeId;

    /**
     * No Argument constructor for firebase, Do not use this to create a like
     */
    public Like() {}

    /**
     * Creates a new Like object
     * @param userId of owning user
     * @param artifactLot of artifact
     */
    public Like(String userId, String artifactLot) {
        this.likeId = FirebaseDatabase.getInstance().getReference().push().getKey();
        this.artifactLot = artifactLot;
        this.userId = userId;
    }

    public String getArtifactLot() {
        return artifactLot;
    }

    public void setArtifactLot(String artifactLot) {
        this.artifactLot = artifactLot;
    }

    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
