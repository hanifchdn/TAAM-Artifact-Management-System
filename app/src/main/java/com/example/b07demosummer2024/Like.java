package com.example.b07demosummer2024;

import com.google.firebase.database.FirebaseDatabase;

public class Like {

    private String userId;
    private String artifactLot;
    private String likeId;

    public Like() {}

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
