package com.example.b07demosummer2024;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;

/**
 * Artifact class that defines the Artifact structure
 *
 * Represents the type contract and fields of an Artifact.
 *
 */
public class Artifact implements DatabaseItem{

    private String LOT;
    private String name;
    private String description;
    private String category;
    private String material;
    private String dynasty;
    private String culturalOrigin;
    private String height;
    private String width;
    private String depth;

    private String condition;
    private String currentLocation;
    private String acquisitionMethod;
    private String provenance;
    private String accessionNumber;
    private String notes;
    private String imageUrl;
    private ArrayList<String> likedUsers;

    public Artifact() {}
    public Artifact(String LOT, String name, String description, String category, String material, String dynasty) {
        this.LOT = LOT;
        this.name = name;
        this.description = description;
        this.category = category;
        this.material = material;
        this.dynasty = dynasty;
        this.likedUsers = new ArrayList<String>();

    }

    /**
     * Returns if a user has already liked
     * @param UID of user to check if they have liked or not
     * @return true/false boolean if they liked or not
     */
    @Exclude
    public boolean isUserLiked(String UID) {
        return likedUsers.contains(UID);
    }

    /**
     * Removes a UID to the list of users who has liked this artifact.
     * If the UID is NOT already present, returns -1. Else, remove and return -1
     * Note this will NOT update the db
     * @param UID of the user to remove
     * @return -1 if user is NOT already in the list, else on success returns 0
     */
    public int removeLikedUser(String UID) {
        if (likedUsers.contains(UID)) {
            return -1;
        }
        likedUsers.add(UID);
        return 0;
    }

    /**
     * Adds a UID to the list of users who has liked this artifact.
     * If the UID is already present, then it will not add it, and returns -1.
     * Note this will NOT update the db
     * @param UID of the user to add
     * @return -1 if user is already in the list, else on success returns 0
     */
    public int addLikedUser(String UID) {
        if (likedUsers.contains(UID)) {
            return -1;
        }
        likedUsers.add(UID);
        return 0;
    }

    /**
     * Returns the total amount of likes on this artifact
     * @return number of likes on this artifact
     */
    @Exclude
    public int getTotalLikes() {
        return likedUsers.size();
    }
    public String getLOT() {
        return LOT;
    }

    public void setLOT(String LOT) {
        this.LOT = LOT;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getDynasty() {
        return dynasty;
    }

    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
    }

    public String getCulturalOrigin() {
        return culturalOrigin;
    }

    public void setCulturalOrigin(String culturalOrigin) {
        this.culturalOrigin = culturalOrigin;
    }

    public String getHeight(){
        return height;
    }
    public void setHeight(String height){
        this.height = height;
    }
    public String getWidth(){
        return width;
    }
    public void setWidth(String width){
        this.width = width;
    }
    public String getDepth(){
        return depth;
    }
    public void setDepth(String depth){
        this.depth = depth;
    }


    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String getAcquisitionMethod() {
        return acquisitionMethod;
    }

    public void setAcquisitionMethod(String acquisitionMethod) {
        this.acquisitionMethod = acquisitionMethod;
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ArrayList<String> getLikedUsers() {
        return likedUsers;
    }

    public void setLikedUsers(ArrayList<String> likedUsers) {
        this.likedUsers = likedUsers;
    }

}
