package com.example.b07demosummer2024;

import com.google.firebase.database.FirebaseDatabase;

public class Comment {
    private String id;
    private String userId;
    private String username;
    private String body;
    private String artifactLot;

    public Comment() {

    }

    public Comment(String userId, String username,
                   String artifactLot, String body) {
        // a unique id is guaranteed to be generated
        this.id = FirebaseDatabase.getInstance().getReference().push().getKey();
        this.userId = userId;
        this.username = username;
        this.artifactLot = artifactLot;
        this.body = body;

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getArtifactLot() {
        return artifactLot;
    }

    public void setArtifactLot(String artifactLot) {
        this.artifactLot = artifactLot;
    }


}
